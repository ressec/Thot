/*
 * Copyright(c) 2017 - Heliosphere Corp.
 * ---------------------------------------------------------------------------
 * This file is part of the Heliosphere's project which is licensed under the 
 * Apache license version 2 and use is subject to license terms.
 * You should have received a copy of the license with the project's artifact
 * binaries and/or sources.
 * 
 * License can be consulted at http://www.apache.org/licenses/LICENSE-2.0
 * ---------------------------------------------------------------------------
 */
package org.heliosphere.thot.akka.chat.room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.h2.engine.User;
import org.heliosphere.thot.akka.chat.user.UserMessageProtocol;

import com.heliosphere.athena.base.message.Message;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class RoomActor extends AbstractActor
{
	/** 
	 * Akka logging adapter.
	 */
	private final LoggingAdapter LOG = Logging.getLogger(getContext().getSystem(), this);

	/**
	 * Room name.
	 */
	private String name = null;

	/**
	 *  Collection of {@link User}.
	 */
	private Map<String, ActorRef> users = new HashMap<>();

	/**
	 * Static creation pattern for a {@link RoomActor}.
	 * <hr>
	 * @param name {@link RoomActor} name.
	 * @return {@link Props}.
	 */
	public static Props props(final String name)
	{
		return Props.create(RoomActor.class, name);
	}

	/**
	 * Creates a new {@link RoomActor}.
	 * <hr>
	 * @param name {@link RoomActor} name.
	 */
	public RoomActor(final String name)
	{
		this.name = name;
	}

	@SuppressWarnings("nls")
	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
				//.match(Message.class, message -> handleAndDispatchMessage(message))
				//.matchEquals("stopIt", p -> handleStop())
				.match(RoomMessageProtocol.RoomJoin.class, message -> handleRoomJoin(message))
				.match(RoomMessageProtocol.RoomLeave.class, message -> handleRoomLeave(message))
				.match(UserMessageProtocol.UserList.class, message -> handleUserList(message))
				.match(UserMessageProtocol.Say.class, message -> handleSay(message))
				.match(UserMessageProtocol.Whisper.class, message -> handleWhisper(message))
				.matchAny(message -> handleUnknownMessage(message))
				.build();
	}

	/**
	 * Handles unknown {@link Message} received.
	 * <hr>
	 * @param message Message.
	 */
	@SuppressWarnings("nls")
	private final void handleUnknownMessage(Object message)
	{
		LOG.info("Received an unknown message: " + message);
	}

	/**
	 * Handles {@link org.heliosphere.thot.akka.chat.supervisor.ChatSupervisorProtocol.RoomJoin} message.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	private final void handleRoomJoin(final RoomMessageProtocol.RoomJoin message)
	{
		if (users.containsKey(message.getUser()))
		{
			getSender().tell(new Status.Failure(new RoomException(String.format("User: %1s has already joined room: %2s!", message.getUser(), name))), getSelf());
		}
		else
		{
			users.put(message.getUser(), getSender());
			getSender().tell(new RoomMessageProtocol.RoomJoined(message.getUser(), message.getRoom()), getSelf());
		}
	}

	/**
	 * Handles {@link org.heliosphere.thot.akka.chat.supervisor.ChatSupervisorProtocol.RoomLeave} message.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	private final void handleRoomLeave(final RoomMessageProtocol.RoomLeave message)
	{
		if (!users.containsKey(message.getUser()))
		{
			getSender().tell(new Status.Failure(new RoomException(String.format("User: %1s is not a member of room: %2s!", message.getUser(), name))), getSelf());
		}
		else
		{
			users.remove(message.getUser());
			getSender().tell(new RoomMessageProtocol.RoomLeft(message.getUser(), message.getRoom()), getSelf());
		}
	}

	/**
	 * Handles {@link org.heliosphere.thot.akka.chat.supervisor.UserMessageProtocol.UserList} message.
	 * <hr>
	 * @param message Message to process.
	 */
	private final void handleUserList(final UserMessageProtocol.UserList message)
	{
		getSender().tell(new UserMessageProtocol.UserList(message.getRoom(), new ArrayList<>(users.keySet())), getSelf());
	}

	/**
	 * Handles a {@code say} message.
	 * <hr>
	 * @param message Message to process.
	 */
	private final void handleSay(final UserMessageProtocol.Say message)
	{
		// Dispatch the message to all users in the room.
		for (ActorRef user : users.values())
		{
			user.tell(new UserMessageProtocol.Said(message.getUser(), message.getMessage()), getSelf());
		}
	}

	/**
	 * Handles a {@code whisper} message.
	 * <hr>
	 * @param message Message to process.
	 */
	private final void handleWhisper(final UserMessageProtocol.Whisper message)
	{
		// Dispatch the message to the recipient user in the room.
		ActorRef recipient = users.get(message.getRecipient());

		// Dispatch the message to the recipient user in the room.
		ActorRef sender = users.get(message.getSender());

		if (recipient != null)
		{
			recipient.tell(new UserMessageProtocol.Whispered(message.getRecipient(), message.getMessage(), message.getSender()), getSelf());
		}

		if (sender != null)
		{
			sender.tell(new UserMessageProtocol.Whispered(message.getRecipient(), message.getMessage(), message.getSender()), getSelf());
		}
	}

	//	/**
	//	 * Handles and dispatches incoming {@link Message}.
	//	 * <hr>
	//	 * @param message Message to process.
	//	 */
	//	@SuppressWarnings("nls")
	//	private final void handleAndDispatchMessage(final IMessage message)
	//	{
	//		try
	//		{
	//			// Validate the message.
	//			message.validate();
	//
	//			switch (message.getCategoryType())
	//			{
	//				case REQUEST:
	//					handleRequestMessage(message);
	//					break;
	//
	//				case REPLY:
	//					//handleReplyMessage(message);
	//					break;
	//
	//				case NOTIFICATION:
	//					//handleNotificationMessage(message);
	//					break;
	//
	//				default:
	//					LOG.warning(this + " does not handle message category of type: " + message.getCategoryType());
	//					break;
	//			}
	//		}
	//		catch (Exception e)
	//		{
	//			// Notify the sender the execution has failed!
	//			getSender().tell(new Status.Failure(e), getSelf());
	//		}
	//	}

	//	/**
	//	 * Handles request {@link Message}.
	//	 * <hr>
	//	 * @param message Request message to process.
	//	 * @throws Exception Thrown in case an error occurred while processing a message.
	//	 */
	//	@SuppressWarnings("nls")
	//	private final void handleRequestMessage(final IMessage message) throws Exception
	//	{
	//		switch ((ChatMessageType) message.getType())
	//		{
	//			//			case ROOM_JOIN:
	//			//				break;
	//			//
	//			//			case ROOM_LEAVE:
	//			//				break;
	//			//
	//			//			case ROOM_USER_LIST:
	//			//				break;
	//			//
	//			//			case ROOM_USER_GET:
	//			//				break;
	//
	//			default:
	//				LOG.warning(this + " does not handle message (protocol) of type: " + message.getType());
	//				break;
	//		}
	//	}
}
