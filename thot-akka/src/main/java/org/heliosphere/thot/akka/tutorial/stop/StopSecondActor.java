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
package org.heliosphere.thot.akka.tutorial.stop;

import akka.actor.AbstractActor;

/**
 * Simple {@code Akka} actor.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class StopSecondActor extends AbstractActor
{
	@SuppressWarnings("nls")
	@Override
	public void preStart()
	{
		System.out.println("second-actor started");
	}

	@SuppressWarnings("nls")
	@Override
	public void postStop()
	{
		System.out.println("second-actor stopped");
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
		System.out.println("reference: " + this.getSelf());
	}

	/**
	 * Handles the {@code stopIt} message.
	 */
	private final void handleStop()
	{
		// Stops the actor hierarchy from path: [akka://<actor_system_name>/user/first-actor/second-actor]
		getContext().stop(getSelf());
	}
}
