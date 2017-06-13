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
package org.heliosphere.thot.akka.chat.server;

import org.heliosphere.thot.akka.AkkaUtility;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorSystem;
import akka.actor.Props;

public class ChatServer
{

	@SuppressWarnings("nls")
	public static void main(String[] args)
	{
		if (args.length == 0)
		{
			System.err.println("Port number must be provided!");
			System.exit(-1);
		}
		else
			if (args.length > 1)
			{
				System.err.println("Only port number can be provided!");
			}
			else
			{
				startup(args);
			}
	}

	@SuppressWarnings({ "nls", "javadoc" })
	public static void startup(String[] ports)
	{
		Config configuration = null;

		for (String port : ports)
		{
			// Override the configuration of the port
			configuration = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + port).withFallback(ConfigFactory.load("./config/cluster/chat/application.conf"));

			AkkaUtility.dumpConfigKeyFor(configuration, "akka.actor.provider");

			// Create an Akka system
			ActorSystem system = ActorSystem.create("HeliosphereChatSystem", configuration);

			// Create an actor that handles cluster domain events
			system.actorOf(Props.create(ClusterListener.class), "clusterListener");

		}
	}
}
