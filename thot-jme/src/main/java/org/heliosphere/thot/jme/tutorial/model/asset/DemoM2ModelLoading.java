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
package org.heliosphere.thot.jme.tutorial.model.asset;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;

/**
 * The Class DemoM2ModelLoading demonstrates the loading of a M2 (World of Warcraft) mesh model.<br>
 * <p>
 * @author Resse Christophe - Heliosphere Corporation.
 */
public class DemoM2ModelLoading extends SimpleApplication 
{

	/**
	 * The main method.
	 *
	 * @param arguments The arguments passed on the command line.
	 */
	public static void main(String[] arguments) 
	{
		DemoM2ModelLoading application = new DemoM2ModelLoading();
		application.start();
	}

	@SuppressWarnings("nls")
	@Override
	public void simpleInitApp() {

		Spatial mymodel = assetManager.loadModel("asset/model/character/bloodelf/bloodelf.female.1.obj");
		rootNode.attachChild(mymodel);

		DirectionalLight sun = new DirectionalLight();
		sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)));
		sun.setColor(ColorRGBA.White);
		rootNode.addLight(sun);        
	}

	@Override
	public void simpleUpdate(float tpf) 
	{
	}

	@Override
	public void simpleRender(RenderManager manager) 
	{
	}
}
