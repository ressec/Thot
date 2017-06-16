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
package org.heliosphere.thot.akka.tutorial.launcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Generic {@code Akka} tutorial launcher.
 * <hr>
 * @author <a href="mailto:christophe.resse@gmail.com">Christophe Resse - Heliosphere</a>
 * @version 1.0.0
 */
public class TutorialLauncher
{
	/**
	 * Actor system name.
	 */
	@SuppressWarnings("nls")
	private final static String ACTOR_SYSTEM_NAME = "ActorSystem";

	/**
	 * Application entry point.
	 * <hr>
	 * @param args Arguments passed on the command-line.
	 */
	@SuppressWarnings({ "nls", "unchecked" })
	public static void main(String[] args)
	{
		Class<?> clazz = null;
		String systemName = ACTOR_SYSTEM_NAME;

		if (args.length < 1 || args.length > 2)
		{
			System.err.println("Argument #1 (mandatory) is the class name of the tutorial to lauch.");
			System.err.println("Argument #2 (optional) is the actor system name.");
			System.exit(-1);
		}

		if (args.length == 2)
		{
			// Set the actor system name.
			systemName = args[1];
		}

		// First argument is the class path name of the tutorial to launch.
		try
		{
			clazz = Class.forName(args[0]);
			Constructor<String> ctor = (Constructor<String>) clazz.getConstructor(String.class);
			Object object = ctor.newInstance(systemName);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvocationTargetException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NoSuchMethodException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SecurityException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
