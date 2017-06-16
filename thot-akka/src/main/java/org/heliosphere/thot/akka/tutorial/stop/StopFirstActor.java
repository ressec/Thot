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
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * Very simple {@code Akka} actor creating on a message type a child actor.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class StopFirstActor extends AbstractActor
{
	/**
	 * Child actor.
	 */
	private ActorRef second = null;

	/**
	 * Creates a new actor.
	 */
	public StopFirstActor()
	{
		// Create a child actor.
		second = getContext().actorOf(Props.create(StopSecondActor.class), "second-actor");
	}

	@SuppressWarnings("nls")
	@Override
	public void preStart()
	{
		System.out.println("first-actor started");
	}

	@SuppressWarnings("nls")
	@Override
	public void postStop()
	{
		System.out.println("first-actor stopped");
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

		// Ask child actor to also print its reference.
		second.tell("printIt", ActorRef.noSender());
	}

	/**
	 * Handles the {@code stopIt} message.
	 */
	private final void handleStop()
	{
		// Stops the actor hierarchy from path: [akka://<actor_system_name>/user/first-actor]
		getContext().stop(getSelf());
	}
}
