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
package org.heliosphere.thot.akka.chat.command;

public enum ChatCommandType
{
	Help(CommandCategoryType.Normal, "Help", "List the commands that are vaialble.", "h", null),

	Info(CommandCategoryType.Normal, "Info", "List the commands that are vaialble.", "h", null),

	Connect(CommandCategoryType.Normal, "Connect", "List the commands that are vaialble.", "h", null),

	Quit(CommandCategoryType.Normal, "Quit", "List the commands that are vaialble.", "h", null);

	/**
	 * Command category type prefix.
	 */
	private final CommandCategoryType category;

	/**
	 * Command name.
	 */
	private final String name;

	/**
	 * Command description.
	 */
	private final String description;

	/**
	 * Command aliases.
	 */
	private final String aliases;

	/**
	 * Command value.
	 */
	private final String value;

	/**
	 * Creates a new enumerated value based on values.
	 * <p>
	 * @param prefix Command prefix.
	 */
	private ChatCommandType(final CommandCategoryType category, final String name, final String description, final String aliases, final String value)
	{
		this.category = category;
		this.name = name;
		this.description = description;
		this.aliases = aliases;
		this.value = value;
	}

	/**
	 * Returns the command category type.
	 * <p>
	 * @return Category type.
	 */
	public final CommandCategoryType getCategory()
	{
		return category;
	}

	/**
	 * Returns the command name.
	 * <p>
	 * @return Category name.
	 */
	public final String getName()
	{
		return name;
	}

	/**
	 * Returns the command description.
	 * <p>
	 * @return Category description.
	 */
	public final String getDescription()
	{
		return description;
	}

	/**
	 * Returns the command aliases.
	 * <p>
	 * @return Category aliases.
	 */
	public final String getAliases()
	{
		return aliases;
	}

}
