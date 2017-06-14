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
package org.heliosphere.thot.akka;

import java.util.Map;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigValue;

public final class AkkaUtility
{
	/**
	 * Dumps on the console the keys and their values for a given path.
	 * <hr>
	 * @param path Path.
	 */
	@SuppressWarnings("nls")
	public final static void dumpConfigKeyFor(final Config configuration, final String path)
	{
		String aPath = path;

		if (path == null || path.isEmpty())
		{
			aPath = "root";
		}

		System.out.println(" ");
		System.out.println("Dumping entries found for path: " + aPath);
		for (Map.Entry<String, ConfigValue> entry : aPath.equals("root") ? configuration.entrySet() : configuration.withOnlyPath(path).entrySet())
		{
			System.out.println("   " + entry.getKey() + " = " + entry.getValue().render());
		}
	}
}
