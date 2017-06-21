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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.heliosphere.thot.akka.chat.client.ChatClient;
import org.heliosphere.thot.akka.chat.lobby.Lobby;
import org.heliosphere.thot.akka.chat.protocol.ChatData;
import org.heliosphere.thot.akka.chat.protocol.ChatMessageProtocolType;
import org.heliosphere.thot.akka.chat.user.UserException;

import com.heliosphere.athena.base.message.internal.IMessage;
import com.heliosphere.athena.base.message.internal.Message;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Status;
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
	 * Collection of registered {@link ChatClient} users.
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

	@SuppressWarnings("nls")
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
		switch ((ChatMessageProtocolType) message.getType())
		{
			case CHAT_CLIENT_REGISTER:
				handleRequestChatClientRegister(message);
				break;

			case CHAT_CLIENT_UNREGISTER:
				break;

			case CHAT_LOBBY_LIST:
				break;

			case CHAT_LOBBY_CREATE:
				handleRequestChatLobbyCreation(message);
				break;

			case CHAT_LOBBY_DELETE:
				break;

			default:
				LOG.warning(this + " does not handle message (protocol) of type: " + message.getType());
				break;
		}
	}

	/**
	 * Handles chat request message for client registration.
	 * <hr>
	 * @param message {@link IMessage} to process.
	 * @throws Exception Thrown in case an error occurred while processing a message.
	 */
	@SuppressWarnings("nls")
	private final void handleRequestChatClientRegister(final IMessage message) throws Exception
	{
		ChatData content = (ChatData) message.getContent();

		if (content.getUserName() == null || content.getUserName().isEmpty())
		{
			throw new UserException("User: " + content.getUserName() + " cannot be null or empty!");
		}

		// Does this client's user name already registered?
		if (clients.containsKey(content.getUserName()))
		{
			throw new UserException("User: " + content.getUserName() + " is already registered!");
		}

		// Register the client and the user name.
		clients.put(content.getUserName(), getSender());

		// Send the client a confirmation message.
		getSender().tell(Message.createReplyConfirmed(message), getSelf());
	}

	/**
	 * Handles chat request message for lobby creation.
	 * <hr>
	 * @param message {@link IMessage} to process.
	 * @throws Exception Thrown in case an error occurred while processing a message.
	 */
	@SuppressWarnings("nls")
	private final void handleRequestChatLobbyCreation(final IMessage message) throws Exception
	{
		ChatData content = (ChatData) message.getContent();

		if (content.getLocale() == null)
		{
			throw new UserException(String.format("For message of type: %1s, the 'locale' must be provided!", message.getType()));
		}

		if (lobbies.containsKey(content.getLocale()))
		{
			throw new UserException(String.format("Lobby for locale: %1s already exist!", content.getLocale()));
		}

		// Create the lobby and register it.
		ActorRef lobby = getContext().actorOf(Lobby.props(content.getLocale()), "lobby-");
		lobbies.put(content.getLocale(), lobby);

		// Send the client a confirmation message and inject the lobby reference.
		content.setLobby(lobby);
		getSender().tell(Message.createReplyConfirmed(message, content), getSelf());
	}
}
