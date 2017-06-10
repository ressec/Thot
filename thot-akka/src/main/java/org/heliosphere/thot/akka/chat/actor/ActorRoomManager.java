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
package org.heliosphere.thot.akka.chat.actor;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * A manager responsible to manage {@code rooms}.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class ActorRoomManager extends AbstractActor
{
	/**
	 * LOG.
	 */
	private final LoggingAdapter LOG = Logging.getLogger(getContext().getSystem(), this);

	@SuppressWarnings("nls")
	@Override
	public Receive createReceive()
	{
		receiveBuilder().match(String.class, message -> handleString(message));
		receiveBuilder().matchAny(o -> LOG.info("Received message of unknown type!"));

		return receiveBuilder().build();
	}

	/**
	 * Handles message of {@code string} type.
	 * @param message Message to handle.
	 */
	@SuppressWarnings("nls")
	private final void handleString(String message)
	{
		LOG.info("Recieved string message: " + message);
	}
}
