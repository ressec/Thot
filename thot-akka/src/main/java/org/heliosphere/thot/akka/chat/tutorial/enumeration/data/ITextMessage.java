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
package org.heliosphere.thot.akka.chat.tutorial.enumeration.data;

import com.heliosphere.athena.base.message.internal.IMessageContent;

/**
 * Provides the behavior of a text message.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public interface ITextMessage extends IMessageContent
{
	/**
	 * Returns the text message.
	 * <hr>
	 * @return Text message.
	 */
	String getText();

	/**
	 * Returns the sender.
	 * <hr>
	 * @return Sender.
	 */
	IUser getSender();

	/**
	 * Returns the recipient.
	 * <p>
	 * If null, it's a 'say' message and the recipients are all the users in the room. If
	 * not null, it's a 'whisper' message.
	 * <hr>
	 * @return Recipient.
	 */
	IUser getRecipient();
}
