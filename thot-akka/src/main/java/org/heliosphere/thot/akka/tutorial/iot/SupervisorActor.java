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
package org.heliosphere.thot.akka.tutorial.iot;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

/**
 * IoT supervisor actor.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class SupervisorActor extends AbstractActor
{
	// Akka logging adapter.
	private final LoggingAdapter LOG = Logging.getLogger(getContext().getSystem(), this);

	/** 
	 * Actor creational pattern using {@link Props}.
	 * <hr>
	 * @return Props.
	 */
	public static Props props()
	{
		return Props.create(SupervisorActor.class);
	}

	@Override
	public void preStart()
	{
		LOG.info("IoT Application started");
	}

	@Override
	public void postStop()
	{
		LOG.info("IoT Application stopped");
	}

	@SuppressWarnings("nls")
	@Override
	public Receive createReceive()
	{
		return receiveBuilder().matchEquals("printIt", p -> handlePrint())
				.matchEquals("stopIt", p -> handleStop())
				.matchAny(o -> System.out.println("Received unknown message!"))
				.build();
	}

	/**
	 * Handles the {@code printIt} message.
	 */
	@SuppressWarnings("nls")
	private final void handlePrint()
	{
	}

	/**
	 * Handles the {@code stopIt} message.
	 */
	private final void handleStop()
	{
	}
}
