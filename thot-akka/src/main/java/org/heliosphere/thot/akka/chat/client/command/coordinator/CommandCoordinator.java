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
package org.heliosphere.thot.akka.chat.client.command.coordinator;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.heliosphere.thot.akka.chat.client.TerminalProtocol;

import com.heliosphere.athena.base.command.internal.ICommand;
import com.heliosphere.athena.base.command.internal.coordinator.ICommandCoordinator;
import com.heliosphere.athena.base.command.internal.exception.CommandException;
import com.heliosphere.athena.base.command.internal.interpreter.ICommandInterpreter;
import com.heliosphere.athena.base.command.internal.processor.ICommandProcessor;
import com.heliosphere.athena.base.command.internal.protocol.ICommandProtocolType;
import com.heliosphere.athena.base.command.protocol.DefaultCommandCategoryType;
import com.heliosphere.athena.base.command.protocol.DefaultCommandGroupType;
import com.heliosphere.athena.base.message.internal.IMessage;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Status;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import lombok.NonNull;

/**
 * An actor responsible to coordinate commands for a {@link ChatTerminal}.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class CommandCoordinator extends AbstractActor implements ICommandCoordinator
{
	/**
	 * Akka logging adapter.
	 */
	private final LoggingAdapter LOG = Logging.getLogger(getContext().getSystem(), this);

	/**
	 * Pre-defined command processors.
	 */
	private Map<Enum<? extends ICommandProtocolType>, ICommandProcessor> processors = new HashMap<>();

	/**
	 * Command interpreter.
	 */
	private ICommandInterpreter interpreter;

	/**
	 * Static creation pattern for a {@link CommandCoordinator}.
	 * <hr>
	 * @param interpreter Command interpreter.
	 * @return {@link Props}.
	 */
	public static Props create(final ICommandInterpreter interpreter)
	{
		return Props.create(CommandCoordinator.class, interpreter);
	}

	/**
	 * Create a new command coordinator.
	 * <hr>
	 * @param interpreter Command interpreter.
	 */
	public CommandCoordinator(final ICommandInterpreter interpreter)
	{
		this.interpreter = interpreter;
	}

	@Override
	public final ICommandInterpreter getCommandInterpreter()
	{
		return interpreter;
	}

	@SuppressWarnings("nls")
	@Override
	public void postStop() throws Exception
	{
		LOG.info(getSelf() + " is terminated!");

		super.postStop();
	}

	@Override
	public final void registerProcessor(final Enum<? extends ICommandProtocolType> type, final ICommandProcessor processor)
	{
		if (processors.get(type) != null)
		{
			// TODO Throw an exception!
		}
		if (processor == null)
		{
			// TODO Throw an exception!
		}

		processors.put(type, processor);
	}

	@Override
	public Receive createReceive()
	{
		return receiveBuilder()
				.match(ICommand.class, command -> handleAndDispatchCommand(command))
				.match(CommandCoordinatorProtocol.RegisterCommandProcessor.class, message -> handleRegisterCommandProcessor(message))
				.match(CommandCoordinatorProtocol.ExecuteCommand.class, command -> handleExecuteCommand(command))
				//.matchEquals("stopIt", p -> handleStop())
				//				.matchAny(command -> handleUnknownCommand(command))
				.matchAny(o -> LOG.info("received unknown object!"))
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
		if (command.getMetadata().getProtocolCategory() == DefaultCommandCategoryType.NORMAL)
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
			CommandException exception = new CommandException(String.format("Cannot process command: %1s, expected a 'normal' command category type!", command.getMetadata().getProtocolCategory()));
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
		//		ChatMessageData data = new ChatMessageData();
		//
		//		switch ((DefaultCommandCategoryType) command.getMetadata().getProtocolCategory())
		//		{
		//			case NORMAL:
		//				switch ((DefaultCommandCodeType) command.getMetadata().getProtocolGroup())
		//				{
		//					case REGISTER_USER:
		//						data.setUserName((String) command.getParameter("username").getValues().get(0));
		//						message = Message.createRequest(command.getMetadata().getMessageType(), data);
		//						break;
		//
		//					case QUIT:
		//					case AFK:
		//					case DISPLAY_TERMINAL:
		//					case WHO:
		//
		//					default:
		//						break;
		//				}
		//				break;
		//
		//			case ADMINISTRATION:
		//			case DEBUG:
		//			case SUPER_ADMINISTRATION:
		//			case SYSTEM:
		//
		//			default:
		//				break;
		//		}

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
		switch ((DefaultCommandGroupType) command.getMetadata().getProtocolGroup())
		{
			case CHAT:
				handleCommandChat(command);
				break;

			case SYSTEM:
				handleCommandSystem(command);
				break;

			default:
				LOG.warning("Unknown command type: " + command.getMetadata().getProtocolCategory());
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
		//		switch ((DefaultCommandCodeType) command.getMetadata().getCodeType())
		//		{
		//			case REGISTER_USER:
		//				getSender().tell(convert(command), getSelf());
		//				break;
		//
		//			case QUIT:
		//			case AFK:
		//			case DISPLAY_TERMINAL:
		//			case WHO:
		//
		//			default:
		//				break;
		//		}
	}

	/**
	 * Handles system commands.
	 * <hr>
	 * @param command Command to process.
	 * @throws CommandException Thrown in case an error occurred while converting the command.
	 */
	protected void handleCommandSystem(final @NonNull ICommand command) throws CommandException
	{
		//		switch ((DefaultCommandCodeType) command.getMetadata().getCodeType())
		//		{
		//			case HELP:
		//				executeCommandProcessor(command);
		//				break;
		//
		//			case QUIT:
		//			case AFK:
		//			case DISPLAY_TERMINAL:
		//			case WHO:
		//
		//			default:
		//				break;
		//		}
	}

	//	@SuppressWarnings("unchecked")
	//	protected void executeCommandProcessor(final ICommand command) throws CommandException
	//	{
	//		ICommandProcessor processor = processors.get(command.getMetadata().getProtocolGroup());
	//		if (processor != null)
	//		{
	//			List<String> result = (List<String>) processor.execute(command);
	//			result.add("\r\n");
	//
	//			// Send lines to the terminal.
	//			getContext().getParent().tell(new TerminalProtocol.DisplayOnTerminal(result), getSelf());
	//		}
	//	}

	//	/**
	//	 * Handles system commands.
	//	 * <hr>
	//	 * @param command Command to process.
	//	 */
	//	@SuppressWarnings("nls")
	//	protected void handleCommandSystem(final @NonNull ICommand command)
	//	{
	//		ICommandResponse response = null;
	//
	//		if (command.getMetadata().getGroupType() == DefaultCommandGroupType.SYSTEM)
	//		{
	//			switch ((DefaultCommandCodeType) command.getMetadata().getCodeType())
	//			{
	//				case QUIT:
	//					response = new CommandResponse(command, CommandStatusType.PROCESSED);
	//
	//					// TODO Do necessary cleanup before shutting down the whole system.
	//
	//					response.setOrder(DefaultCommandCodeType.QUIT);
	//					break;
	//
	//				default:
	//					break;
	//			}
	//		}
	//		else
	//		{
	//			response = new CommandResponse(command, CommandStatusType.FAILED);
	//			response.addException(new CommandException("Command is not a normal system command!"));
	//		}
	//
	//		if (response != null)
	//		{
	//			getSender().tell(response, getSelf());
	//		}
	//	}

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

	/**
	 * Handles a command execution.
	 * <hr>
	 * @param command Command to execute.
	 */
	@SuppressWarnings("unchecked")
	protected void handleExecuteCommand(final @NonNull CommandCoordinatorProtocol.ExecuteCommand command)
	{
		// Do we have a command processor for this command type?
		ICommandProcessor processor = processors.get(command.getCommand().getMetadata().getProtocolType());
		if (processor != null)
		{
			try
			{
				// Execute the command through the command processor.
				Object result = processor.execute(command.getCommand());

				// Send the result to the parent actor.
				getContext().getParent().tell(new TerminalProtocol.DisplayOnTerminal((List<String>) result), getSelf());
			}
			catch (CommandException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handles message for registering a command processor.
	 * <hr>
	 * @param message Message to process.
	 */
	protected void handleRegisterCommandProcessor(final @NonNull CommandCoordinatorProtocol.RegisterCommandProcessor message)
	{
		Constructor<?> ctor;

		if (processors.get(message.getType()) != null)
		{
			// TODO throw exception this command type is already handled!
		}

		// Create an instance of the processor class.
		try
		{
			ctor = message.getCommandProcessorClass().getConstructor(Enum.class, List.class);
			ICommandProcessor processor = (ICommandProcessor) ctor.newInstance(new Object[] { message.getType(), interpreter.getCommandDefinitions() });
			processors.put(message.getType(), processor);
		}
		catch (NoSuchMethodException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}