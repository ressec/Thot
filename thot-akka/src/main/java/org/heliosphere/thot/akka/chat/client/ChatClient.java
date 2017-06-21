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
package org.heliosphere.thot.akka.chat.client;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.heliosphere.thot.akka.chat.protocol.ChatMessageProtocolType;
import org.heliosphere.thot.akka.chat.protocol.LobbyMessageProtocolType;

import com.heliosphere.athena.base.message.internal.IMessage;
import com.heliosphere.athena.base.message.internal.Message;
import com.heliosphere.athena.base.message.internal.MessageException;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ChatClient extends AbstractActor
{
	/** 
	 * Akka logging adapter.
	 */
	private final LoggingAdapter LOG = Logging.getLogger(getContext().getSystem(), this);

	/**
	 * User name.
	 */
	private String username;

	private Map<Locale, ActorRef> roomManagers = new HashMap();

	private Map<String, ActorRef> rooms = new HashMap();

	/**
	 * Is the user name registered?
	 */
	private boolean isRegistered = false;

	/**
	 * User actor (at the other end of the connection).
	 */
	private ActorRef user = null;

	/**
	 * Static User creational pattern.
	 * <hr>
	 * @param username User name.
	 * @return {@link Props}.
	 */
	public static Props props(final String username)
	{
		return Props.create(ChatClient.class, username);

	}

	/**
	 * Creates a new client.
	 * <hr>
	 * @param username User name.
	 */
	public ChatClient(final String username)
	{
		this.username = username;
	}

	@SuppressWarnings("nls")
	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
				.match(Status.Failure.class, message -> handleFailure(message))
				.match(Message.class, message -> handleAndDispatchMessage(message))
				//.matchEquals("stopIt", p -> handleStop())
				.matchAny(message -> handleUnknownMessage(message))
				.build();
	}

	@SuppressWarnings("nls")
	public final void handleUnknownMessage(Object message)
	{
		LOG.info("Received an unknown message: " + message);
	}

	@SuppressWarnings("nls")
	public final void handleFailure(final Status.Failure message)
	{
		LOG.error("Failure due to: " + message.cause().getMessage());
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
					handleReplyMessage(message);
					break;

				case NOTIFICATION:
					//handleNotificationChatMessage(message);
					break;

				default:
					LOG.warning(this + " does not handle message category of type: " + message.getCategoryType());
					break;
			}
		}
		catch (MessageException me)
		{
			getSender().tell(new Status.Failure(me), getSelf());
		}
	}

	/**
	 * Handles request {@link Message} of type {@link ChatMessageProtocolType}.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	private final void handleRequestMessage(final IMessage message)
	{
		if (message.getType() instanceof ChatMessageProtocolType)
		{
			switch ((ChatMessageProtocolType) message.getType())
			{
				case CHAT_CLIENT_REGISTER:
					break;

				case CHAT_CLIENT_UNREGISTER:
					break;

				case CHAT_LOBBY_LIST:
					break;

				case CHAT_LOBBY_CREATE:
					break;

				case CHAT_LOBBY_DELETE:
					break;

				default:
					LOG.warning(this + " does not handle message (protocol) of type: " + message.getType());
					break;
			}
		}

		if (message.getType() instanceof LobbyMessageProtocolType)
		{
			switch ((LobbyMessageProtocolType) message.getType())
			{
				case LOBBY_ROOM_CREATE:
					break;

				case LOBBY_ROOM_DELETE:
					break;

				case LOBBY_ROOM_LIST:
					break;

				default:
					LOG.warning(this + " does not handle message (protocol) of type: " + message.getType());
					break;
			}
		}
	}

	/**
	 * Handles reply {@link Message} of type {@link ChatMessageProtocolType}.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	private final void handleReplyMessage(final IMessage message)
	{
		if (message.getType() instanceof ChatMessageProtocolType)
		{
			switch ((ChatMessageProtocolType) message.getType())
			{
				case CHAT_CLIENT_REGISTER:
					handleReplyGeneric(message);
					break;

				case CHAT_CLIENT_UNREGISTER:
					break;

				case CHAT_LOBBY_LIST:
					break;

				case CHAT_LOBBY_CREATE:
					handleReplyGeneric(message);
					break;

				case CHAT_LOBBY_DELETE:
					break;

				default:
					LOG.warning(this + " does not handle message (protocol) of type: " + message.getType());
					break;
			}
		}

		if (message.getType() instanceof LobbyMessageProtocolType)
		{
			switch ((LobbyMessageProtocolType) message.getType())
			{
				case LOBBY_ROOM_CREATE:
					handleReplyGeneric(message);
					break;

				case LOBBY_ROOM_DELETE:
					handleReplyGeneric(message);
					break;

				case LOBBY_ROOM_LIST:
					handleReplyGeneric(message);
					break;

				default:
					LOG.warning(this + " does not handle message (protocol) of type: " + message.getType());
					break;
			}
		}
	}

	/**
	 * Handles reply {@link Message} independently of the message type.
	 * <hr>
	 * @param message Message to process.
	 */
	@SuppressWarnings("nls")
	private final void handleReplyGeneric(final IMessage message)
	{
		switch (message.getResponseType())
		{
			case CONFIRMED:
				LOG.info(String.format("Received confirmation reply for message: %1s", message.getType()));
				isRegistered = true;
				break;

			case REJECTED:

			default:
				break;
		}
	}

}
