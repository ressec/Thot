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
 * Represents a lobby in the chat application. This POJO is used as message's data.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class Lobby implements ILobby
{
	/**
	 * Default serialization identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Lobby's name.
	 */
	private String name;

	/**
	 * Lobby's unique identifier.
	 */
	private int id;

	/**
	 * Creates a new lobby.
	 * <hr>
	 * @param name Lobby's name.
	 * @param id Lobby's identifier.
	 */
	public Lobby(String name, int id)
	{
		this.name = name;
		this.id = id;
	}

	@Override
	public final String getName()
	{
		return name;
	}

	@Override
	public final int getId()
	{
		return id;
	}
}
