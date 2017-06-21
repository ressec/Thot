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

import java.io.IOException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

/**
 * {@code Akka} actor tutorial demonstrates ...
 * <p>
 * This tutorial is creating an actor system with one user actor and one child actor. It
 * then ask (sending messages) the actors to output their reference on the console. It finally
 * ask the user actor to stop itself and its whole actor's hierarchy.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class IoTSupervisorTutorial
{
	/**
	 * Creates a new actor.
	 * <hr>
	 * @param systemName Actor system name.
	 */
	@SuppressWarnings("nls")
	public IoTSupervisorTutorial(final String systemName)
	{
		// Create an actor system without special configuration file.
		ActorSystem system = ActorSystem.create(systemName);

		try
		{
			// Create the supervisor actor.
			ActorRef supervisor = system.actorOf(SupervisorActor.props(), "iot-supervisor");

			System.out.println("Press ENTER to exit the system");
			System.in.read();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			system.terminate();
		}

		// Terminates the actor system.
		system.terminate();
	}
}
