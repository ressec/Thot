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
 * Provides the behavior of a room.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public interface IRoom extends IMessageContent
{
	/**
	 * Returns the room's name.
	 * <hr>
	 * @return Room's name.
	 */
	String getName();

	/**
	 * Returns the room's unique identifier.
	 * <hr>
	 * @return Room's unique identifier.
	 */
	int getUid();
}
