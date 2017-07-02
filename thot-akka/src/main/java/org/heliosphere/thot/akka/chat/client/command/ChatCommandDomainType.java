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

import com.heliosphere.athena.base.command.internal.protocol.ICommandDomainType;
import com.heliosphere.athena.base.exception.InvalidArgumentException;
import com.heliosphere.athena.base.resource.bundle.BundleAthenaBase;
import com.heliosphere.athena.base.resource.bundle.ResourceBundleManager;

/**
 * Enumeration defining a set of {@code default} command domain types.
 * <p>
 * <b>Note:</b><br>
 * You should not use this protocol directly (except for testing purpose) but you should define your(s) according to your needs.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse</a>
 * @version 1.0.0
 */
public enum ChatCommandDomainType implements ICommandDomainType
{
	/**
	 * User command domain type.
	 */
	USER,

	/**
	 * Lobby command domain type.
	 */
	LOBBY,

	/**
	 * Room command domain type.
	 */
	ROOM,

	/**
	 * Message command domain type.
	 */
	MESSAGE;

	/**
	 * Creates a command domain enumerated value from a given string value.
	 * <p>
	 * <b>Example:</b><p> 
	 * <code>DefultCommandDomainType.fromString("User");</code>
	 * <hr>
	 * @param value String representing the enumerated value.
	 * @return Command domain type.
	 */
	@Override
	public Enum<? extends ICommandDomainType> fromString(String value)
	{
		if (value == null || value.trim().length() == 0)
		{
			throw new InvalidArgumentException(ResourceBundleManager.getMessage(BundleAthenaBase.CommandCategoryCannotBeNull));
		}

		for (ChatCommandDomainType element : ChatCommandDomainType.values())
		{
			if (element.name().equalsIgnoreCase(value))
			{
				return element;
			}
		}

		throw new InvalidArgumentException(ResourceBundleManager.getMessage(BundleAthenaBase.CannotCreateEnumerated, ChatCommandDomainType.class.getName(), value));
	}
}
