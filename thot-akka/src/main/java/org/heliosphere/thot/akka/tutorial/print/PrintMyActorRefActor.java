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
package org.heliosphere.thot.akka.tutorial.print;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

/**
 * Very simple {@code Akka} actor creating on a message type a child actor.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class PrintMyActorRefActor extends AbstractActor
{
	@SuppressWarnings("nls")
	@Override
	public Receive createReceive()
	{
		return receiveBuilder().matchEquals("printIt", p -> HandlePrint())
				//.matchAny()
				.build();
	}

	/**
	 * Handles the {@code printIt} message.
	 */
	@SuppressWarnings("nls")
	private final void HandlePrint()
	{
		// Create a child actor that does nothing.
		ActorRef second = getContext().actorOf(Props.empty(), "second-actor");
		System.out.println("Second: " + second);
	}
}
