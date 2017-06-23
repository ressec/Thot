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
package org.heliosphere.thot.akka.chat.lobby;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.heliosphere.thot.akka.chat.protocol.ChatMessageType;
import org.heliosphere.thot.akka.chat.protocol.data.ChatMessageData;
import org.heliosphere.thot.akka.chat.room.Room;

import com.heliosphere.athena.base.message.Message;
import com.heliosphere.athena.base.message.internal.IMessage;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Lobby extends AbstractActor
{
	/** 
	 * Akka logging adapter.
	 */
	private final LoggingAdapter LOG = Logging.getLogger(getContext().getSystem(), this);

	/**
	 * Locale for the lobby.
	 */
	private Locale locale = null;

	/**
	 *  Collection of {@link Room}.
	 */
	private Map<String, ActorRef> rooms = new HashMap<>();

	/**
	 * Static creation pattern for a {@link Lobby}.
	 * <hr>
	 * @param locale Locale for the {@link Lobby}.
	 * @return {@link Props}.
	 */
	public static Props props(final Locale locale)
	{
		return Props.create(Lobby.class, locale);
	}

	/**
	 * Creates a new {@link Lobby}.
	 * <hr>
	 * @param locale Locale for the lobby.
	 */
	public Lobby(final Locale locale)
	{
		this.locale = locale;
	}

	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
				.match(Message.class, message -> handleAndDispatchMessage(message))
				//.matchEquals("stopIt", p -> handleStop())
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
	 * Handles and dispatches incoming {@link Message}.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	private final void handleAndDispatchMessage(final IMessage message)
	{
		try
		{
			// Validate the message.
			message.validate();

			switch (message.getCategoryType())
			{
				case REQUEST:
					handleRequestMessage(message);
					break;

				case REPLY:
					//handleReplyMessage(message);
					break;

				case NOTIFICATION:
					//handleNotificationMessage(message);
					break;

				default:
					LOG.warning(this + " does not handle message category of type: " + message.getCategoryType());
					break;
			}
		}
		catch (Exception e)
		{
			// Notify the sender the execution has failed!
			getSender().tell(new Status.Failure(e), getSelf());
		}
	}

	/**
	 * Handles request {@link Message}.
	 * <hr>
	 * @param message Request message to process.
	 * @throws Exception Thrown in case an error occurred while processing a message.
	 */
	@SuppressWarnings("nls")
	private final void handleRequestMessage(final IMessage message) throws Exception
	{
		switch ((ChatMessageType) message.getType())
		{
			//			case LOBBY_ROOM_CREATE:
			//				handleRequestLobbyRoomCreate(message);
			//				break;
			//
			//			case LOBBY_ROOM_DELETE:
			//				break;
			//
			//			case LOBBY_ROOM_LIST:
			//				break;

			default:
				LOG.warning(this + " does not handle message (protocol) of type: " + message.getType());
				break;
		}
	}

	/**
	 * Handles request message for room creation.
	 * <hr>
	 * @param message {@link IMessage} to process.
	 * @throws Exception Thrown in case an error occurred while processing a message.
	 */
	@SuppressWarnings("nls")
	private final void handleRequestLobbyRoomCreate(final IMessage message) throws Exception
	{
		ChatMessageData content = (ChatMessageData) message.getContent();

		if (content.getRoomName() == null || content.getRoomName().isEmpty())
		{
			throw new LobbyException("Room name: " + content.getRoomName() + " cannot be null or empty!");
		}

		// Does this room name already registered?
		if (rooms.containsKey(content.getRoom()))
		{
			throw new LobbyException("Room: " + content.getRoomName() + " is already registered!");
		}

		// Create the room and register it.
		ActorRef room = getContext().actorOf(Room.props(content.getRoomName()), "room-");
		rooms.put(content.getRoomName(), room);

		// Send the client a confirmation message.
		content.setRoom(room);
		getSender().tell(Message.createReplyConfirmed(message, content), getSelf());
	}

}
