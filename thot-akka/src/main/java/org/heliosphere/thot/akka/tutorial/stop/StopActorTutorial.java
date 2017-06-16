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

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * {@code Akka} actor tutorial demonstrating how to stop an actor's hierarchy.
 * <p>
 * This tutorial is creating an actor system with one user actor and one child actor. It
 * then ask (sending messages) the actors to output their reference on the console. It finally
 * ask the user actor to stop itself and its whole actor's hierarchy.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class StopActorTutorial
{
	/**
	 * Creates a new actor.
	 * <hr>
	 * @param systemName Actor system name.
	 */
	@SuppressWarnings("nls")
	public StopActorTutorial(final String systemName)
	{
		// Create an actor system without special configuration file.
		ActorSystem system = ActorSystem.create(systemName);

		// Create a user actor.
		ActorRef first = system.actorOf(Props.create(StopFirstActor.class), "first-actor");

		// Sends a message (omitting the sender) to ask actor to print its reference on the console.
		first.tell("printIt", ActorRef.noSender());

		try
		{
			// Pauses the thread for 100 milliseconds to avoid the actor shutdown before the printIt message is processed!
			Thread.sleep(100);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		// Sends a message asking 'first-actor' to stop its actor hierarchy.
		first.tell("stopIt", ActorRef.noSender());


		// Terminates the actor system.
		system.terminate();
	}
}
