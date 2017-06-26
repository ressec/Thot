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

import com.heliosphere.athena.base.command.internal.ICommand;
import com.heliosphere.athena.base.command.internal.processor.ICommandProcessor;
import com.heliosphere.athena.base.command.internal.protocol.ICommandProtocolType;

/**
 * Message protocol for a {@link CommandCoordinator} actor.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public final class CommandCoordinatorProtocol
{
	/**
	 * Message handled by a {@link CommandCoordinator} to have a command processor registered for a given command protocol type.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class RegisterCommandProcessor
	{
		/**
		 * Command type.
		 */
		private final Enum<? extends ICommandProtocolType> type;

		/**
		 * Command processor class.
		 */
		private final Class<? extends ICommandProcessor> commandProcessorClass;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param type Command type.
		 * @param commandProcessorClass Command processor class.
		 */
		public RegisterCommandProcessor(Enum<? extends ICommandProtocolType> type, Class<? extends ICommandProcessor> commandProcessorClass)
		{
			this.type = type;
			this.commandProcessorClass = commandProcessorClass;
		}

		/**
		 * Returns the command protocol type.
		 * <hr>
		 * @return Command protocol type.
		 */
		public final Enum<? extends ICommandProtocolType> getType()
		{
			return type;
		}

		/**
		 * Returns the command processor class.
		 * <hr>
		 * @return Command processor class.
		 */
		public final Class<? extends ICommandProcessor> getCommandProcessorClass()
		{
			return commandProcessorClass;
		}
	}

	/**
	 * Message handled by a {@link CommandCoordinator} to have a command executed by a command processor.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class ExecuteCommand
	{
		/**
		 * Command to execute.
		 */
		private final ICommand command;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param command Command to execute.
		 */
		public ExecuteCommand(ICommand command)
		{
			this.command = command;
		}

		/**
		 * Returns the command to execute.
		 * <hr>
		 * @return Command to execute.
		 */
		public final ICommand getCommand()
		{
			return command;
		}
	}
}
