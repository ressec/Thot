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

import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.MemberUp;
import akka.cluster.ClusterEvent.UnreachableMember;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ClusterListener extends AbstractActor
{
	private LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private Cluster cluster = Cluster.get(getContext().system());

	// Subscribes to cluster changes.
	@Override
	public void preStart()
	{
		cluster.subscribe(self(), ClusterEvent.initialStateAsEvents(), MemberEvent.class, UnreachableMember.class);
	}

	// Re-subscribes when restart.
	@Override
	public void postStop()
	{
		cluster.unsubscribe(self());
	}

	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
				.match(MemberUp.class, event -> handleMemberUp(event))
				.match(MemberRemoved.class, event -> handleMemberRemoved(event))
				.match(MemberEvent.class, event -> handleMemberEvent(event))
				.build();
	}

	@SuppressWarnings("nls")
	private void handleMemberUp(MemberUp event)
	{
		System.out.println("Member :" + event.member().toString() + " is up");
	}

	@SuppressWarnings("nls")
	private void handleMemberRemoved(MemberRemoved event)
	{
		System.out.println("Member :" + event.member().toString() + " is unreachable");
	}

	@SuppressWarnings("nls")
	private void handleMemberEvent(MemberEvent event)
	{
		System.out.println("Member :" + event.member().toString() + " is: " + event.toString());
	}
}
