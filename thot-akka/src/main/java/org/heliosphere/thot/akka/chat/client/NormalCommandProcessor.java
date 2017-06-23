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
package org.heliosphere.thot.akka.chat.client;

import org.heliosphere.thot.akka.chat.protocol.data.ChatMessageData;

import com.heliosphere.athena.base.command.internal.ICommand;
import com.heliosphere.athena.base.command.internal.exception.CommandException;
import com.heliosphere.athena.base.command.protocol.DefaultCommandCategoryType;
import com.heliosphere.athena.base.command.protocol.DefaultCommandCodeType;
import com.heliosphere.athena.base.command.protocol.DefaultCommandGroupType;
import com.heliosphere.athena.base.command.response.CommandResponse;
import com.heliosphere.athena.base.command.response.CommandStatusType;
import com.heliosphere.athena.base.command.response.ICommandResponse;
import com.heliosphere.athena.base.message.Message;
import com.heliosphere.athena.base.message.internal.IMessage;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import lombok.NonNull;

/**
 * An actor for managing chat normal commands for a {@link ChatTerminal}.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public final class NormalCommandProcessor extends AbstractActor
{
	/**
	 * Akka logging adapter.
	 */
	private final LoggingAdapter LOG = Logging.getLogger(getContext().getSystem(), this);

	/**
	 * Static creation pattern for a {@link NormalCommandProcessor}.
	 * <hr>
	 * @return {@link Props}.
	 */
	public static Props props()
	{
		return Props.create(NormalCommandProcessor.class);
	}

	@SuppressWarnings("nls")
	@Override
	public void postStop() throws Exception
	{
		LOG.info(getSelf() + " is terminated!");

		super.postStop();
	}

	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
				.match(ICommand.class, command -> handleAndDispatchCommand(command))
				//.matchEquals("stopIt", p -> handleStop())
				//				.matchAny(command -> handleUnknownCommand(command))
				.matchAny(o -> LOG.info("received unknown message"))
				.build();
	}

	/**
	 * Handles unknown command.
	 * <hr>
	 * @param command Command received.
	 */
	@SuppressWarnings("nls")
	private final void handleUnknownCommand(Object command)
	{
		LOG.info(this + "Received an unknown command: " + command);
	}

	/**
	 * Handles and dispatch (normal) commands.
	 * <hr>
	 * @param command Command to process.
	 */
	@SuppressWarnings("nls")
	protected void handleAndDispatchCommand(final ICommand command)
	{
		// Check the command type.
		if (command.getMetadata().getCategoryType() == DefaultCommandCategoryType.NORMAL)
		{
			try
			{
				handleNormalCommand(command);
			}
			catch (CommandException exception)
			{
				getSender().tell(new Status.Failure(exception), getSelf());
			}
		}
		else
		{
			CommandException exception = new CommandException(String.format("Cannot process command: %1s, expected a 'normal' command category type!", command.getMetadata().getCategoryType()));
			getSender().tell(new Status.Failure(exception), getSelf());
		}
	}

	/**
	 * Converts a {@link ICommand} into an {@link IMessage}.
	 * <hr>
	 * @param command Command to convert.
	 * @return Message.
	 * @throws CommandException Thrown in case an error occurred while converting the command.
	 */
	@SuppressWarnings("nls")
	private final IMessage convert(final ICommand command) throws CommandException
	{
		IMessage message = null;
		ChatMessageData data = new ChatMessageData();

		switch ((DefaultCommandCategoryType) command.getMetadata().getCategoryType())
		{
			case NORMAL:
				switch ((DefaultCommandCodeType) command.getMetadata().getCodeType())
				{
					case REGISTER_USER:
						data.setUserName((String) command.getParameter("username").getValues().get(0));
						message = Message.createRequest(command.getMetadata().getMessageType(), data);
						break;

					case QUIT:
					case AFK:
					case DISPLAY_TERMINAL:
					case WHO:

					default:
						break;
				}
				break;

			case ADMINISTRATION:
			case DEBUG:
			case SUPER_ADMINISTRATION:
			case SYSTEM:

			default:
				break;
		}

		return message;
	}

	/**
	 * Handles {@link DefaultCommandCategoryType#NORMAL} commands.
	 * <hr>
	 * @param command Command to process.
	 * @throws CommandException Thrown in case an error occurred while converting the command.
	 */
	@SuppressWarnings("nls")
	protected void handleNormalCommand(final ICommand command) throws CommandException
	{
		switch ((DefaultCommandGroupType) command.getMetadata().getGroupType())
		{
			case CHAT:
				handleCommandChat(command);
				break;

			case SYSTEM:

			default:
				LOG.warning("Unknown command type: " + command.getMetadata().getCategoryType());
				break;
		}
	}

	/**
	 * Handles chat commands.
	 * <hr>
	 * @param command Command to process.
	 * @throws CommandException Thrown in case an error occurred while converting the command.
	 */
	protected void handleCommandChat(final @NonNull ICommand command) throws CommandException
	{
		switch ((DefaultCommandCodeType) command.getMetadata().getCodeType())
		{
			case REGISTER_USER:
				getSender().tell(convert(command), getSelf());
				break;

			case QUIT:
			case AFK:
			case DISPLAY_TERMINAL:
			case WHO:

			default:
				break;
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

		if (command.getMetadata().getGroupType() == DefaultCommandGroupType.SYSTEM)
		{
			switch ((DefaultCommandCodeType) command.getMetadata().getCodeType())
			{
				case QUIT:
					response = new CommandResponse(command, CommandStatusType.PROCESSED);

					// TODO Do necessary cleanup before shutting down the whole system.

					response.setOrder(DefaultCommandCodeType.QUIT);
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
