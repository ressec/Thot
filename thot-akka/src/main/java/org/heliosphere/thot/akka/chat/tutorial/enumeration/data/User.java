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
 * Represents a user in the chat application. This POJO is used as message's data.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class User implements IUser
{
	/**
	 * Default serialization identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * User's first name.
	 */
	private String firstname;

	/**
	 * User's last name.
	 */
	private String lastname;

	/**
	 * User's alias.
	 */
	private String alias;

	/**
	 * User's unique identifier.
	 */
	private int uid;

	/**
	 * Is the user authenticated against the server side?
	 */
	private boolean authenticated = false;

	/**
	 * Creates a new user.
	 * <hr>
	 * @param alias User's alias.
	 */
	public User(String alias)
	{
		this.alias = alias;
	}

	/**
	 * Creates a new user.
	 * <hr>
	 * @param uid User's unique identifier.
	 */
	public User(int uid)
	{
		this.uid = uid;
	}

	/**
	 * Creates a new user.
	 * <hr>
	 * @param firstname User's first name.
	 * @param lastname User's last name.
	 */
	public User(String firstname, String lastname)
	{
		this.firstname = firstname;
		this.lastname = lastname;
	}

	/**
	 * Creates a new user.
	 * <hr>
	 * @param firstname User's first name.
	 * @param lastname User's last name.
	 * @param alias User's alias.
	 * @param uid User's identifier.
	 * @param authenticated Is the user authenticated?
	 */
	public User(String firstname, String lastname, String alias, int uid, boolean authenticated)
	{
		this.firstname = firstname;
		this.lastname = lastname;
		this.alias = alias;
		this.uid = uid;
		this.authenticated = authenticated;
	}

	@Override
	public final String getFirstName()
	{
		return firstname;
	}

	@Override
	public final String getLastName()
	{
		return lastname;
	}

	@Override
	public final String getAlias()
	{
		return alias;
	}

	@Override
	public final int getUid()
	{
		return uid;
	}

	@Override
	public final boolean isAuthenticated()
	{
		return authenticated;
	}
}
