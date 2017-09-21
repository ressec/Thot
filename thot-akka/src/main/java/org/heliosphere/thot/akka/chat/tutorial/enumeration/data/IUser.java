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

import com.heliosphere.athena.base.message.internal.IMessageContent;

/**
 * Provides the behavior of a user.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public interface IUser extends IMessageContent
{
	/**
	 * Returns the user's first name.
	 * <hr>
	 * @return User's first name.
	 */
	String getFirstName();

	/**
	 * Returns the user's last name.
	 * <hr>
	 * @return User's last name.
	 */
	String getLastName();

	/**
	 * Returns the user's alias.
	 * <hr>
	 * @return User's alias.
	 */
	String getAlias();

	/**
	 * Returns the user's unique identifier.
	 * <hr>
	 * @return User's identifier.
	 */
	int getUid();

	/**
	 * Returns if the user is authenticated against the server side?
	 * <hr>
	 * @return {@code True} if the user is authenticated, {@code false} otherwise.
	 */
	boolean isAuthenticated();
}
