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
package org.heliosphere.thot.kryo.serializer;

import java.lang.reflect.InvocationHandler;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.GregorianCalendar;
import java.util.Map;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;

import de.javakaffee.kryoserializers.ArraysAsListSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptyListSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptyMapSerializer;
import de.javakaffee.kryoserializers.CollectionsEmptySetSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonListSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonMapSerializer;
import de.javakaffee.kryoserializers.CollectionsSingletonSetSerializer;
import de.javakaffee.kryoserializers.CopyForIterateCollectionSerializer;
import de.javakaffee.kryoserializers.CopyForIterateMapSerializer;
import de.javakaffee.kryoserializers.DateSerializer;
import de.javakaffee.kryoserializers.EnumMapSerializer;
import de.javakaffee.kryoserializers.EnumSetSerializer;
import de.javakaffee.kryoserializers.GregorianCalendarSerializer;
import de.javakaffee.kryoserializers.JdkProxySerializer;
import de.javakaffee.kryoserializers.KryoReflectionFactorySupport;
import de.javakaffee.kryoserializers.SynchronizedCollectionsSerializer;
import de.javakaffee.kryoserializers.UnmodifiableCollectionsSerializer;

/**
 * Kryo serializer.
 * <hr>
 * @author <a href="mailto:christophe.resse@hotmail.com">Resse Christophe</a>
 * @version 1.0.0
 */
public final class KryoSerializer
{
	/**
	 * Dedicated thread for Kryo.
	 */
	private static final ThreadLocal<Kryo> thread = new ThreadLocal<Kryo>()
	{
		@SuppressWarnings("nls")
		@Override
		protected Kryo initialValue()
		{
			Kryo kryo = new KryoReflectionFactorySupport()
			{

				@Override
				@SuppressWarnings({ "rawtypes", "unchecked" })
				public Serializer<?> getDefaultSerializer(final Class type)
				{
					if (EnumSet.class.isAssignableFrom(type))
					{
						return new EnumSetSerializer();
					}
					if (EnumMap.class.isAssignableFrom(type))
					{
						return new EnumMapSerializer();
					}
					if (Collection.class.isAssignableFrom(type))
					{
						return new CopyForIterateCollectionSerializer();
					}
					if (Map.class.isAssignableFrom(type))
					{
						return new CopyForIterateMapSerializer();
					}
					if (Date.class.isAssignableFrom(type))
					{
						return new DateSerializer(type);
					}

					return super.getDefaultSerializer(type);
				}
			};

			kryo.setRegistrationRequired(false);

			kryo.register(Arrays.asList("").getClass(), new ArraysAsListSerializer());
			kryo.register(Collections.EMPTY_LIST.getClass(), new CollectionsEmptyListSerializer());
			kryo.register(Collections.EMPTY_MAP.getClass(), new CollectionsEmptyMapSerializer());
			kryo.register(Collections.EMPTY_SET.getClass(), new CollectionsEmptySetSerializer());
			kryo.register(Collections.singletonList("").getClass(), new CollectionsSingletonListSerializer());
			kryo.register(Collections.singleton("").getClass(), new CollectionsSingletonSetSerializer());
			kryo.register(Collections.singletonMap("", "").getClass(), new CollectionsSingletonMapSerializer());
			kryo.register(BigDecimal.class, new DefaultSerializers.BigDecimalSerializer());
			kryo.register(BigInteger.class, new DefaultSerializers.BigIntegerSerializer());
			kryo.register(GregorianCalendar.class, new GregorianCalendarSerializer());
			kryo.register(InvocationHandler.class, new JdkProxySerializer());
			//			kryo.register(Pattern.class, new RegexSerializer());
			//			kryo.register(BitSet.class, new BitSetSerializer());
			//			kryo.register(URI.class, new URISerializer());
			//			kryo.register(UUID.class, new UUIDSerializer());

			UnmodifiableCollectionsSerializer.registerSerializers(kryo);
			SynchronizedCollectionsSerializer.registerSerializers(kryo);

			//			try
			//			{
			//				Class<?> clazz = Class.forName("org.joda.time.DateTime");
			//				Serializer<?> serializer = (Serializer<?>) Class.forName("de.javakaffee.kryoserializers.jodatime.JodaDateTimeSerializer").newInstance();
			//				kryo.register(clazz, serializer);
			//			}
			//			catch (Throwable thex)
			//			{
			//			}
			//
			//			try
			//			{
			//				Class<?> clazz = Class.forName("de.javakaffee.kryoserializers.cglib.CGLibProxySerializer$CGLibProxyMarker");
			//				Serializer<?> serializer = (Serializer<?>) Class.forName("de.javakaffee.kryoserializers.cglib.CGLibProxySerializer").newInstance();
			//				kryo.register(clazz, serializer);
			//			}
			//			catch (Throwable thex)
			//			{
			//			}

			return kryo;
		}
	};

	/**
	 * Creates a new kryo serializer.
	 */
	private KryoSerializer()
	{
	}

	/**
	 * Writes the given object.
	 * <p>
	 * @param object Object to write.
	 * @return Array of bytes containing the serialized object.
	 */
	public static byte[] write(final Object object)
	{
		return write(object, -1);
	}

	/**
	 * Writes the given object.
	 * <p>
	 * @param object Object to write.
	 * @param size Maximum buffer size.
	 * @return Array of bytes containing the serialized object.
	 */
	public static byte[] write(final Object object, final int size)
	{
		Kryo kryo = thread.get();
		Output output = new Output(1024, size);
		kryo.writeClassAndObject(output, object);

		return output.toBytes();
	}

	/**
	 * Read.
	 * 
	 * @param bytes the bytes
	 * @return the object
	 */
	public static Object read(final byte[] bytes)
	{
		Kryo kryo = thread.get();
		Input input = new Input(bytes);

		return kryo.readClassAndObject(input);
	}
}