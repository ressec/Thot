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
package org.heliosphere.thot.console.textio.tutorial.model;

import java.time.Month;
import java.util.function.BiConsumer;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.heliosphere.thot.console.textio.utility.AppUtil;

/**
 * A simple application illustrating the use of TextIO.
 */
public class UserDataCollector implements BiConsumer<TextIO, String>
{
	public static void main(String[] args)
	{
		TextIO textIO = TextIoFactory.getTextIO();
		new UserDataCollector().accept(textIO, null);
	}

	@Override
	public void accept(TextIO textIO, String initData)
	{
		TextTerminal terminal = textIO.getTextTerminal();
		AppUtil.printGsonMessage(terminal, initData);

		String user = textIO.newStringInputReader().withDefaultValue("admin").read("Username");

		String password = textIO.newStringInputReader().withMinLength(6).withInputMasking(true).read("Password");

		int age = textIO.newIntInputReader().withMinVal(13).read("Age");

		Month month = textIO.newEnumInputReader(Month.class).read("What month were you born in?");

		terminal.printf("\nUser %s is %d years old, was born in %s and has the password %s.\n", user, age, month, password);

		textIO.newStringInputReader().withMinLength(0).read("\nPress enter to terminate...");
		textIO.dispose("User '" + user + "' has left the building.");
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ": reading personal data.\n" + "(Properties are initialized at start-up.\n" + "Properties file: " + getClass().getSimpleName() + ".properties.)";
	}
}
