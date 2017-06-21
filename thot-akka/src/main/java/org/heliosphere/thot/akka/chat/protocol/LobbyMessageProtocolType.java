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

import org.heliosphere.thot.akka.chat.client.ChatClient;
import org.heliosphere.thot.akka.chat.lobby.Lobby;

import com.heliosphere.athena.base.message.internal.IMessageContent;
import com.heliosphere.athena.base.message.internal.IMessageType;
import com.heliosphere.athena.base.message.internal.type.MessageUsageType;

/**
 * Enumeration defining the message protocol handled by a {@link Lobby}.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse</a>
 * @version 1.0.0
 */
public enum LobbyMessageProtocolType implements IMessageType
{
	/**
	 * Message sent to a {@link Lobby} by a {@link ChatClient} to request the list of {@link Room}.
	 */
	LOBBY_ROOM_LIST(MessageUsageType.NONE, ChatData.class),

	/**
	 * Message sent to a {@link Lobby} by a {@link ChatClient} to request the creation of a room.
	 */
	LOBBY_ROOM_CREATE(MessageUsageType.NONE, ChatData.class), // Locale, Subject

	/**
	 * Message sent to a {@link Lobby} by a {@link ChatClient} to request the deletion of a {@link Room}.
	 */
	LOBBY_ROOM_DELETE(MessageUsageType.NONE, ChatData.class);

	/**
	 * Message usage type.
	 */
	private final MessageUsageType usage;

	/**
	 * Content data type class.
	 */
	private final Class<? extends IMessageContent> contentClass;

	/**
	 * Creates a new enumerated value given some values.
	 * <p>
	 * @param usage Message usage type.
	 * @param contentClass Message type content class.
	 */
	private LobbyMessageProtocolType(final MessageUsageType usage, final Class<? extends IMessageContent> contentClass)
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
}
