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
package org.heliosphere.thot.akka.tutorial.client;

import java.util.HashSet;
import java.util.Set;

import org.heliosphere.thot.akka.AkkaUtility;
import org.heliosphere.thot.akka.chat.client.TerminalActor;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorPath;
import akka.actor.ActorPaths;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Address;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.client.ClusterClient;
import akka.cluster.client.ClusterClientSettings;

/**
 * Client application (outside of a cluster) able to communicate with a cluster.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public final class ClusterClientTutorial
{
	/**
	 * AKKA configuration file.
	 */
	@SuppressWarnings("nls")
	private static final String AKKA_CONFIG = "./config/cluster/chat/application.conf";

	/**
	 * Main entry point of the application.
	 * <hr>
	 * @param arguments Arguments entered on the command line.
	 */
	@SuppressWarnings("nls")
	public static void main(String[] arguments)
	{
		Set<ActorPath> initialContacts = new HashSet<>();

		Config config = ConfigFactory.load(AKKA_CONFIG);

		AkkaUtility.dumpConfigKeyFor(config, "akka.actor.provider");

		ActorSystem system = ActorSystem.create("HeliosphereCommandCluster", config);
		Address clusterAddress = Cluster.get(system).selfAddress();	
		initialContacts.add(ActorPaths.fromString(clusterAddress + "/system/receptionist"));

		ActorRef clusterClient = system.actorOf(ClusterClient.props(ClusterClientSettings.create(system).withInitialContacts(initialContacts)), "clusterClient");

		// Override the configuration of the port number.
		Config configuration = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + arguments[0]).withFallback(ConfigFactory.load(AKKA_CONFIG));


		// Creates the command terminal actor.
		ActorRef terminal = system.actorOf(Props.create(TerminalActor.class), "terminal");
	}
}
