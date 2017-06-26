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

import java.util.List;

import com.heliosphere.athena.base.message.internal.IMessageProtocol;

/**
 * Message protocol for a {@link Terminal} actor.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public final class TerminalProtocol implements IMessageProtocol
{
	/**
	 * Default serialization identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Message used by a {@link Terminal} to display messages on the console.
	 * <hr>
	 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
	 * @version 1.0.0
	 */
	public final static class DisplayOnTerminal
	{
		/**
		 * List of lines to display on the terminal.
		 */
		private final List<String> messages;

		/**
		 * Creates a new message.
		 * <hr>
		 * @param messages List of message to display on the terminal.
		 */
		public DisplayOnTerminal(List<String> messages)
		{
			this.messages = messages;
		}

		/**
		 * Returns the messages that have to been displayed on the terminal.
		 * <hr>
		 * @return List of messages.
		 */
		public final List<String> getMessages()
		{
			return messages;
		}
	}
}
