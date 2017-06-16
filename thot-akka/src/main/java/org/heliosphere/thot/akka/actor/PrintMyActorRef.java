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
package org.heliosphere.thot.akka.actor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

/**
 * Very simple {@code Akka} actor tutorial.
 * <p>
 * It creates an actor system then a user actor {@code first-actor} and prints its reference (name) in the console output. 
 * It then sends a message {@code printit} to this actor. The {@code printit} message creates a child actor {@code second actor} 
 * and prints also its reference into the console output.
 * <p>
 * This tutorial demonstrates the following principles:<br>
 * <li>How to create a simple {@code actor system} without any specific configuration.</li> 
 * <li>How to create a user actor.</li> 
 * <li>How to create a child actor.</li> 
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class PrintMyActorRef
{
	/**
	 * Application entry point.
	 * <hr>
	 * @param args Arguments passed on the command-line.
	 */
	@SuppressWarnings("nls")
	public static void main(String[] args)
	{
		// Create an actor system without special configuration file.
		ActorSystem system = ActorSystem.create("tutorial-system");

		// Create a user actor.
		ActorRef first = system.actorOf(Props.create(PrintMyActorRefActor.class), "first-actor");
		System.out.println("First : " + first);

		// Sends a message omitting the sender.
		first.tell("printit", ActorRef.noSender());

		// Terminates the actor system.
		system.terminate();
	}
}
