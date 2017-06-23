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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.heliosphere.thot.akka.chat.client.ChatClient;
import org.heliosphere.thot.akka.chat.lobby.Lobby;
import org.heliosphere.thot.akka.chat.protocol.ChatMessageType;
import org.heliosphere.thot.akka.chat.protocol.data.ChatMessageData;
import org.heliosphere.thot.akka.chat.user.UserException;

import com.heliosphere.athena.base.message.Message;
import com.heliosphere.athena.base.message.internal.IMessage;
import com.heliosphere.athena.base.message.protocol.data.DefaultMessageData;

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

	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
				.match(IMessage.class, message -> handleAndDispatchMessage(message))
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
		LOG.info(String.format("Received message [category=%1s, type=%2s, sender=%3s]", message.getCategoryType(), message.getType(), getSender()));

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
					LOG.warning("Does not handle message category: " + message.getCategoryType());
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
			case QUERY_SERVER_TIME:
				handleRequestSystemServerTime(message);
				break;

			case REGISTER_USER:
				handleRequestChatClientRegister(message);
				break;

			case QUERY_WHO:
			case STATUS_AFK:

			case NONE:
			default:
				LOG.warning("Does not handle message of type: " + message.getType());
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
		ChatMessageData content = (ChatMessageData) message.getContent();

		if (content.getUserName() == null || content.getUserName().isEmpty())
		{
			throw new UserException("User name cannot be null or empty!");
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
		ChatMessageData content = (ChatMessageData) message.getContent();

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

	/**
	 * Handles system message requesting to get the server time.
	 * <hr>
	 * @param message {@link IMessage} to process.
	 * @throws Exception Thrown in case an error occurred while processing a message.
	 */
	private final void handleRequestSystemServerTime(final IMessage message) throws Exception
	{
		DefaultMessageData data = new DefaultMessageData();
		data.setData(LocalTime.now().toString());
		IMessage reply = Message.createReplyConfirmed(message, data);
		getSender().tell(reply, getSelf());
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
