/*
 * Copyright(c) 2016 - Heliosphere Corp.
 * ---------------------------------------------------------------------------
 * This file is part of the Heliosphere's project which is licensed under the
 * Apache license version 2 and use is subject to license terms.
 * You should have received a copy of the license with the project's artifact
 * binaries and/or sources.
 *
 * License can be consulted at http://www.apache.org/licenses/LICENSE-2.0
 * ---------------------------------------------------------------------------
 */
package org.heliosphere.thot.akka.chat.server.lobby;

import com.heliosphere.athena.base.exception.AbstractCheckedException;
import com.heliosphere.athena.base.exception.IExceptionType;
import com.heliosphere.athena.base.resource.bundle.IBundle;

/**
 * Checked exception thrown to indicate an error occurred while processing a {@link LobbyActor}.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class LobbyException extends AbstractCheckedException
{
	/**
	 * Serialization identifier.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Checked exception thrown to indicate an error occurred while processing a {@link LobbyActor}.
	 */
	public LobbyException()
	{
		super();
	}

	/**
	 * Checked exception thrown to indicate an error occurred while processing a {@link LobbyActor}.
	 * <p>
	 * @param key Resource bundle key (enumerated value coming from an
	 * enumeration implementing the {@link IBundle} interface).
	 */
	public LobbyException(final Enum<? extends IBundle> key)
	{
		super(key);
	}

	/**
	 * Checked exception thrown to indicate an error occurred while processing a {@link LobbyActor}.
	 * <p>
	 * @param key Exception key (enumerated value coming from an enumeration
	 * implementing the {@link IExceptionType} interface).
	 * @param parameters List of parameters used to populate the exception
	 * message.
	 */
	public LobbyException(final Enum<?> key, final Object... parameters)
	{
		super(key, parameters);
	}

	/**
	 * Checked exception thrown to indicate an error occurred while processing a {@link LobbyActor}.
	 * <p>
	 * @param exception Parent exception.
	 */
	public LobbyException(final Exception exception)
	{
		super(exception);
	}

	/**
	 * Checked exception thrown to indicate an error occurred while processing a {@link LobbyActor}.
	 * <p>
	 * @param message Message describing the error being the cause of the raised
	 * exception.
	 */
	public LobbyException(final String message)
	{
		super(message);
	}

	/**
	 * Checked exception thrown to indicate an error occurred while processing a {@link LobbyActor}.
	 * <p>
	 * @param message Message describing the error being the cause of the raised
	 * exception.
	 * @param exception Parent exception.
	 */
	public LobbyException(final String message, final Exception exception)
	{
		super(message, exception);
	}
}
