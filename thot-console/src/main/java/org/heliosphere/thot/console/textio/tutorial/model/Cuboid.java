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

import java.util.function.BiConsumer;

import org.beryx.textio.PropertiesConstants;
import org.beryx.textio.TerminalProperties;
import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.heliosphere.thot.console.textio.utility.AppUtil;

/**
 * A simple application illustrating the use of TextIO.
 */
public class Cuboid implements BiConsumer<TextIO, String>
{
	public static void main(String[] args)
	{
		TextIO textIO = TextIoFactory.getTextIO();
		new Cuboid().accept(textIO, null);
	}

	private static class TextProps
	{
		private final String prefix;
		private final TerminalProperties props;

		public final String color;
		public final String bgcolor;
		public final boolean bold;
		public final boolean italic;
		public final boolean underline;
		public final String style;

		public TextProps(TerminalProperties props, String prefix)
		{
			this.props = props;
			this.prefix = prefix;
			color = props.getString(propName("color"));
			bgcolor = props.getString(propName("bgcolor"));
			bold = props.getBoolean(propName("bold"), false);
			italic = props.getBoolean(propName("italic"), false);
			underline = props.getBoolean(propName("underline"), false);
			style = props.getString(propName("style"));
		}

		private String propName(String name)
		{
			return "custom." + prefix + "." + name;
		}

		public void configurePrompt()
		{
			if (color != null)
			{
				props.setPromptColor(color);
			}
			if (bgcolor != null)
			{
				props.setPromptBackgroundColor(bgcolor);
			}
			props.setPromptBold(bold);
			props.setPromptItalic(italic);
			props.setPromptUnderline(underline);
			props.put(PropertiesConstants.PROP_PROMPT_STYLE_CLASS, style);
		}

		public void configureInput()
		{
			if (color != null)
			{
				props.setInputColor(color);
			}
			if (bgcolor != null)
			{
				props.setInputBackgroundColor(bgcolor);
			}
			props.setInputBold(bold);
			props.setInputItalic(italic);
			props.setInputUnderline(underline);
			props.put(PropertiesConstants.PROP_INPUT_STYLE_CLASS, style);
		}
	}

	@Override
	public void accept(TextIO textIO, String initData)
	{
		TextTerminal terminal = textIO.getTextTerminal();
		AppUtil.printGsonMessage(terminal, initData);

		TerminalProperties props = terminal.getProperties();

		new TextProps(props, "title").configurePrompt();
		terminal.println("Cuboid dimensions");

		new TextProps(props, "length.prompt").configurePrompt();
		new TextProps(props, "length.input").configureInput();
		double length = textIO.newDoubleInputReader().withMinVal(0.0).read("Length");

		new TextProps(props, "width.prompt").configurePrompt();
		new TextProps(props, "width.input").configureInput();
		double width = textIO.newDoubleInputReader().withMinVal(0.0).read("Width");

		new TextProps(props, "height.prompt").configurePrompt();
		new TextProps(props, "height.input").configureInput();
		double height = textIO.newDoubleInputReader().withMinVal(0.0).read("Height");

		new TextProps(props, "title").configurePrompt();
		terminal.println("The volume of your cuboid is: " + length * width * height);

		new TextProps(props, "default").configurePrompt();
		textIO.newStringInputReader().withMinLength(0).read("\nPress enter to terminate...");
		textIO.dispose();
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ": computing the volume of a cuboid.\n" + "(Properties are dynamically changed at runtime using custom properties values.\n" + "Properties file: " + getClass().getSimpleName() + ".properties.)";
	}
}
