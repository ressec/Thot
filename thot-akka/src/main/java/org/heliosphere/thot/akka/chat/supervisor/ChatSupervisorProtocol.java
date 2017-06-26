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

import java.util.List;
import java.util.Locale;

import com.heliosphere.athena.base.message.internal.IMessageProtocol;

/**
 * Message protocol for a {@link ChatSupervisor} actor.
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
	 * Message used to get the server time.
	 * <p>
	 * This message is generally used by a client to initiate the first connection to the server side.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class QueryServerTime
	{
		/**
		 * Server time.
		 */
		private final String time;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param time Server time.
		 */
		public QueryServerTime(String time)
		{
			this.time = time;
		}

		/**
		 * Returns the server time.
		 * <hr>
		 * @return Time.
		 */
		public final String getServerTime()
		{
			return time;
		}
	}

	/**
	 * Message used to get the available list of lobbies.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class LobbyList
	{
		/**
		 * User.
		 */
		private final String user;

		/**
		 * Lobbies.
		 */
		private final List<Locale> lobbies;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param lobbies Lobbies.
		 */
		public LobbyList(String user, List<Locale> lobbies)
		{
			this.user = user;
			this.lobbies = lobbies;
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
		 * Returns the lobbies.
		 * <hr>
		 * @return Lobbies.
		 */
		public final List<Locale> getLobbies()
		{
			return lobbies;
		}
	}

	/**
	 * Message used to create a new lobby.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class LobbyCreate
	{
		/**
		 * User.
		 */
		private final String user;

		/**
		 * Lobby's locale.
		 */
		private final Locale lobby;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param lobby Lobby's locale.
		 */
		public LobbyCreate(String user, Locale lobby)
		{
			this.user = user;
			this.lobby = lobby;
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
		 * Returns the lobby's locale.
		 * <hr>
		 * @return Locale.
		 */
		public final Locale getLobby()
		{
			return lobby;
		}
	}

	/**
	 * Message used to confirm a lobby has been created.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class LobbyCreated
	{
		/**
		 * Lobby's locale.
		 */
		private final Locale lobby;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param lobby Lobby's locale.
		 */
		public LobbyCreated(Locale lobby)
		{
			this.lobby = lobby;
		}

		/**
		 * Returns the lobby's locale.
		 * <hr>
		 * @return Locale.
		 */
		public final Locale getLobby()
		{
			return lobby;
		}
	}

	/**
	 * Message used to delete a lobby.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class LobbyDelete
	{
		/**
		 * User.
		 */
		private final String user;

		/**
		 * Lobby's locale.
		 */
		private final Locale lobby;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param lobby Lobby's locale.
		 */
		public LobbyDelete(String user, Locale lobby)
		{
			this.user = user;
			this.lobby = lobby;
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
		 * Returns the lobby's locale.
		 * <hr>
		 * @return Locale.
		 */
		public final Locale getLobby()
		{
			return lobby;
		}
	}

	/**
	 * Message used to confirm a lobby has been deleted.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class LobbyDeleted
	{
		/**
		 * Lobby's locale.
		 */
		private final Locale lobby;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param lobby Lobby's locale.
		 */
		public LobbyDeleted(Locale lobby)
		{
			this.lobby = lobby;
		}

		/**
		 * Returns the lobby's locale.
		 * <hr>
		 * @return Locale.
		 */
		public final Locale getLobby()
		{
			return lobby;
		}
	}
	/**
	 * Message used to connect to a lobby.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class LobbyConnect
	{
		/**
		 * User.
		 */
		private final String user;

		/**
		 * Lobby's locale.
		 */
		private final Locale lobby;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param lobby Lobby's locale.
		 */
		public LobbyConnect(String user, Locale lobby)
		{
			this.user = user;
			this.lobby = lobby;
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
		 * Returns the lobby's locale.
		 * <hr>
		 * @return Locale.
		 */
		public final Locale getLobby()
		{
			return lobby;
		}
	}

	/**
	 * Message used to confirm a user has connected to a lobby.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class LobbyConnected
	{
		/**
		 * User.
		 */
		private final String user;

		/**
		 * Lobby's locale.
		 */
		private final Locale lobby;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param lobby Lobby's locale.
		 */
		public LobbyConnected(String user, Locale lobby)
		{
			this.user = user;
			this.lobby = lobby;
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
		 * Returns the lobby's locale.
		 * <hr>
		 * @return Locale.
		 */
		public final Locale getLobby()
		{
			return lobby;
		}
	}

	/**
	 * Message used to disconnect from a lobby.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class LobbyDisconnect
	{
		/**
		 * User.
		 */
		private final String user;

		/**
		 * Lobby's locale.
		 */
		private final Locale lobby;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param lobby Lobby's locale.
		 */
		public LobbyDisconnect(String user, Locale lobby)
		{
			this.user = user;
			this.lobby = lobby;
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
		 * Returns the lobby's locale.
		 * <hr>
		 * @return Locale.
		 */
		public final Locale getLobby()
		{
			return lobby;
		}
	}

	/**
	 * Message used to confirm a user has been disconnected from a lobby.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class LobbyDisconnected
	{
		/**
		 * User.
		 */
		private final String user;

		/**
		 * Lobby's locale.
		 */
		private final Locale lobby;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param user User.
		 * @param lobby Lobby's locale.
		 */
		public LobbyDisconnected(String user, Locale lobby)
		{
			this.user = user;
			this.lobby = lobby;
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
		 * Returns the lobby's locale.
		 * <hr>
		 * @return Locale.
		 */
		public final Locale getLobby()
		{
			return lobby;
		}
	}
	/**
	 * Message used to get the available list of rooms.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class RoomList
	{
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
	public final static class RoomCreate
	{
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
	public final static class RoomCreated
	{
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
	public final static class RoomDelete
	{
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
	public final static class RoomDeleted
	{
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
	 * Message used to connect to a room.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class RoomConnect
	{
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
		public RoomConnect(String user, String room)
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
	 * Message used to confirm a user is connected to a room.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class RoomConnected
	{
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
		public RoomConnected(String user, String room)
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
	 * Message used to disconnect from a room.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class RoomDisconnect
	{
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
		public RoomDisconnect(String user, String room)
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
	 * Message used to confirm a user is disconnected from a room.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class RoomDisconnected
	{
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
		public RoomDisconnected(String user, String room)
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

	/**
	 * Message used to get a list of users in a room.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class UserList
	{
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
}
