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
package org.heliosphere.thot.akka.chat.client.command;

import com.heliosphere.athena.base.command.internal.protocol.ICommandCategoryType;
import com.heliosphere.athena.base.command.internal.protocol.ICommandDomainType;
import com.heliosphere.athena.base.command.internal.protocol.ICommandGroupType;
import com.heliosphere.athena.base.command.internal.protocol.ICommandProtocolType;
import com.heliosphere.athena.base.command.protocol.DefaultCommandCategoryType;
import com.heliosphere.athena.base.command.protocol.DefaultCommandGroupType;
import com.heliosphere.athena.base.exception.InvalidArgumentException;
import com.heliosphere.athena.base.resource.bundle.BundleAthenaBase;
import com.heliosphere.athena.base.resource.bundle.ResourceBundleManager;

public enum ChatCommandProtocol implements ICommandProtocolType
{
	/**
	 * Command used to list the lobbies.
	 */
	LOBBY_LIST(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.CHAT, ChatCommandDomainType.LOBBY),

	/**
	 * Command used to create a lobby.
	 */
	LOBBY_CREATE(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.CHAT, ChatCommandDomainType.LOBBY),

	/**
	 * Command used to delete a lobby.
	 */
	LOBBY_DELETE(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.CHAT, ChatCommandDomainType.LOBBY),

	/**
	 * Command used to join a lobby.
	 */
	LOBBY_JOIN(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.CHAT, ChatCommandDomainType.LOBBY),

	/**
	 * Command used to leave a lobby.
	 */
	LOBBY_LEAVE(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.CHAT, ChatCommandDomainType.LOBBY),

	/**
	 * Command used to list the rooms.
	 */
	ROOM_LIST(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.CHAT, ChatCommandDomainType.ROOM),

	/**
	 * Command used to create a room.
	 */
	ROOM_CREATE(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.CHAT, ChatCommandDomainType.ROOM),

	/**
	 * Command used to delete a room.
	 */
	ROOM_DELETE(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.CHAT, ChatCommandDomainType.ROOM),

	/**
	 * Command used to join a room.
	 */
	ROOM_JOIN(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.CHAT, ChatCommandDomainType.ROOM),

	/**
	 * Command used to leave a room.
	 */
	ROOM_LEAVE(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.CHAT, ChatCommandDomainType.ROOM),

	/**
	 * Command used to register a user.
	 */
	USER_REGISTER(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.SYSTEM, ChatCommandDomainType.USER),

	/**
	 * Command used to unregister a user.
	 */
	USER_UNREGISTER(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.SYSTEM, ChatCommandDomainType.USER),

	/**
	 * Command used to list users in a room.
	 */
	USER_LIST(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.SYSTEM, ChatCommandDomainType.USER),

	/**
	 * Command to send a text message.
	 */
	MESSAGE_SAY(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.CHAT, ChatCommandDomainType.MESSAGE),

	/**
	 * Command to send a private text message.
	 */
	MESSAGE_WHIPSER(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.CHAT, ChatCommandDomainType.MESSAGE);

	/**
	 * Command category type.
	 */
	private final Enum<? extends ICommandCategoryType> category;

	/**
	 * Command group type.
	 */
	private final Enum<? extends ICommandGroupType> group;

	/**
	 * Command domain type.
	 */
	private final Enum<? extends ICommandDomainType> domain;

	/**
	 * Creates a new enumerated value based on given values.
	 * <p>
	 * @param category Command category type.
	 * @param group Command group type.
	 * @param domain Command domain type.
	 */
	private ChatCommandProtocol(final Enum<? extends ICommandCategoryType> category, final Enum<? extends ICommandGroupType> group, final Enum<? extends ICommandDomainType> domain)
	{
		this.category = category;
		this.group = group;
		this.domain = domain;
	}

	@Override
	public final Enum<? extends ICommandCategoryType> getCategory()
	{
		return category;
	}

	@Override
	public final Enum<? extends ICommandGroupType> getGroup()
	{
		return group;
	}

	@Override
	public final Enum<? extends ICommandDomainType> getDomain()
	{
		return domain;
	}

	@SuppressWarnings("nls")
	@Override
	public final ChatCommandProtocol fromString(final String value)
	{
		if (value == null || value.trim().length() == 0)
		{
			throw new InvalidArgumentException("Chat command protocol type string representation cannot be null!");
		}

		for (ChatCommandProtocol element : ChatCommandProtocol.values())
		{
			if (element.name().equalsIgnoreCase(value))
			{
				return element;
			}
		}

		throw new InvalidArgumentException(ResourceBundleManager.getMessage(BundleAthenaBase.CannotCreateEnumerated, ChatCommandProtocol.class.getName(), value));
	}
}
