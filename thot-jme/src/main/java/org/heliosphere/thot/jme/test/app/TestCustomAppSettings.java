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
package org.heliosphere.thot.jme.test.app;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.prefs.BackingStoreException;

import com.jme3.scene.Mesh;
import com.jme3.system.AppSettings;

public class TestCustomAppSettings {

	private static final String APPSETTINGS_KEY = "JME_AppSettingsTest";

	private static void assertEqual(Object a, Object b) {
		if (!a.equals(b)){
			throw new AssertionError();
		}
	}

	/**
	 * Tests preference based AppSettings.
	 */
	private static void testPreferenceSettings() {
		AppSettings settings = new AppSettings(false);
		settings.putBoolean("TestBool", true);
		settings.putInteger("TestInt", 123);
		settings.putString("TestStr", "HelloWorld");
		settings.putFloat("TestFloat", 123.567f);
		settings.put("TestObj", new Mesh()); // Objects not supported by preferences

		try {
			settings.save(APPSETTINGS_KEY);
		} catch (BackingStoreException ex) {
			ex.printStackTrace();
		}

		AppSettings loadedSettings = new AppSettings(false);
		try {
			loadedSettings.load(APPSETTINGS_KEY);
		} catch (BackingStoreException ex) {
			ex.printStackTrace();
		}

		assertEqual(loadedSettings.getBoolean("TestBool"), true);
		assertEqual(loadedSettings.getInteger("TestInt"), 123);
		assertEqual(loadedSettings.getString("TestStr"), "HelloWorld");
		assertEqual(loadedSettings.get("TestFloat"), 123.567f);
	}

	/**
	 * Test Java properties file based AppSettings.
	 */
	private static void testFileSettings() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		AppSettings settings = new AppSettings(false);
		settings.putBoolean("TestBool", true);
		settings.putInteger("TestInt", 123);
		settings.putString("TestStr", "HelloWorld");
		settings.putFloat("TestFloat", 123.567f);
		settings.put("TestObj", new Mesh()); // Objects not supported by file settings

		try {
			settings.save(baos);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		AppSettings loadedSettings = new AppSettings(false);
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			loadedSettings.load(bais);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		assertEqual(loadedSettings.getBoolean("TestBool"), true);
		assertEqual(loadedSettings.getInteger("TestInt"), 123);
		assertEqual(loadedSettings.getString("TestStr"), "HelloWorld");
		assertEqual(loadedSettings.get("TestFloat"), 123.567f);
	}

	public static void main(String[] args){
		testPreferenceSettings();
		testFileSettings();
		System.out.println("All OK");
	}
}
