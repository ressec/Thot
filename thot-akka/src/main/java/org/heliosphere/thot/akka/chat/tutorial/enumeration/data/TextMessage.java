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

/**
 * Represents a text message in the chat application. This POJO is used as message's data.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class TextMessage implements ITextMessage
{
	/**
	 * Default serialization identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Sender.
	 */
	private IUser sender;

	/**
	 * Recipient.
	 */
	private IUser recipient;

	/**
	 * Text of the message.
	 */
	private String text;

	/**
	 * Creates a new text message.
	 * <hr>
	 * @param text Text of the message.
	 * @param sender Sender of the message.
	 */
	public TextMessage(String text, IUser sender)
	{
		this.text = text;
		this.sender = sender;
	}

	/**
	 * Creates a new text message.
	 * <hr>
	 * @param text Text of the message.
	 * @param sender Sender of the message.
	 * @param recipient Recipient of the message.
	 */
	public TextMessage(String text, IUser sender, IUser recipient)
	{
		this.text = text;
		this.sender = sender;
		this.recipient = recipient;
	}

	@Override
	public final String getText()
	{
		return text;
	}

	@Override
	public final IUser getSender()
	{
		return sender;
	}

	@Override
	public final IUser getRecipient()
	{
		return recipient;
	}
}
