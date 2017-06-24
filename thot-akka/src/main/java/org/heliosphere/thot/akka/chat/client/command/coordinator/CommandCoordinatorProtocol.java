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

import com.heliosphere.athena.base.command.internal.processor.ICommandProcessor;
import com.heliosphere.athena.base.command.internal.protocol.ICommandCodeType;

import lombok.Getter;

public final class CommandCoordinatorProtocol
{
	public final static class RegisterCommandProcesor
	{
		/**
		 * Command type.
		 */
		@Getter
		private final Enum<? extends ICommandCodeType> type;

		/**
		 * Command processor class.
		 */
		@Getter
		private final Class<? extends ICommandProcessor> commandProcessorClass;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param type Command type.
		 * @param commandProcessorClass Command processor class.
		 */
		public RegisterCommandProcesor(Enum<? extends ICommandCodeType> type, Class<? extends ICommandProcessor> commandProcessorClass)
		{
			this.type = type;
			this.commandProcessorClass = commandProcessorClass;
		}
	}

}
