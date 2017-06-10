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
package org.heliosphere.thot.jme;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * The Class BlueCube.<br>
 * <p>
 * This is a very small tutorial on jme (jMonkeyEngine) to show you how it is
 * easy to create a 3D scene. It creates a simple box with a blue color.
 * When starting, the application provides some settings to the user (resolution, 
 * color depth, refresh rate, anti-aliasing).
 * The cube is moveable by using the arrow keys of the keyboard. 
 * The main windows displays a 2D summary of the scene information (fps, triangles, 
 * vertices, etc...)
 * <p>
 * @author Resse Christophe - Heliosphere Corporation.
 */
public class BlueCube extends SimpleApplication 
{
	/**
	 * The main method.
	 * <p>
	 * @param args The arguments passed on the command line.
	 */
	public static void main(String[] args) 
	{
		BlueCube app = new BlueCube();
		app.start();
	}

	@SuppressWarnings("nls")
	@Override
	public void simpleInitApp() 
	{
		Box b = new Box(1, 1, 1);
		Geometry geom = new Geometry("Box", b);

		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		geom.setMaterial(mat);

		rootNode.attachChild(geom);
	}

	@Override
	public void simpleUpdate(float tpf) 
	{
		//TODO: add update code
	}

	@Override
	public void simpleRender(RenderManager rm) 
	{
		//TODO: add render code
	}
}
