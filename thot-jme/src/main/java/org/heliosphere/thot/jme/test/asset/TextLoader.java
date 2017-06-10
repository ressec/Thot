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
package org.heliosphere.thot.jme.test.asset;

import java.io.IOException;
import java.util.Scanner;

import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;

/**
 * An example implementation of {@link AssetLoader} to load text
 * files as strings.
 */
public class TextLoader implements AssetLoader {
	@Override
	public Object load(AssetInfo assetInfo) throws IOException {
		Scanner scan = new Scanner(assetInfo.openStream());
		StringBuilder sb = new StringBuilder();
		try {
			while (scan.hasNextLine()) {
				sb.append(scan.nextLine()).append('\n');
			}
		} finally {
			scan.close();
		}
		return sb.toString();
	}
}
