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

import com.heliosphere.athena.base.command.internal.CommandException;
import com.heliosphere.athena.base.command.internal.ICommand;
import com.heliosphere.athena.base.command.internal.type.CommandCodeType;
import com.heliosphere.athena.base.command.internal.type.CommandGroupType;
import com.heliosphere.athena.base.command.response.CommandResponse;
import com.heliosphere.athena.base.command.response.CommandStatusType;
import com.heliosphere.athena.base.command.response.ICommandResponse;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import lombok.NonNull;

/**
 * An actor for managing normal commands.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public final class NormalCommandActor extends AbstractActor
{
	/**
	 * Akka logger.
	 */
	private final LoggingAdapter LOG = Logging.getLogger(getContext().getSystem(), this);

	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
				.match(ICommand.class, command -> handleCommand(command))
				//.matchAny()
				.build();
	}

	/**
	 * Handles (normal) commands.
	 * <hr>
	 * @param command Command to process.
	 */
	protected void handleCommand(final @NonNull ICommand command)
	{
		switch ((CommandGroupType) command.getMetadata().getGroup())
		{
			case CHAT:
				handleCommandChat(command);
				break;
			case CHARACTER:
			case COMBAT:
			case EMOTE:
			case GUILD:
			case PARTY:
			case PET:
			case PVP:
			case RAID:
			case SYSTEM:
				handleCommandSystem(command);
				break;
			case TARGETING:

			default:
				break;
		}
	}

	/**
	 * Handles chat commands.
	 * <hr>
	 * @param command Command to process.
	 */
	@SuppressWarnings("nls")
	protected void handleCommandChat(final @NonNull ICommand command)
	{
		ICommandResponse response = null;

		if (command.getMetadata().getGroup() == CommandGroupType.CHAT)
		{
			switch (command.getMetadata().getName())
			{
				case "afk":
					response = new CommandResponse(command, CommandStatusType.PROCESSED);
					response.addMessage("You are now away from the keyboard");
					break;

				default:
					break;
			}
		}
		else
		{
			response = new CommandResponse(command, CommandStatusType.FAILED);
			response.addException(new CommandException("Command is not a normal chat command!"));
		}

		if (response != null)
		{
			getSender().tell(response, getSelf());
		}
	}

	/**
	 * Handles system commands.
	 * <hr>
	 * @param command Command to process.
	 */
	@SuppressWarnings("nls")
	protected void handleCommandSystem(final @NonNull ICommand command)
	{
		ICommandResponse response = null;

		if (command.getMetadata().getGroup() == CommandGroupType.SYSTEM)
		{
			switch ((CommandCodeType) command.getMetadata().getCode())
			{
				case QUIT:
					response = new CommandResponse(command, CommandStatusType.PROCESSED);

					// TODO Do necessary cleanup before shutting down the whole system.

					response.setOrder(CommandCodeType.QUIT);
					break;

				default:
					break;
			}
		}
		else
		{
			response = new CommandResponse(command, CommandStatusType.FAILED);
			response.addException(new CommandException("Command is not a normal system command!"));
		}

		if (response != null)
		{
			getSender().tell(response, getSelf());
		}
	}

	//	/**
	//	 * Handles debug commands.
	//	 * <hr>
	//	 * @param command Command to process.
	//	 */
	//	protected void handleCommandDebug(final @NonNull ICommand command)
	//	{
	//		// Empty, must be overridden by subclasses.
	//	}
	//
	//	/**
	//	 * Handles super administration commands.
	//	 * <hr>
	//	 * @param command Command to process.
	//	 */
	//	protected void handleCommandSuperAdministration(final @NonNull ICommand command)
	//	{
	//		// Empty, must be overridden by subclasses.
	//	}
	//
	//	/**
	//	 * Handles system commands.
	//	 * <hr>
	//	 * @param command Command to process.
	//	 */
	//	protected void handleCommandSystem(final @NonNull ICommand command)
	//	{
	//		// Empty, must be overridden by subclasses.
	//	}
	//
	//	/**
	//	 * Handles a command response.
	//	 * <hr>
	//	 * @param response Command response to handle.
	//	 */
	//	protected void handleResponse(final @NonNull ICommandResponse response)
	//	{
	//		// TODO Implement the method's core.
	//	}
	//
	//	/**
	//	 * Handles unknown commands.
	//	 * <hr>
	//	 * @param command Command to process.
	//	 */
	//	protected void handleCommandUnknown(final @NonNull ICommand command)
	//	{
	//		// Empty, must be overridden by subclasses.
	//	}
}
