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
import lombok.NonNull;

public final class ChatMessageData implements IMessageContent
{
	/**
	 * Locale for the lobby.
	 */
	private Locale locale;

	/**
	 * Room name or subject.
	 */
	private String roomName;

	/**
	 * Client's user name.
	 */
	private String userName;

	/**
	 * Message.
	 */
	private String message;

	/**
	 * Lobby reference.
	 */
	private ActorRef lobby;

	/**
	 * Room reference.
	 */
	private ActorRef room;

	public final String getUserName()
	{
		return userName;
	}

	public final void setUserName(final @NonNull String userName)
	{
		this.userName = userName;
	}

	public final Locale getLocale()
	{
		return locale;
	}

	public final void setLocale(Locale locale)
	{
		this.locale = locale;
	}

	public final String getRoomName()
	{
		return roomName;
	}

	public final void setRoomName(String roomName)
	{
		this.roomName = roomName;
	}

	public final String getMessage()
	{
		return message;
	}

	public final void setMessage(String message)
	{
		this.message = message;
	}

	public final ActorRef getLobby()
	{
		return lobby;
	}

	public final void setLobby(ActorRef lobby)
	{
		this.lobby = lobby;
	}

	public final ActorRef getRoom()
	{
		return room;
	}

	public final void setRoom(ActorRef room)
	{
		this.room = room;
	}
}
