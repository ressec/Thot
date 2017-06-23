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
package org.heliosphere.thot.akka.chat.protocol.data;

import java.util.Locale;

import com.heliosphere.athena.base.message.internal.IMessageContent;

import akka.actor.ActorRef;
import lombok.Getter;
import lombok.Setter;

public final class ChatMessageData implements IMessageContent
{
	/**
	 * Locale for the lobby.
	 */
	@Getter
	@Setter
	private Locale locale;

	/**
	 * Room name or subject.
	 */
	@Getter
	@Setter
	private String roomName;

	/**
	 * Client's user name.
	 */
	@Getter
	@Setter
	private String userName;

	/**
	 * Message.
	 */
	@Getter
	@Setter
	private String message;

	/**
	 * Lobby reference.
	 */
	@Getter
	@Setter
	private ActorRef lobby;

	/**
	 * Room reference.
	 */
	@Getter
	@Setter
	private ActorRef room;
}
