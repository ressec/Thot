/*
 * Copyright(c) 2010-2013 Heliosphere Ltd.
 * ---------------------------------------------------------------------------
 * This file is part of the Heliosphere's Thot project which is licensed 
 * under the Apache license version 2 and use is subject to license terms. 
 * You should have received a copy of the license with the project artefact 
 * binaries and/or sources.
 * 
 * License can be consulted at http://www.apache.org/licenses/LICENSE-2.0
 * ---------------------------------------------------------------------------
 */
package org.heliosphere.thot.hazelcast.test.persistence.h2.performance;

import java.util.Map;

import org.heliosphere.thot.hazelcast.persistence.h2.BerkeleyStoreMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * A performance test case for the {@link BerkeleyStoreMap}.
 * <hr>
 * @author Resse Christophe, Heliosphere Corp. 2012-2013
 * @version 1.0.0
 */
public final class MapPersistencePerformanceTest
{
	private final static int iterations = 200000;
	private static HazelcastInstance instance1 = null;
	private static HazelcastInstance instance2 = null;

	/**
	 * Initialization of the test cases.
	 * <p>
	 * @throws Exception In case an error occurs during the initialization.
	 */
	@SuppressWarnings("nls")
	@BeforeClass
	public static final void setUpBeforeClass() throws Exception
	{
		instance1 = Hazelcast.newHazelcastInstance();
		instance2 = Hazelcast.newHazelcastInstance();

		instance1.getMap("default").clear(); // Ensure the datastore is empty!
	}

	/**
	 * Finalization of the test cases.
	 * <p>
	 * @throws Exception In case an error occurs during the finalization.
	 */
	@AfterClass
	public static final void tearDownAfterClass() throws Exception
	{
	}

	/**
	 * Sets up the fixture.
	 * <p>
	 * @throws Exception In case an error occurs during the setup phase.
	 */
	@Before
	public final void setUp() throws Exception
	{
	}

	/**
	 * Tears down the fixture.
	 * <p>
	 * @throws Exception In case an error occurs during the tear down phase.
	 */
	@After
	public final void tearDown() throws Exception
	{
	}

	/**
	 * Tests the persistence of thousands of records in the 'default' map ; i.e.
	 * records are persisted to the underlying datastore.
	 */
	@SuppressWarnings({ "static-method", "nls" })
	@Test
	public final void save()
	{
		Map<Integer, Integer> map = instance1.getMap("default");

		// Store thousands of records in the map and persist them in the datastore.
		for (int i = 0; i < iterations; i++)
		{
			map.put(Integer.valueOf(i), Integer.valueOf(i));
		}

		// Shutdown the only node of the cluster so that, we will see if records have been persisted.
		instance1.getLifecycleService().shutdown();

		// This should force the loading from the underlying datastore of our records.
		map = instance2.getMap("default");

		Assert.assertTrue(map.size() == iterations);
	}
}
