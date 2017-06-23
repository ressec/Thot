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
package org.heliosphere.thot.akka.chat.protocol;

import org.heliosphere.thot.akka.chat.protocol.data.ChatMessageData;

import com.heliosphere.athena.base.exception.InvalidArgumentException;
import com.heliosphere.athena.base.message.internal.IMessageContent;
import com.heliosphere.athena.base.message.internal.protocol.IMessageType;
import com.heliosphere.athena.base.message.internal.protocol.MessageUsageType;
import com.heliosphere.athena.base.message.protocol.data.DefaultMessageData;
import com.heliosphere.athena.base.resource.bundle.BundleAthenaBase;
import com.heliosphere.athena.base.resource.bundle.ResourceBundleManager;

/**
 * Enumeration defining a (default) message types for testing purpose only.
 * <p>
 * <b>Note:</b><br>
 * You should not use this protocol enumeration but define your(s) according to your needs.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse</a>
 * @version 1.0.0
 */
public enum ChatMessageType implements IMessageType
{
	/**
	 * Not associated with a message.
	 */
	NONE(MessageUsageType.NONE, null),

	/**
	 * Register a user.
	 */
	REGISTER_USER(MessageUsageType.NONE, ChatMessageData.class),

	/**
	 * Query for the server time.
	 */
	QUERY_SERVER_TIME(MessageUsageType.NONE, DefaultMessageData.class),

	/**
	 * Query for people.
	 */
	QUERY_WHO(MessageUsageType.NONE, ChatMessageData.class),

	/**
	 * Status: AFK (Away From Keyboard).
	 */
	STATUS_AFK(MessageUsageType.NONE, null);

	/**
	 * Message usage type.
	 */
	private final MessageUsageType usage;

	/**
	 * Content data type class.
	 */
	private final Class<? extends IMessageContent> contentClass;

	/**
	 * Creates a new enumerated value given a message data content class.
	 * <p>
	 * @param usage Message usage type.
	 * @param contentClass Message type content class.
	 */
	private ChatMessageType(final MessageUsageType usage, final Class<? extends IMessageContent> contentClass)
	{
		this.usage = usage;
		this.contentClass = contentClass;
	}

	@Override
	public final MessageUsageType getUsageType()
	{
		return usage;
	}

	@Override
	public final Class<? extends IMessageContent> getContentClass()
	{
		return contentClass;
	}

	/**
	 * Creates a message type from a given string representation.
	 * <p>
	 * <b>Example:</b><p> 
	 * <code>MessageType.fromString("REGISTER_USER");</code>
	 * <hr>
	 * @param value String representing the enumerated value.
	 * @return Message type.
	 */
	@Override
	public Enum<? extends IMessageType> fromString(String value)
	{
		if (value == null || value.trim().length() == 0)
		{
			throw new InvalidArgumentException(ResourceBundleManager.getMessage(BundleAthenaBase.CommandCategoryCannotBeNull));
		}

		for (ChatMessageType element : ChatMessageType.values())
		{
			if (element.name().equalsIgnoreCase(value))
			{
				return element;
			}
		}

		throw new InvalidArgumentException(ResourceBundleManager.getMessage(BundleAthenaBase.CannotCreateEnumerated, ChatMessageType.class.getName(), value));
	}
}
