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

import java.util.HashMap;
import java.util.Map;

import org.h2.engine.User;
import org.heliosphere.thot.akka.chat.protocol.ChatMessageType;

import com.heliosphere.athena.base.message.Message;
import com.heliosphere.athena.base.message.internal.IMessage;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Room extends AbstractActor
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
	 * Static creation pattern for a {@link Room}.
	 * <hr>
	 * @param name {@link Room} name.
	 * @return {@link Props}.
	 */
	public static Props props(final String name)
	{
		return Props.create(Room.class, name);
	}

	/**
	 * Creates a new {@link Room}.
	 * <hr>
	 * @param name {@link Room} name.
	 */
	public Room(final String name)
	{
		this.name = name;
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
		switch ((ChatMessageType) message.getType())
		{
			//			case ROOM_JOIN:
			//				break;
			//
			//			case ROOM_LEAVE:
			//				break;
			//
			//			case ROOM_USER_LIST:
			//				break;
			//
			//			case ROOM_USER_GET:
			//				break;

			default:
				LOG.warning(this + " does not handle message (protocol) of type: " + message.getType());
				break;
		}
	}
}
