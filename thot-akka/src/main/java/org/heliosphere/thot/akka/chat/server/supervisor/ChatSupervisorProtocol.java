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

import com.heliosphere.athena.base.message.internal.IMessageProtocol;

/**
 * Message protocol for a {@link ChatSupervisorActor} actor.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public final class ChatSupervisorProtocol implements IMessageProtocol
{
	/**
	 * Default serialization identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Message used to initiate a conversation with the server.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class InitiateConversation
	{
		// Empty.
	}

	/**
	 * Message used confirm a conversation has been initiated with the server.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class ConversationInitiated
	{
		// Empty.
	}


	/**
	 * Message used to register a user.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class RegisterUser
	{
		/**
		 * User name.
		 */
		private final String user;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User name.
		 */
		public RegisterUser(String user)
		{
			this.user = user;
		}

		/**
		 * Returns the user name.
		 * <hr>
		 * @return User name.
		 */
		public final String getUser()
		{
			return user;
		}
	}

	/**
	 * Message used to confirm a user has been registered.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class UserRegistered
	{
		/**
		 * User name.
		 */
		private final String user;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User name.
		 */
		public UserRegistered(String user)
		{
			this.user = user;
		}

		/**
		 * Returns the user name.
		 * <hr>
		 * @return User name.
		 */
		public final String getUser()
		{
			return user;
		}
	}
}
