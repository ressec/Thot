/*
 * Copyright(c) 2010-2013 Heliosphere Ltd.
 * ---------------------------------------------------------------------------
 * This file is part of the Drake project which is licensed under the Apache
 * license version 2 and use is subject to license terms. You should have
 * received a copy of the license with the project artifact binaries and/or
 * sources.
 * 
 * License can be consulted at http://www.apache.org/licenses/LICENSE-2.0
 * ---------------------------------------------------------------------------
 */
package org.heliosphere.thot.akka.chat.client.command;

import com.heliosphere.athena.base.command.internal.protocol.ICommandCodeType;
import com.heliosphere.athena.base.exception.InvalidArgumentException;
import com.heliosphere.athena.base.resource.bundle.BundleAthenaBase;
import com.heliosphere.athena.base.resource.bundle.ResourceBundleManager;

/**
 * Enumeration defining a set of basic chat command code types.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse</a>
 * @version 1.0.0
 */
public enum ChatCommandCodeType implements ICommandCodeType
{
	/**
	 * Registers a user name against the chat system.
	 */
	REGISTER_USER,

	/**
	 * Unregisters a user name against the chat system.
	 */
	UNREGISTER_USER,

	/**
	 * Creates a lobby.
	 */
	LOBBY_CREATE,

	/**
	 * Deletes a lobby.
	 */
	LOBBY_DELETE,

	/**
	 * Gets the list of lobbies.
	 */
	LOBBY_LIST,

	/**
	 * Creates a room.
	 */
	ROOM_CREATE,

	/**
	 * Deletes a room.
	 */
	ROOM_DELETE,

	/**
	 * Gets the list of rooms.
	 */
	ROOM_LIST,

	/**
	 * Gets the list of users.
	 */
	USER_LIST,

	/**
	 * Sends a message to all users of a room.
	 */
	SAY,

	/**
	 * Sends a message to a specific user.
	 */
	WHISPER,

	/**
	 * Quits the client application.
	 */
	QUIT;

	/**
	 * Creates an command code enumerated value from a given string value.
	 * <p>
	 * <b>Example:</b><p> 
	 * <code>CommandCodeType.fromString("Quit");</code>
	 * <hr>
	 * @param value String representing the enumerated value.
	 * @return Command code type.
	 */
	@Override
	public Enum<? extends ICommandCodeType> fromString(String value)
	{
		if (value == null || value.trim().length() == 0)
		{
			throw new InvalidArgumentException(ResourceBundleManager.getMessage(BundleAthenaBase.CommandCategoryCannotBeNull));
		}

		for (ChatCommandCodeType element : ChatCommandCodeType.values())
		{
			if (element.name().equalsIgnoreCase(value))
			{
				return element;
			}
		}

		throw new InvalidArgumentException(ResourceBundleManager.getMessage(BundleAthenaBase.CannotCreateEnumerated, ChatCommandCodeType.class.getName(), value));
	}
}
