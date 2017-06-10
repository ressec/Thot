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
package org.heliosphere.thot.hazelcast.persistence.h2;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.heliosphere.thot.kryo.serializer.KryoSerializer;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.MapLoaderLifecycleSupport;
import com.hazelcast.core.MapStore;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.sleepycat.je.CheckpointConfig;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

/**
 * Implementation of the persistence of a {@link Map} for the Oracle's Berkeley
 * DB - h2 database.
 * <hr>
 * @author <a href="mailto:christophe.resse@hotmail.com">Resse Christophe</a>
 * @version 1.0.0
 * @param <K> Key of the map.
 * @param <V> Value of the map.
 */
@SuppressWarnings({ "unchecked", "nls" })
public class BerkeleyStoreMap<K, V> implements MapLoaderLifecycleSupport, MapStore<K, V>, Runnable
{
	/**
	 * Logger.
	 */
	private final ILogger LOG = Logger.getLogger(BerkeleyStoreMap.class.getName());

	/**
	 * Environment.
	 */
	private static Environment environment;

	/**
	 * Database map.
	 */
	private static Map<Object, Object> dbMap = new HashMap<>();

	/**
	 * H2 (Berkeley DB) database.
	 */
	private Database database;

	/**
	 * The synchronization interval.
	 */
	private int interval;

	/**
	 * The scheduled executor service.
	 */
	private ScheduledExecutorService executor;

	/**
	 * The {@code Hazelcast} instance.
	 */
	@SuppressWarnings("unused")
	private HazelcastInstance hazelcast;

	/**
	 * The properties file.
	 */
	private Properties properties;

	/**
	 * The map name.
	 */
	private String mapName;

	/**
	 * Initializes the Berkeley map store persister.
	 */
	protected static void initialize()
	{
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setAllowCreate(true);
		envConfig.setLocking(true);
		envConfig.setSharedCache(true);
		envConfig.setTransactional(false);
		envConfig.setCachePercent(10);
		envConfig.setConfigParam(EnvironmentConfig.LOG_FILE_MAX, "104857600");

		File file = new File(System.getProperty("user.dir", ".") + "/db/");
		if (!file.exists() && !file.mkdirs())
		{
			throw new RuntimeException("Cannot create: " + System.getProperty("user.dir", ".") + "/db/");
		}

		environment = new Environment(file, envConfig);
	}

	/**
	 * Converts a database entry to an object.
	 * <p>
	 * @param entry {@link DatabaseEntry} to be converted.
	 * @return Converted object.
	 * @throws Exception Thrown if an error occured while converting the data.
	 */
	@SuppressWarnings("static-method")
	private Object entryToObject(final DatabaseEntry entry) throws Exception
	{
		int len = entry.getSize();
		if (len == 0)
		{
			return null;
		}

		return KryoSerializer.read(entry.getData());
	}

	/**
	 * Converts an object to a database entry.
	 * <p>
	 * @param object Object to convert.
	 * @return The converted {@link DatabaseEntry}.
	 * @throws Exception Thrown if an error occured while converting the data.
	 */
	@SuppressWarnings("static-method")
	private DatabaseEntry objectToEntry(final Object object) throws Exception
	{
		byte[] bb = KryoSerializer.write(object);

		DatabaseEntry entry = new DatabaseEntry();
		entry.setData(bb);

		return entry;
	}

	@Override
	public void init(final HazelcastInstance instance, final Properties properties, final String mapName)
	{
		hazelcast = instance;
		this.properties = properties;
		this.mapName = mapName;

		if (environment == null)
		{
			initialize();
		}

		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
		dbConfig.setDeferredWrite(true);
		dbConfig.setSortedDuplicates(false);
		dbConfig.setTransactional(false);
		database = environment.openDatabase(null, mapName, dbConfig);
		dbMap.put(mapName, database);

		if (executor == null)
		{
			try
			{
				interval = Integer.parseInt(this.properties.getProperty("syncinterval"));
			}
			catch (Exception e)
			{
				interval = 3;
				LOG.log(Level.WARNING, e.getMessage(), e);
			}
			if (interval > 0)
			{
				executor = Executors.newSingleThreadScheduledExecutor();
				executor.scheduleWithFixedDelay(this, 1, interval, TimeUnit.SECONDS);
			}
		}

		LOG.log(Level.INFO, this.getClass().getCanonicalName() + ":" + mapName + ":count:" + database.count());
	}

