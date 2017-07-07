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
package org.heliosphere.thot.akka.chat.tutorial.enumeration.data;

/**
 * Represents a room in the chat application. This POJO is used as message's data.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class Room implements IRoom
{
	/**
	 * Default serialization identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Room's name.
	 */
	private String name;

	/**
	 * Room's unique identifier.
	 */
	private int uid;

	/**
	 * Creates a new room.
	 * <hr>
	 * @param name Room's name.
	 */
	public Room(String name)
	{
		this.name = name;
	}

	/**
	 * Creates a new room.
	 * <hr>
	 * @param uid Room's unique identifier.
	 */
	public Room(int uid)
	{
		this.uid = uid;
	}

	/**
	 * Creates a new room.
	 * <hr>
	 * @param name Room's name.
	 * @param uid Room's unique identifier.
	 */
	public Room(String name, int uid)
	{
		this.name = name;
		this.uid = uid;
	}

	@Override
	public final String getName()
	{
		return name;
	}

	@Override
	public final int getUid()
	{
		return uid;
	}
}
