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

import java.time.Duration;

import org.heliosphere.thot.akka.chat.client.TerminalActor;
import org.heliosphere.thot.akka.chat.server.supervisor.ChatSupervisorActor;

import com.heliosphere.athena.base.message.protocol.DefaultMessageProtocol;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

/**
 * {@code Akka} actor tutorial demonstrating a chat system.
 * <p>
 * This tutorial is creating an actor system with one user actor and one child actor. It
 * then ask (sending messages) the actors to output their reference on the console. It finally
 * ask the user actor to stop itself and its whole actor's hierarchy.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class LocalChatTutorial
{
	/**
	 * Creates a new chat tutorial actor system.
	 * <hr>
	 * @param systemName Actor system name.
	 */
	@SuppressWarnings("nls")
	public LocalChatTutorial(final String systemName)
	{
		// Create an actor system without special configuration file.
		ActorSystem system = ActorSystem.create(systemName);

		// Create the chat manager actor.
		ActorRef supervisor = system.actorOf(ChatSupervisorActor.props(), "chat-supervisor");

		// Create a first terminal chat client actor.
		ActorRef terminal1 = system.actorOf(TerminalActor.props("Terminal #1", "/config/terminal/terminal-1.properties", "/config/command/chat-client-commands.xml"), "chat-terminal-1");
		// Create a second terminal chat client actor.
		ActorRef terminal2 = system.actorOf(TerminalActor.props("Terminal #2", "/config/terminal/terminal-2.properties", "/config/command/chat-client-commands.xml"), "chat-terminal-2");
		// Create a third terminal chat client actor.
		ActorRef terminal3 = system.actorOf(TerminalActor.props("Terminal #3", "/config/terminal/terminal-1.properties", "/config/command/chat-client-commands.xml"), "chat-terminal-3");

		Duration wait = Duration.ofMillis(300);
		terminal1.tell(new DefaultMessageProtocol.SubmitCommand("/user -register=christophe", Duration.ofSeconds(1)), ActorRef.noSender());
		terminal1.tell(new DefaultMessageProtocol.SubmitCommand("/lobby -create=fr", wait), ActorRef.noSender());
		terminal1.tell(new DefaultMessageProtocol.SubmitCommand("/lobby -join=fr", wait), ActorRef.noSender());
		terminal1.tell(new DefaultMessageProtocol.SubmitCommand("/room -create=Discussion", wait), ActorRef.noSender());
		terminal1.tell(new DefaultMessageProtocol.SubmitCommand("/room -join=Discussion", wait), ActorRef.noSender());

		terminal2.tell(new DefaultMessageProtocol.SubmitCommand("/user -register=fabienne", Duration.ofSeconds(2)), ActorRef.noSender());
		terminal2.tell(new DefaultMessageProtocol.SubmitCommand("/lobby -join=fr", wait), ActorRef.noSender());
		terminal2.tell(new DefaultMessageProtocol.SubmitCommand("/room -join=Discussion", wait), ActorRef.noSender());

		terminal3.tell(new DefaultMessageProtocol.SubmitCommand("/user -register=alan", Duration.ofSeconds(2)), ActorRef.noSender());
		terminal3.tell(new DefaultMessageProtocol.SubmitCommand("/lobby -join=fr", wait), ActorRef.noSender());
		terminal3.tell(new DefaultMessageProtocol.SubmitCommand("/room -join=Discussion", wait), ActorRef.noSender());
	}
}
