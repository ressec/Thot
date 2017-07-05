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
package org.heliosphere.thot.akka.chat.server.room;

import java.util.List;
import java.util.Locale;

import com.heliosphere.athena.base.message.internal.IMessageProtocol;

public class RoomMessageProtocol
{
	/**
	 * Message used to get the available list of rooms.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class RoomList implements IMessageProtocol
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * User.
		 */
		private final String user;

		/**
		 * Lobby.
		 */
		private final Locale lobby;

		/**
		 * Rooms.
		 */
		private final List<String> rooms;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param lobby Lobby.
		 * @param rooms Rooms.
		 */
		public RoomList(String user, Locale lobby, List<String> rooms)
		{
			this.user = user;
			this.lobby = lobby;
			this.rooms = rooms;
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
		 * Returns the lobby.
		 * <hr>
		 * @return Lobby.
		 */
		public final Locale getLobby()
		{
			return lobby;
		}

		/**
		 * Returns the rooms.
		 * <hr>
		 * @return Rooms.
		 */
		public final List<String> getRooms()
		{
			return rooms;
		}
	}

	/**
	 * Message used to create a new room.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class RoomCreate implements IMessageProtocol
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * User.
		 */
		private final String user;

		/**
		 * Room.
		 */
		private final String room;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param room Room name.
		 */
		public RoomCreate(String user, String room)
		{
			this.user = user;
			this.room = room;
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
		 * Returns the room's name.
		 * <hr>
		 * @return Name.
		 */
		public final String getRoom()
		{
			return room;
		}
	}

	/**
	 * Message used to confirm a new room has been created.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class RoomCreated implements IMessageProtocol
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * User.
		 */
		private final String user;

		/**
		 * Room.
		 */
		private final String room;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param room Room name.
		 */
		public RoomCreated(String user, String room)
		{
			this.user = user;
			this.room = room;
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
		 * Returns the room's name.
		 * <hr>
		 * @return Name.
		 */
		public final String getRoom()
		{
			return room;
		}
	}

	/**
	 * Message used to delete a room.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class RoomDelete implements IMessageProtocol
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * User.
		 */
		private final String user;

		/**
		 * Room.
		 */
		private final String room;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param room Room name.
		 */
		public RoomDelete(String user, String room)
		{
			this.user = user;
			this.room = room;
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
		 * Returns the room's name.
		 * <hr>
		 * @return Name.
		 */
		public final String getRoom()
		{
			return room;
		}
	}

	/**
	 * Message used to confirm a room has been deleted.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class RoomDeleted implements IMessageProtocol
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * User.
		 */
		private final String user;

		/**
		 * Room.
		 */
		private final String room;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param room Room name.
		 */
		public RoomDeleted(String user, String room)
		{
			this.user = user;
			this.room = room;
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
		 * Returns the room's name.
		 * <hr>
		 * @return Name.
		 */
		public final String getRoom()
		{
			return room;
		}
	}

	/**
	 * Message used by a client to request a user to join a room.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class RoomJoin implements IMessageProtocol
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * User.
		 */
		private final String user;

		/**
		 * Room.
		 */
		private final String room;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param room Room name.
		 */
		public RoomJoin(String user, String room)
		{
			this.user = user;
			this.room = room;
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
		 * Returns the room's name.
		 * <hr>
		 * @return Name.
		 */
		public final String getRoom()
		{
			return room;
		}
	}

	/**
	 * Message used to confirm a user has joined to a room.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class RoomJoined implements IMessageProtocol
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * User.
		 */
		private final String user;

		/**
		 * Room.
		 */
		private final String room;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param room Room name.
		 */
		public RoomJoined(String user, String room)
		{
			this.user = user;
			this.room = room;
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
		 * Returns the room's name.
		 * <hr>
		 * @return Name.
		 */
		public final String getRoom()
		{
			return room;
		}
	}

	/**
	 * Message used by a client to request a user to leave a room.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class RoomLeave implements IMessageProtocol
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * User.
		 */
		private final String user;

		/**
		 * Room.
		 */
		private final String room;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param room Room name.
		 */
		public RoomLeave(String user, String room)
		{
			this.user = user;
			this.room = room;
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
		 * Returns the room's name.
		 * <hr>
		 * @return Name.
		 */
		public final String getRoom()
		{
			return room;
		}
	}

	/**
	 * Message used to confirm a user has left from a room.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class RoomLeft implements IMessageProtocol
	{
		/**
		 * Default serialization identifier.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * User.
		 */
		private final String user;

		/**
		 * Room.
		 */
		private final String room;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param room Room name.
		 */
		public RoomLeft(String user, String room)
		{
			this.user = user;
			this.room = room;
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
		 * Returns the room's name.
		 * <hr>
		 * @return Name.
		 */
		public final String getRoom()
		{
			return room;
		}
	}
}
