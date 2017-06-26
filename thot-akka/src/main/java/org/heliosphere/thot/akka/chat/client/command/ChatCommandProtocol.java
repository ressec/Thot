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
	 * Command to manipulate users.
	 */
	USER(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.SYSTEM),

	/**
	 * Command to manipulate lobbies.
	 */
	LOBBY(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.CHAT),

	/**
	 * Command to manipulate rooms.
	 */
	ROOM(DefaultCommandCategoryType.NORMAL, DefaultCommandGroupType.CHAT);

	/**
	 * Command category type.
	 */
	private final Enum<? extends ICommandCategoryType> category;

	/**
	 * Command group type.
	 */
	private final Enum<? extends ICommandGroupType> group;

	/**
	 * Creates a new enumerated value based on given values.
	 * <p>
	 * @param category Command category type.
	 * @param group Command group type.
	 */
	private ChatCommandProtocol(final Enum<? extends ICommandCategoryType> category, final Enum<? extends ICommandGroupType> group)
	{
		this.category = category;
		this.group = group;
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

	@SuppressWarnings("nls")
	@Override
	public final ChatCommandProtocol fromString(final String value)
	{
		if (value == null || value.trim().length() == 0)
		{
			throw new InvalidArgumentException("Chat command protocol string representation cannot be null!");
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
