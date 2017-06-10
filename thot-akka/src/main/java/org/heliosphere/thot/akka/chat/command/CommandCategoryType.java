/*
 * Copyright(c) 2010-2013 Heliosphere Ltd.
 * ---------------------------------------------------------------------------
 * This file is part of the Drake project which is licensed under the Apache
 * license version 2 and use is subject to license terms. You should have
 * received a copy of the license with the project artefact binaries and/or
 * sources.
 * 
 * License can be consulted at http://www.apache.org/licenses/LICENSE-2.0
 * ---------------------------------------------------------------------------
 */
package org.heliosphere.thot.akka.chat.command;

import org.heliosphere.drake.base.exception.InvalidArgumentException;
import org.heliosphere.drake.base.resource.bundle.BundleBase;
import org.heliosphere.drake.base.resource.bundle.ResourceBundleManager;

/**
 * Enumeration of the command category types.
 * <hr>
 * @author <a href="mailto:christophe.resse@hotmail.com">Christophe Resse</a>
 * @version 1.0.0
 */
@SuppressWarnings("nls")
public enum CommandCategoryType
{
	/**
	 * Debug command category.
	 */
	Debug("*"),

	/**
	 * Normal command category.
	 */
	Normal("/"),

	/**
	 * Administration command category.
	 */
	Administration("/"),

	/**
	 * Super administration command category.
	 */
	SuperAdministration("%"),

	/**
	 * System command category.
	 */
	System(".");

	/**
	 * Command category prefix.
	 */
	private final String prefix;

	/**
	 * Creates a new enumerated value based on the command prefix.
	 * <p>
	 * @param prefix Command prefix.
	 */
	private CommandCategoryType(final String prefix)
	{
		this.prefix = prefix;
	}

	/**
	 * Returns the prefix of the command category.
	 * <p>
	 * @return Command prefix.
	 */
	public final String getPrefix()
	{
		return prefix;
	}

	/**
	 * Creates a command category from a command prefix.
	 * <p>
	 * @param prefix Command prefix.
	 * @return {@link CommandCategoryType}.
	 */
	public final CommandCategoryType from(final String prefix)
	{
		if (prefix == null || prefix.trim().length() == 0)
		{
			throw new InvalidArgumentException(ResourceBundleManager.getMessage(BundleBase.CommandCategoryCannotBeNull));
		}

		for (CommandCategoryType value : CommandCategoryType.values())
		{
			if (value.getPrefix().equalsIgnoreCase(prefix))
			{
				return value;
			}
		}

		throw new InvalidArgumentException(ResourceBundleManager.getMessage(BundleBase.CannotCreateEnumerated, this.getClass(), prefix));
	}
}
