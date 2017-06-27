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

import org.heliosphere.thot.akka.chat.client.TerminalActor;

import com.heliosphere.athena.base.message.internal.IMessageProtocol;

public class RoomMessageProtocol implements IMessageProtocol
{
	/**
	 * Default serialization identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Message send by a {@link TerminalActor} to a {@link RoomActor} when a user sends a message to a room.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class Say
	{
		/**
		 * Message.
		 */
		private final String message;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param message Message.
		 */
		public Say(String message)
		{
			this.message = message;
		}

		/**
		 * Returns the message.
		 * <hr>
		 * @return Message.
		 */
		public final String getMessage()
		{
			return message;
		}
	}

	/**
	 * Message send by a {@link TerminalActor} to a {@link RoomActor} when a user sends a message to another user in a room.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class Whisper
	{
		/**
		 * User.
		 */
		private final String user;

		/**
		 * Message.
		 */
		private final String message;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User name.
		 * @param message Message.
		 */
		public Whisper(String user, String message)
		{
			this.user = user;
			this.message = message;
		}

		/**
		 * Returns the user.
		 * <hr>
		 * @return User name.
		 */
		public final String getUser()
		{
			return user;
		}

		/**
		 * Returns the message.
		 * <hr>
		 * @return Message.
		 */
		public final String getMessage()
		{
			return message;
		}
	}
}
