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

import java.io.File;
import java.io.IOException;
import java.time.Month;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.beryx.textio.swing.SwingTextTerminal;

public class SimpleTextTerminal
{
	@SuppressWarnings({ "nls", "boxing" })
	public static void main(String[] args)
	{
		System.out.println(System.getProperty("textio.properties.location"));

		TextIO textIO = TextIoFactory.getTextIO();
		TextTerminal terminal = textIO.getTextTerminal();

		if (terminal instanceof SwingTextTerminal)
		{
			JFrame.setDefaultLookAndFeelDecorated(true);
			JFrame frame = ((SwingTextTerminal) terminal).getFrame();
			frame.setTitle("Heliosphere Terminal");
			//			Image image = Toolkit.getDefaultToolkit().getImage("/Users/Christophe/Icons/Eclipse/welcome_item.gif");
			//			frame.setIconImage(image);
			//			frame.setVisible(true);

			ImageIcon icon = new ImageIcon("/Users/Christophe/Icons/Eclipse/welcome_item.gif");
			frame.setIconImage(icon.getImage());
			frame.repaint();
			frame.setVisible(true);

			try
			{
				frame.setIconImage(ImageIO.read(new File("/Users/Christophe/Icons/Eclipse/welcome_item.gif")));
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			frame.setAlwaysOnTop(true);
			//			frame.setSize(2000, 2000);
		}

		//		terminal.getProperties().put("input.font.family", "Menlo");
		//		terminal.getProperties().put("input.font.size", "12");
		//		terminal.getProperties().put("prompt.font.family", "Menlo");
		//		terminal.getProperties().put("prompt.font.size", "12");

		String user = textIO.newStringInputReader().withDefaultValue("admin").read("Username");

		String password = textIO.newStringInputReader().withMinLength(6).withInputMasking(true).read("Password");

		int age = textIO.newIntInputReader().withMinVal(13).read("Age");

		Month month = textIO.newEnumInputReader(Month.class).read("What month were you born in?");

		terminal.printf("\nUser %s is %d years old, was born in %s and has the password %s.\n", user, age, month, password);
	}
}
