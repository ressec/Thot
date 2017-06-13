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
package org.heliosphere.thot.akka.chat.actor;

import org.apache.commons.collections4.ListUtils;

import com.heliosphere.athena.base.command.internal.ICommand;
import com.heliosphere.athena.base.command.internal.ICommandListener;
import com.heliosphere.athena.base.command.internal.type.CommandCategoryType;
import com.heliosphere.athena.base.command.internal.type.CommandCodeType;
import com.heliosphere.athena.base.command.response.ICommandResponse;
import com.heliosphere.athena.base.file.internal.FileException;
import com.heliosphere.athena.base.terminal.CommandTerminal;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import lombok.NonNull;

/**
 * A actor managing a {@code terminal}.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class TerminalActor extends AbstractActor implements ICommandListener
{
	/**
	 * Akka logger.
	 */
	private final LoggingAdapter LOG = Logging.getLogger(getContext().getSystem(), this);

	/**
	 * Terminal.
	 */
	private CommandTerminal terminal = null;

	/**
	 * Normal command processor actor.
	 */
	private ActorRef normalCommandProcessor = getContext().actorOf(Props.create(NormalCommandActor.class), "normalCommandProcessor");

	/**
	 * Creates a new terminal actor.
	 * @throws FileException 
	 */
	@SuppressWarnings("nls")
	public TerminalActor() throws FileException
	{
		terminal = new CommandTerminal("/config/command/chat-commands.xml");
		terminal.registerListener(this);
		terminal.start();
	}

	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
				//.match(ICommand.class, command -> handleCommand(command))
				.match(ICommandResponse.class, response -> handleResponse(response))
				//.matchAny()
				.build();
	}

	@Override
	public final void onCommand(ICommand command)
	{
		switch ((CommandCategoryType) command.getMetadata().getCategory())
		{
			case NORMAL:
				handleCommandNormal(command);
				break;

			case ADMINISTRATION:
				handleCommandAdministration(command);
				break;

			case DEBUG:
				handleCommandDebug(command);
				break;

			case SUPER_ADMINISTRATION:
				handleCommandSuperAdministration(command);
				break;

			case SYSTEM:
				handleCommandSystem(command);
				break;

			default:
				handleCommandUnknown(command);
				break;
		}
	}

	/**
	 * Handles normal commands.
	 * <hr>
	 * @param command Command to process.
	 */
	protected void handleCommandNormal(final @NonNull ICommand command)
	{
		normalCommandProcessor.tell(command, getSelf());
	}

	/**
	 * Handles administration commands.
	 * <hr>
	 * @param command Command to process.
	 */
	protected void handleCommandAdministration(final @NonNull ICommand command)
	{
		// Empty, must be overridden by subclasses.
	}

	/**
	 * Handles debug commands.
	 * <hr>
	 * @param command Command to process.
	 */
	protected void handleCommandDebug(final @NonNull ICommand command)
	{
		// Empty, must be overridden by subclasses.
	}

	/**
	 * Handles super administration commands.
	 * <hr>
	 * @param command Command to process.
	 */
	protected void handleCommandSuperAdministration(final @NonNull ICommand command)
	{
		// Empty, must be overridden by subclasses.
	}

	/**
	 * Handles system commands.
	 * <hr>
	 * @param command Command to process.
	 */
	protected void handleCommandSystem(final @NonNull ICommand command)
	{
		// Empty, must be overridden by subclasses.
	}

	/**
	 * Handles a command response.
	 * <hr>
	 * @param response Command response to handle.
	 */
	@SuppressWarnings("nls")
	protected void handleResponse(final @NonNull ICommandResponse response)
	{
		switch ((CommandCodeType) response.getOrder())
		{
			case DISPLAY_TERMINAL:

				// Display the status of the command execution then the exceptions and then the messages.
				terminal.getTerminal().println("[status: " + response.getStatus() + " for command: " + response.getCommand().getMetadata().getName() + "]");

				for (Exception e : ListUtils.emptyIfNull(response.getExceptions()))
				{
					terminal.getTerminal().println("   Error: " + e.getMessage());
				}

				for (String element : ListUtils.emptyIfNull(response.getMessages()))
				{
					terminal.getTerminal().println(element);
				}
				break;

			case QUIT:
				getContext().system().terminate();
				terminal.getTerminal().println("System is shutting down...");
				break;

			default:
				break;
		}

		terminal.getTerminal().println(response.getMessages());
	}

	/**
	 * Handles unknown commands.
	 * <hr>
	 * @param command Command to process.
	 */
	protected void handleCommandUnknown(final @NonNull ICommand command)
	{
		// Empty, must be overridden by subclasses.
	}
}
