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
package org.heliosphere.thot.akka.chat.server.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.heliosphere.thot.akka.chat.server.room.RoomActor;
import org.heliosphere.thot.akka.chat.server.room.RoomException;
import org.heliosphere.thot.akka.chat.server.room.RoomMessageProtocol;

import com.heliosphere.athena.base.message.Message;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class LobbyActor extends AbstractActor
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
	 *  Collection of {@link RoomActor}.
	 */
	private Map<String, ActorRef> rooms = new HashMap<>();

	/**
	 * Connected users.
	 */
	private Map<String, ActorRef> users = new HashMap<>();

	/**
	 * Static creation pattern for a {@link LobbyActor}.
	 * <hr>
	 * @param locale Locale for the {@link LobbyActor}.
	 * @return {@link Props}.
	 */
	public static Props props(final Locale locale)
	{
		return Props.create(LobbyActor.class, locale);
	}

	/**
	 * Creates a new {@link LobbyActor}.
	 * <hr>
	 * @param locale Locale for the lobby.
	 */
	public LobbyActor(final Locale locale)
	{
		this.locale = locale;
	}

	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
				//.match(Message.class, message -> handleAndDispatchMessage(message))
				.match(LobbyMessageProtocol.LobbyJoin.class, message -> handleLobbyJoin(message))
				.match(LobbyMessageProtocol.LobbyLeave.class, message -> handleLobbyLeave(message))
				.match(RoomMessageProtocol.RoomCreate.class, message -> handleRoomCreate(message))
				.match(RoomMessageProtocol.RoomDelete.class, message -> handleRoomDelete(message))
				.match(RoomMessageProtocol.RoomList.class, message -> handleRoomList(message))
				.match(RoomMessageProtocol.RoomJoin.class, message -> handleRoomJoin(message))
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
	 * Handles {@link LobbyMessageProtocol.LobbyJoin} message.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	private final void handleLobbyJoin(final LobbyMessageProtocol.LobbyJoin message)
	{
		// User already connected?
		if (users.get(message.getUser()) != null)
		{
			getSender().tell(new Status.Failure(new LobbyException(String.format("User: %1s is already connected to lobby: lobby-%2s", message.getUser(), message.getLobby().toString()))), getSelf());
		}
		else
		{
			users.put(message.getUser(), getSender());
			getSender().tell(new LobbyMessageProtocol.LobbyJoined(message.getUser(), message.getLobby()), getSelf());
		}
	}

	/**
	 * Handles {@link LobbyMessageProtocol.LobbyLeave} message.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	private final void handleLobbyLeave(final LobbyMessageProtocol.LobbyLeave message)
	{
		// User already connected?
		if (users.get(message.getUser()) == null)
		{
			getSender().tell(new Status.Failure(new LobbyException(String.format("No user: %1s connected to lobby: lobby-%2s", message.getUser(), message.getLobby().toString()))), getSelf());
		}
		else
		{
			getSender().tell(new LobbyMessageProtocol.LobbyLeft(message.getUser(), message.getLobby()), getSelf());
		}
	}

	/**
	 * Handles {@link org.heliosphere.thot.akka.chat.supervisor.ChatSupervisorProtocol.RoomCreate} message.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	private final void handleRoomCreate(final RoomMessageProtocol.RoomCreate message)
	{
		if (message.getRoom() == null)
		{
			getSender().tell(new Status.Failure(new RoomException("Room (name) cannot be null or empty!")), getSelf());
		}
		else if (rooms.containsKey(message.getRoom()))
		{
			getSender().tell(new Status.Failure(new RoomException(String.format("Room name: %1s already exist!", message.getRoom()))), getSelf());
		}
		else
		{
			ActorRef room = getContext().actorOf(RoomActor.props(message.getRoom()), "room-" + message.getRoom());
			rooms.put(message.getRoom(), room);
			getSender().tell(new RoomMessageProtocol.RoomCreated(message.getUser(), message.getRoom()), getSelf());
		}
	}

	/**
	 * Handles a {@code room delete} message.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	private final void handleRoomDelete(final RoomMessageProtocol.RoomDelete message)
	{
		if (message.getRoom() == null)
		{
			getSender().tell(new Status.Failure(new RoomException("Room (name) cannot be null or empty!")), getSelf());
		}
		else if (!rooms.containsKey(message.getRoom()))
		{
			getSender().tell(new Status.Failure(new RoomException(String.format("Room name: %1s does not exist!", message.getRoom()))), getSelf());
		}
		else
		{
			rooms.get(message.getRoom()).tell(new RoomMessageProtocol.RoomDelete(message.getUser(), message.getRoom()), getSelf());
		}
	}

	/**
	 * Handles a {@code room list} message.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	private final void handleRoomList(final RoomMessageProtocol.RoomList message)
	{
		if (message.getUser() == null || message.getUser().isEmpty())
		{
			getSender().tell(new Status.Failure(new LobbyException("User (name) cannot be null or empty!")), getSelf());
		}
		else if (users.get(message.getUser()) == null)
		{
			getSender().tell(new Status.Failure(new LobbyException(String.format("User: %1s is not connected to lobby: lobby-%2s", message.getUser(), locale.toString()))), getSelf());
		}
		else
		{
			// Returns the rooms.
			getSender().tell(new RoomMessageProtocol.RoomList(message.getUser(), message.getLobby(), new ArrayList<>(rooms.keySet())), getSelf());
		}
	}

	/**
	 * Handles a {@code room join} message.
	 * <hr>
	 * @param message Message to process.
	 * @throws Exception Thrown in case an error occurred while processing a message.
	 */
	@SuppressWarnings("nls")
	private final void handleRoomJoin(final RoomMessageProtocol.RoomJoin message) throws Exception
	{
		if (message.getRoom() == null)
		{
			getSender().tell(new Status.Failure(new RoomException("Room (name) cannot be null or empty!")), getSelf());
		}
		else if (!rooms.containsKey(message.getRoom()))
		{
			getSender().tell(new Status.Failure(new RoomException(String.format("Room name: %1s does not exist!", message.getRoom()))), getSelf());
		}
		else
		{
			// Forward the message to the room.
			ActorRef room = rooms.get(message.getRoom());
			room.forward(message, getContext());
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
	//			//			case LOBBY_ROOM_CREATE:
	//			//				handleRequestLobbyRoomCreate(message);
	//			//				break;
	//			//
	//			//			case LOBBY_ROOM_DELETE:
	//			//				break;
	//			//
	//			//			case LOBBY_ROOM_LIST:
	//			//				break;
	//
	//			default:
	//				LOG.warning(this + " does not handle message (protocol) of type: " + message.getType());
	//				break;
	//		}
	//	}

	//	/**
	//	 * Handles request message for room creation.
	//	 * <hr>
	//	 * @param message {@link IMessage} to process.
	//	 * @throws Exception Thrown in case an error occurred while processing a message.
	//	 */
	//	@SuppressWarnings("nls")
	//	private final void handleRequestLobbyRoomCreate(final IMessage message) throws Exception
	//	{
	//		ChatMessageData content = (ChatMessageData) message.getContent();
	//
	//		if (content.getRoomName() == null || content.getRoomName().isEmpty())
	//		{
	//			throw new LobbyException("Room name: " + content.getRoomName() + " cannot be null or empty!");
	//		}
	//
	//		// Does this room name already registered?
	//		if (rooms.containsKey(content.getRoom()))
	//		{
	//			throw new LobbyException("Room: " + content.getRoomName() + " is already registered!");
	//		}
	//
	//		// Create the room and register it.
	//		ActorRef room = getContext().actorOf(Room.props(content.getRoomName()), "room-");
	//		rooms.put(content.getRoomName(), room);
	//
	//		// Send the client a confirmation message.
	//		content.setRoom(room);
	//		getSender().tell(Message.createReplyConfirmed(message, content), getSelf());
	//	}

}
