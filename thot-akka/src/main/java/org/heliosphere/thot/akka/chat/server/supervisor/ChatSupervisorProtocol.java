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

import java.io.Serializable;

/**
 * Message protocol for a {@link ChatSupervisorActor} actor.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public final class ChatSupervisorProtocol
{
	/**
	 * Message used to initiate a conversation with the server.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class InitiateConversation implements Serializable
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Message used confirm a conversation has been initiated with the server.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class ConversationInitiated implements Serializable
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;
	}

	/**
	 * Message used to register a user.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class RegisterUser implements Serializable
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

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
	public final static class UserRegistered implements Serializable
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

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

	/**
	 * Message used to unregister a user.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class UnregisterUser implements Serializable
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * User name.
		 */
		private final String user;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User name.
		 */
		public UnregisterUser(String user)
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
	 * Message used to confirm a user has been unregistered.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class UserUnregistered implements Serializable
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * User name.
		 */
		private final String user;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User name.
		 */
		public UserUnregistered(String user)
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