	@Override
	public void destroy()
	{
		if (executor != null)
		{
			try
			{
				executor.shutdown();
			}
			finally
			{
				executor = null;
			}
		}

		if (database != null)
		{
			try
			{
				database.sync();
			}
			catch (Throwable ex)
			{
				LOG.log(Level.WARNING, ex.getMessage(), ex);
			}

			LOG.log(Level.INFO, this.getClass().getCanonicalName() + ":" + mapName + ":count:" + database.count());

			try
			{
				database.close();
			}
			catch (Throwable ex)
			{
				LOG.log(Level.WARNING, ex.getMessage(), ex);
			}
			finally
			{
				database = null;
				dbMap.remove(mapName);
			}
		}

		if (dbMap.size() == 0)
		{
			try
			{
				boolean anyCleaned = false;
				while (environment.cleanLog() > 0)
				{
					anyCleaned = true;
				}
				if (anyCleaned)
				{
					CheckpointConfig force = new CheckpointConfig();
					force.setForce(true);
					environment.checkpoint(force);
				}
			}
			catch (Throwable ex)
			{
				LOG.log(Level.WARNING, ex.getMessage(), ex);
			}

			try
			{
				environment.close();
			}
			catch (Throwable ex)
			{
				LOG.log(Level.WARNING, ex.getMessage(), ex);
			}
			finally
			{
				environment = null;
			}

			LOG.log(Level.INFO, this.getClass().getCanonicalName() + ":BerkeleyDB is empty!");
		}

	}

	@Override
	public void run()
	{
		try
		{
			database.sync();
		}
		catch (Throwable ex)
		{
			LOG.log(Level.SEVERE, ex.getMessage(), ex);
		}
	}

	@Override
	public V load(final K key)
	{
		try
		{
			DatabaseEntry keyEntry = objectToEntry(key);
			DatabaseEntry valueEntry = new DatabaseEntry();
			OperationStatus status = database.get(null, keyEntry, valueEntry, LockMode.DEFAULT);
			if (status == OperationStatus.SUCCESS)
			{
				return (V) entryToObject(valueEntry);
			}

			return null;
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, e.getMessage(), e);
			return null;
		}
	}

	@Override
	public void delete(final K key)
	{
		try
		{
			database.delete(null, objectToEntry(key));
			if (interval == 0)
			{
				database.sync();
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	public void deleteAll(final Collection<K> keys)
	{
		for (K key : keys)
		{
			this.delete(key);
		}
		if (interval == 0)
		{
			database.sync();
		}
	}

	@Override
	public void store(final K key, final V value)
	{
		try
		{
			DatabaseEntry keyEntry = objectToEntry(key);
			DatabaseEntry valueEntry = objectToEntry(value);
			database.put(null, keyEntry, valueEntry);
			if (interval == 0)
			{
				database.sync();
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
	}

	@Override
	public void storeAll(final Map<K, V> map)
	{
		for (K key : map.keySet())
		{
			this.store(key, map.get(key));
		}
		if (interval == 0)
		{
			database.sync();
		}
	}

	@Override
	public Map<K, V> loadAll(final Collection<K> keys)
	{
		return privateLoadAll(keys);
	}

	/**
	 * Load all the data.
	 * <p>
	 * @param keys Collection containing the keys to load.
	 * @return Map with pairs of key and value.
	 */
	private Map<K, V> privateLoadAll(final Collection<K> keys)
	{
		Map<K, V> map = new HashMap<>(keys.size());
		for (K key : keys)
		{
			map.put(key, this.load(key));
		}

		return map;
	}

	@Override
	public Set<K> loadAllKeys()
	{
		return privateLoadAllKeys();
	}

	/**
	 * Load all the keys.
	 * <p>
	 * @param keys Collection containing the keys to load.
	 * @return Set containing all the keys.
	 */
	@SuppressWarnings("rawtypes")
	private Set<K> privateLoadAllKeys()
	{
		Set<K> keys = new HashSet((int) database.count());
		Cursor cursor = null;
		try
		{
			cursor = database.openCursor(null, null);
			DatabaseEntry foundKey = new DatabaseEntry();
			DatabaseEntry foundData = new DatabaseEntry();

			while (cursor.getNext(foundKey, foundData, LockMode.DEFAULT) == OperationStatus.SUCCESS)
			{
				keys.add((K) entryToObject(foundKey));
			}
		}
		catch (Exception e)
		{
			LOG.log(Level.SEVERE, e.getMessage(), e);
		}
		finally
		{
			if (cursor != null)
			{
				cursor.close();
			}
		}

		LOG.log(Level.INFO, this.getClass().getCanonicalName() + ":" + mapName + ":loadAllKeys:" + keys.size());

		return keys;
	}
}
