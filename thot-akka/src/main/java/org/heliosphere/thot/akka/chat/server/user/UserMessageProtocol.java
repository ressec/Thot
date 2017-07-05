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
package org.heliosphere.thot.akka.chat.server.user;

import java.util.List;

import org.heliosphere.thot.akka.chat.client.TerminalActor;
import org.heliosphere.thot.akka.chat.server.room.RoomActor;

import com.heliosphere.athena.base.message.internal.IMessageProtocol;

public class UserMessageProtocol
{
	/**
	 * Message used to get a list of users in a room.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class UserList implements IMessageProtocol
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Room.
		 */
		private final String room;

		/**
		 * Users.
		 */
		private final List<String> users;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param room Room name.
		 * @param users Users in the room.
		 */
		public UserList(String room, List<String> users)
		{
			this.room = room;
			this.users = users;
		}

		/**
		 * Returns the room name.
		 * <hr>
		 * @return Room name.
		 */
		public final String getRoom()
		{
			return room;
		}

		/**
		 * Returns the users in the room.
		 * <hr>
		 * @return Users.
		 */
		public final List<String> getUsers()
		{
			return users;
		}
	}

	/**
	 * Message send by a {@link TerminalActor} to a {@link RoomActor} when a user sends a message to a room.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class Say implements IMessageProtocol
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Message.
		 */
		private final String message;

		/**
		 * User.
		 */
		private final String user;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param message Message.
		 */
		public Say(String user, String message)
		{
			this.user = user;
			this.message = message;
		}

		/**
		 * Returns the user.
		 * <hr>
		 * @return User.
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

	/**
	 * Message send by a {@link RoomActor} to a {@link TerminalActor} to notify a user sent a public message in a room.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class Said implements IMessageProtocol
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Message.
		 */
		private final String message;

		/**
		 * User.
		 */
		private final String user;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param message Message.
		 */
		public Said(String user, String message)
		{
			this.user = user;
			this.message = message;
		}

		/**
		 * Returns the user.
		 * <hr>
		 * @return User.
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

	/**
	 * Message send by a {@link TerminalActor} to a {@link RoomActor} when a user sends a message to another user in a room.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class Whisper implements IMessageProtocol
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Recipient user name.
		 */
		private final String recipient;

		/**
		 * Sender user name.
		 */
		private final String sender;

		/**
		 * Message.
		 */
		private final String message;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param recipient Recipient user name.
		 * @param message Message.
		 * @param sender Sender user name.
		 */
		public Whisper(String recipient, String message, String sender)
		{
			this.recipient = recipient;
			this.message = message;
			this.sender = sender;
		}

		/**
		 * Returns the recipient user.
		 * <hr>
		 * @return Recipient user name.
		 */
		public final String getRecipient()
		{
			return recipient;
		}

		/**
		 * Returns the sender user.
		 * <hr>
		 * @return Sender user name.
		 */
		public final String getSender()
		{
			return sender;
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
	 * Message send by a {@link RoomActor} to a {@link TerminalActor} to notify a user sent a private message in a room to a specific user.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class Whispered implements IMessageProtocol
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Message.
		 */
		private final String message;

		/**
		 * Sender user name.
		 */
		private final String sender;

		/**
		 * User.
		 */
		private final String user;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param message Message.
		 * @param sender Sender user name.
		 */
		public Whispered(String user, String message, String sender)
		{
			this.user = user;
			this.message = message;
			this.sender = sender;
		}

		/**
		 * Returns the user.
		 * <hr>
		 * @return User.
		 */
		public final String getUser()
		{
			return user;
		}

		/**
		 * Returns the sender user.
		 * <hr>
		 * @return Sender user name.
		 */
		public final String getSender()
		{
			return sender;
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
