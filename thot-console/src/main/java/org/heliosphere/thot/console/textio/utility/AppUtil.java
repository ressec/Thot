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
package org.heliosphere.thot.console.textio.utility;

import org.beryx.textio.TextTerminal;

import com.google.gson.Gson;

/**
 * Utility class dedicated to the console.
 * <hr>
 * @author Resse Christophe - Heliosphere Corp.
 */
public class AppUtil
{
	/**
	 * Prints a JSon message to a text terminal.
	 * <hr>
	 * @param terminal Terminal where to print the message.
	 * @param data Json message to print.
	 */
	public static void printGsonMessage(TextTerminal terminal, String data)
	{
		if (data != null && !data.isEmpty())
		{
			String message = new Gson().fromJson(data, String.class);
			if (message != null && !message.isEmpty())
			{
				terminal.println(message);
			}
		}
	}
}
