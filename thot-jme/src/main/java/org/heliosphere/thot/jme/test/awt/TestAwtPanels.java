/*
 * Copyright(c) 2015-2015 Heliosphere Corp.
 * ---------------------------------------------------------------------------
 * This file is part of the Heliosphere project which is licensed under the 
 * Apache license version 2 and use is subject to license terms.
 * You should have received a copy of the license with the project artefact
 * binaries and/or sources.
 * 
 * License can be consulted at http://www.apache.org/licenses/LICENSE-2.0
 * ---------------------------------------------------------------------------
 */
package org.heliosphere.thot.jme.test.awt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.system.awt.AwtPanel;
import com.jme3.system.awt.AwtPanelsContext;
import com.jme3.system.awt.PaintMode;

public class TestAwtPanels extends SimpleApplication {

	private static TestAwtPanels app;
	private static AwtPanel panel, panel2;
	private static int panelsClosed = 0;

	private static void createWindowForPanel(AwtPanel panel, int location){
		JFrame frame = new JFrame("Render Display " + location);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				if (++panelsClosed == 2){
					app.stop();
				}
			}
		});
		frame.pack();
		frame.setLocation(location, Toolkit.getDefaultToolkit().getScreenSize().height - 400);
		frame.setVisible(true);
	}

	public static void main(String[] args){
		Logger.getLogger("com.jme3").setLevel(Level.WARNING);

		app = new TestAwtPanels();
		app.setShowSettings(false);
		AppSettings settings = new AppSettings(true);
		settings.setCustomRenderer(AwtPanelsContext.class);
		settings.setFrameRate(60);
		app.setSettings(settings);
		app.start();

		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				final AwtPanelsContext ctx = (AwtPanelsContext) app.getContext();
				panel = ctx.createPanel(PaintMode.Accelerated);
				panel.setPreferredSize(new Dimension(400, 300));
				ctx.setInputSource(panel);

				panel2 = ctx.createPanel(PaintMode.Accelerated);
				panel2.setPreferredSize(new Dimension(400, 300));

				createWindowForPanel(panel, 300);
				createWindowForPanel(panel2, 700);
			}
		});
	}

	@Override
	public void simpleInitApp() {
		flyCam.setDragToRotate(true);

		Box b = new Box(Vector3f.ZERO, 1, 1, 1);
		Geometry geom = new Geometry("Box", b);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));
		geom.setMaterial(mat);
		rootNode.attachChild(geom);

		panel.attachTo(true, viewPort);
		guiViewPort.setClearFlags(true, true, true);
		panel2.attachTo(false, guiViewPort);
	}
}
