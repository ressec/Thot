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
package org.heliosphere.thot.console.textio.tutorial;

import java.awt.Desktop;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.beryx.textio.web.TextIoApp;

/**
 * Allows executing code in a {@link org.beryx.textio.web.WebTextTerminal}
 * by configuring and initializing a {@link TextIoApp}.
 */
public class WebTextIOExecutor
{
	private Integer port;

	public WebTextIOExecutor withPort(int port)
	{
		this.port = port;
		return this;
	}

	public void execute(TextIoApp app)
	{
		Consumer<String> stopServer = sessionId -> Executors.newSingleThreadScheduledExecutor().schedule(() ->
		{
			System.exit(0);
		}, 2, TimeUnit.SECONDS);

		app.withOnDispose(stopServer).withOnAbort(stopServer).withPort(port).withMaxInactiveSeconds(600).withStaticFilesLocation("public-html").init();

		String url = "http://localhost:" + app.getPort() + "/web-demo.html";
		boolean browserStarted = false;
		if (Desktop.isDesktopSupported())
		{
			try
			{
				Desktop.getDesktop().browse(new URI(url));
				browserStarted = true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		if (!browserStarted)
		{
			System.out.println("Please open the following link in your browser: " + url);
		}
	}
}
