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
package org.heliosphere.thot.akka.tutorial;

import org.heliosphere.thot.akka.AkkaUtility;
import org.heliosphere.thot.akka.chat.client.TerminalActor;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public final class AkkaSimpleCommandTerminal
{
	/**
	 * AKKA configuration file.
	 */
	@SuppressWarnings("nls")
	private static final String AKKA_CONFIG = "./config/cluster/chat/application.conf";

	/**
	 * Reference to the command processor actor.
	 */
	private static ActorRef commandProcessorNormal = null;

	/**
	 * Reference to the command terminal actor.
	 */
	private static ActorRef terminal = null;

	/**
	 * Main entry point of the application.
	 * <hr>
	 * @param arguments Arguments entered on the command line.
	 */
	@SuppressWarnings("nls")
	public static void main(String[] arguments)
	{
		// Override the configuration of the port number.
		Config configuration = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + arguments[0]).withFallback(ConfigFactory.load(AKKA_CONFIG));

		AkkaUtility.dumpConfigKeyFor(configuration, "akka.actor.provider");

		// Create the actor system.
		ActorSystem system = ActorSystem.create("HeliosphereChatSystem", configuration);

		// Creates the command terminal actor.
		terminal = system.actorOf(Props.create(TerminalActor.class), "terminal");

		//		// Create a command processor actor.
		//		commandProcessorNormal = system
		//				.actorOf(Props.create(ChatNormalCommandProcessor.class), "command-processor-normal");
	}
}
