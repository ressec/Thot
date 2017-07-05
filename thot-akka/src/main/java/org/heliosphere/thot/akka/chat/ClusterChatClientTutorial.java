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
package org.heliosphere.thot.akka.chat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.heliosphere.thot.akka.chat.client.TerminalActor;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorPath;
import akka.actor.ActorPaths;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

/**
 * {@code Akka} actor tutorial demonstrating a cluster chat system.
 * <p>
 * This tutorial is creating an actor system with one user actor and one child actor. It
 * then ask (sending messages) the actors to output their reference on the console. It finally
 * ask the user actor to stop itself and its whole actor's hierarchy.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class ClusterChatClientTutorial
{
	/**
	 * Creates a new chat tutorial actor system.
	 * <hr>
	 * @param systemName Actor system name.
	 */
	@SuppressWarnings("nls")
	public ClusterChatClientTutorial(final String systemName)
	{
		Config config = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 0).withFallback(ConfigFactory.load());

		// Create an actor system without special configuration file.
		ActorSystem system = ActorSystem.create(systemName, config);

		// Create a terminal chat client actor.
		ActorRef terminal = system.actorOf(TerminalActor.props(
				"Terminal #1", "/config/terminal/terminal-1.properties",
				"/config/command/chat-client-commands.xml"), "chat-terminal-1");
	}

	private final static Set<ActorPath> initialContacts()
	{
		return new HashSet<>(Arrays.asList(
				ActorPaths.fromString("akka.tcp://ChatSystem@127.0.0.1:2551/system/receptionist")));
	}
}
