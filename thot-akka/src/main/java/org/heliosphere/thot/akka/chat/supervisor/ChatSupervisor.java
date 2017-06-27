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
package org.heliosphere.thot.akka.chat.supervisor;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.heliosphere.thot.akka.chat.client.ChatClientActor;
import org.heliosphere.thot.akka.chat.lobby.Lobby;
import org.heliosphere.thot.akka.chat.lobby.LobbyException;
import org.heliosphere.thot.akka.chat.user.UserException;

import com.heliosphere.athena.base.message.Message;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ChatSupervisor extends AbstractActor
{
	/** 
	 * Akka logging adapter.
	 */
	private final LoggingAdapter LOG = Logging.getLogger(getContext().getSystem(), this);

	/**
	 *  Collection of {@link Lobby}.
	 */
	private Map<Locale, ActorRef> lobbies = new HashMap<>();

	/**
	 * Collection of registered {@link ChatClientActor} users.
	 */
	private Map<String, ActorRef> clients = new HashMap<>();

	/**
	 * Static creation pattern for a {@link ChatSupervisor}.
	 * <hr>
	 * @return {@link Props}.
	 */
	public static Props props()
	{
		return Props.create(ChatSupervisor.class);
	}

	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
				.match(ChatSupervisorProtocol.QueryServerTime.class, message -> getSender().tell(new ChatSupervisorProtocol.QueryServerTime(LocalTime.now().toString()), getSelf()))
				.match(ChatSupervisorProtocol.RegisterUser.class, message -> handleRegisterUser(message))
				.match(ChatSupervisorProtocol.LobbyList.class, message -> handleLobbyList(message))
				.match(ChatSupervisorProtocol.LobbyCreate.class, message -> handleLobbyCreate(message))
				.match(ChatSupervisorProtocol.LobbyDelete.class, message -> handleLobbyDelete(message))
				.match(ChatSupervisorProtocol.LobbyConnect.class, message -> handleLobbyConnect(message))
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
	 * @throws Exception Thrown in case an error occurred while processing a message.
	 */
	@SuppressWarnings("nls")
	private final void handleRegisterUser(final ChatSupervisorProtocol.RegisterUser message) throws Exception
	{
		if (message.getUser() == null || message.getUser().isEmpty())
		{
			throw new UserException("User (name) cannot be null or empty!");
		}

		// Does this client's user name already registered?
		if (clients.containsKey(message.getUser()))
		{
			throw new UserException("User: " + message.getUser() + " is already registered!");
		}

		// Register the client and the user name.
		clients.put(message.getUser(), getSender());

		// Confirm the user has been registered.
		getSender().tell(new ChatSupervisorProtocol.UserRegistered(message.getUser()), getSelf());
	}

	/**
	 * Handles {@link ChatSupervisorProtocol.LobbyList} message.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	private final void handleLobbyList(final ChatSupervisorProtocol.LobbyList message)
	{
		if (message.getUser() == null || message.getUser().isEmpty())
		{
			getSender().tell(new LobbyException("User (name) cannot be null or empty!"), getSelf());
		}
		else if (clients.get(message.getUser()) == null)
		{
			getSender().tell(new LobbyException(String.format("User: %1s is not registered!", message.getUser())), getSelf());
		}
		else
		{
			// Confirm the user has been registered.
			getSender().tell(new ChatSupervisorProtocol.LobbyList(message.getUser(), new ArrayList<>(lobbies.keySet())), getSelf());
		}
	}

	/**
	 * Handles {@link ChatSupervisorProtocol.LobbyCreate} message.
	 * <hr>
	 * @param message Message to process.
	 * @throws Exception Thrown in case an error occurred while processing a message.
	 */
	@SuppressWarnings("nls")
	private final void handleLobbyCreate(final ChatSupervisorProtocol.LobbyCreate message) throws Exception
	{
		if (message.getLobby() == null)
		{
			throw new UserException("Lobby (locale) cannot be null or empty!");
		}

		if (lobbies.containsKey(message.getLobby()))
		{
			throw new UserException(String.format("Lobby for locale: %1s already exist!", message.getLobby()));
		}

		// Create the lobby and register it.
		ActorRef lobby = getContext().actorOf(Lobby.props(message.getLobby()), "lobby-" + message.getLobby().toString());
		lobbies.put(message.getLobby(), lobby);

		// Send the client a confirmation message and inject the lobby reference.
		getSender().tell(new ChatSupervisorProtocol.LobbyCreated(message.getLobby()), getSelf());
	}

	/**
	 * Handles {@link ChatSupervisorProtocol.LobbyDelete} message.
	 * <hr>
	 * @param message Message to process.
	 * @throws Exception Thrown in case an error occurred while processing a message.
	 */
	@SuppressWarnings("nls")
	private final void handleLobbyDelete(final ChatSupervisorProtocol.LobbyDelete message) throws Exception
	{
		if (message.getLobby() == null)
		{
			throw new UserException("Lobby (locale) cannot be null or empty!");
		}

		if (!lobbies.containsKey(message.getLobby()))
		{
			throw new UserException(String.format("Lobby for locale: %1s does not exist!", message.getLobby()));
		}

		// Stop the given lobby.
		ActorRef lobby = lobbies.get(message.getLobby());
		getContext().stop(lobby);

		// Remove this lobby from the collection.
		lobbies.remove(message.getLobby());

		// Send the client a confirmation message.
		getSender().tell(new ChatSupervisorProtocol.LobbyDeleted(message.getLobby()), getSelf());
	}

	/**
	 * Handles {@link ChatSupervisorProtocol.LobbyConnect} message.
	 * <hr>
	 * @param message Message to process.
	 * @throws Exception Thrown in case an error occurred while processing a message.
	 */
	@SuppressWarnings("nls")
	private final void handleLobbyConnect(final ChatSupervisorProtocol.LobbyConnect message) throws Exception
	{
		if (message.getLobby() == null)
		{
			getSender().tell(new LobbyException("Lobby (locale) cannot be null or empty!"), getSelf());
		}

		if (!lobbies.containsKey(message.getLobby()))
		{
			getSender().tell(new LobbyException(String.format("Lobby: lobby-%1s does not exist!", message.getLobby())), getSelf());
		}

		// Forward the message to the lobby.
		ActorRef lobby = lobbies.get(message.getLobby());
		lobby.forward(message, getContext());
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
