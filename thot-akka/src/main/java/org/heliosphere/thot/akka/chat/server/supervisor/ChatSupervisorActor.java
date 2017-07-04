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
package org.heliosphere.thot.akka.chat.server.supervisor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.heliosphere.thot.akka.chat.server.lobby.LobbyActor;
import org.heliosphere.thot.akka.chat.server.lobby.LobbyException;
import org.heliosphere.thot.akka.chat.server.lobby.LobbyMessageProtocol;
import org.heliosphere.thot.akka.chat.server.user.UserException;

import com.heliosphere.athena.base.message.Message;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ChatSupervisorActor extends AbstractActor
{
	/** 
	 * Akka logging adapter.
	 */
	private final LoggingAdapter LOG = Logging.getLogger(getContext().getSystem(), this);

	/**
	 *  Collection of {@link LobbyActor}.
	 */
	private Map<Locale, ActorRef> lobbies = new HashMap<>();

	/**
	 * Collection of registered {@link ChatClientActor} users.
	 */
	private Map<String, ActorRef> clients = new HashMap<>();

	/**
	 * Static creation pattern for a {@link ChatSupervisorActor}.
	 * <hr>
	 * @return {@link Props}.
	 */
	public static Props props()
	{
		return Props.create(ChatSupervisorActor.class);
	}

	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
				.match(ChatSupervisorProtocol.InitiateConversation.class, message -> getSender().tell(new ChatSupervisorProtocol.ConversationInitiated(), getSelf()))
				.match(ChatSupervisorProtocol.RegisterUser.class, message -> handleRegisterUser(message))
				.match(LobbyMessageProtocol.LobbyList.class, message -> handleLobbyList(message))
				.match(LobbyMessageProtocol.LobbyCreate.class, message -> handleLobbyCreate(message))
				.match(LobbyMessageProtocol.LobbyDelete.class, message -> handleLobbyDelete(message))
				.match(LobbyMessageProtocol.LobbyJoin.class, message -> handleLobbyJoin(message))
				//.match(IMessage.class, message -> handleAndDispatchMessage(message))
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

	//	/**
	//	 * Handles and dispatches incoming {@link Message}.
	//	 * <hr>
	//	 * @param message Message to process.
	//	 */
	//	@SuppressWarnings("nls")
	//	private final void handleAndDispatchMessage(final IMessage message)
	//	{
	//		LOG.info(String.format("Received message [category=%1s, type=%2s, sender=%3s]", message.getCategoryType(), message.getType(), getSender()));
	//
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
	//					LOG.warning("Does not handle message category: " + message.getCategoryType());
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
	//			case QUERY_SERVER_TIME:
	//				break;
	//
	//			case REGISTER_USER:
	//				//handleRequestChatClientRegister(message);
	//				break;
	//
	//			case QUERY_WHO:
	//			case STATUS_AFK:
	//
	//			case NONE:
	//			default:
	//				LOG.warning("Does not handle message of type: " + message.getType());
	//				break;
	//		}
	//	}

	/**
	 * Handles {@link ChatSupervisorProtocol.RegisterUser} message.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	private final void handleRegisterUser(final ChatSupervisorProtocol.RegisterUser message)
	{
		if (message.getUser() == null || message.getUser().isEmpty())
		{
			getSender().tell(new Status.Failure(new UserException("User (name) cannot be null or empty!")), getSelf());
		}
		else if (clients.containsKey(message.getUser()))
		{
			// Does this client's user name already registered?
			getSender().tell(new Status.Failure(new UserException("User: " + message.getUser() + " is already registered!")), getSelf());
		}
		else
		{
			// Register the client and the user name.
			clients.put(message.getUser(), getSender());

			// Confirm the user has been registered.
			getSender().tell(new ChatSupervisorProtocol.UserRegistered(message.getUser()), getSelf());
		}
	}

	/**
	 * Handles {@link org.heliosphere.thot.akka.chat.server.lobby.LobbyMessageProtocol.LobbyList} message.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	private final void handleLobbyList(final LobbyMessageProtocol.LobbyList message)
	{
		if (message.getUser() == null || message.getUser().isEmpty())
		{
			getSender().tell(new Status.Failure(new LobbyException("User (name) cannot be null or empty!")), getSelf());
		}
		else if (clients.get(message.getUser()) == null)
		{
			getSender().tell(new Status.Failure(new LobbyException(String.format("User: %1s is not registered!", message.getUser()))), getSelf());
		}
		else
		{
			// Confirm the user has been registered.
			getSender().tell(new LobbyMessageProtocol.LobbyList(message.getUser(), new ArrayList<>(lobbies.keySet())), getSelf());
		}
	}

	/**
	 * Handles {@link org.heliosphere.thot.akka.chat.server.lobby.LobbyMessageProtocol.LobbyCreate} message.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	private final void handleLobbyCreate(final LobbyMessageProtocol.LobbyCreate message)
	{
		if (message.getLobby() == null)
		{
			getSender().tell(new Status.Failure(new UserException("Lobby (locale) cannot be null or empty!")), getSelf());
		}
		else if (lobbies.containsKey(message.getLobby()))
		{
			getSender().tell(new Status.Failure(new UserException(String.format("Lobby for locale: %1s already exist!", message.getLobby()))), getSelf());
		}
		else
		{
			// Create the lobby and register it.
			ActorRef lobby = getContext().actorOf(LobbyActor.props(message.getLobby()), "lobby-" + message.getLobby().toString());
			lobbies.put(message.getLobby(), lobby);

			// Send the client a confirmation message and inject the lobby reference.
			getSender().tell(new LobbyMessageProtocol.LobbyCreated(message.getLobby()), getSelf());
		}
	}

	/**
	 * Handles {@link org.heliosphere.thot.akka.chat.server.lobby.LobbyMessageProtocol.LobbyDelete} message.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	private final void handleLobbyDelete(final LobbyMessageProtocol.LobbyDelete message)
	{
		if (message.getLobby() == null)
		{
			getSender().tell(new Status.Failure(new UserException("Lobby (locale) cannot be null or empty!")), getSelf());
		}
		else if (!lobbies.containsKey(message.getLobby()))
		{
			getSender().tell(new Status.Failure(new UserException(String.format("Lobby for locale: %1s does not exist!", message.getLobby()))), getSelf());
		}
		else
		{
			// Stop the given lobby.
			ActorRef lobby = lobbies.get(message.getLobby());
			getContext().stop(lobby);

			// Remove this lobby from the collection.
			lobbies.remove(message.getLobby());

			// Send the client a confirmation message.
			getSender().tell(new LobbyMessageProtocol.LobbyDeleted(message.getLobby()), getSelf());
		}
	}

	/**
	 * Handles {@link org.heliosphere.thot.akka.chat.server.lobby.LobbyMessageProtocol.LobbyJoin} message.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	private final void handleLobbyJoin(final LobbyMessageProtocol.LobbyJoin message)
	{
		if (message.getLobby() == null)
		{
			getSender().tell(new Status.Failure(new LobbyException("Lobby (locale) cannot be null or empty!")), getSelf());
		}
		else if (!lobbies.containsKey(message.getLobby()))
		{
			getSender().tell(new Status.Failure(new LobbyException(String.format("Lobby: lobby-%1s does not exist!", message.getLobby()))), getSelf());
		}
		else
		{
			// Forward the message to the lobby.
			ActorRef lobby = lobbies.get(message.getLobby());
			lobby.forward(message, getContext());
		}
	}

	@SuppressWarnings("nls")
	@Override
	public final void postStop() throws Exception
	{
		super.postStop();

		LOG.info("Has been stopped!");
	}

	@SuppressWarnings("nls")
	@Override
	public void preStart() throws Exception
	{
		super.preStart();

		LOG.info("Has been started!");
	}
}
