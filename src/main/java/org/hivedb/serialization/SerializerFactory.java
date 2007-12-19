package org.hivedb.serialization;

import java.io.InputStream;
import java.util.Map;

public class SerializerFactory {
	public static <T> Serializer<T, InputStream> createInstance(
			Class<T> representedInterface,
			Map<Class<?>, XmlModernizationPaver<?>> xmlModernizationPaverMap)
	{
		return new XmlXStreamSerializer<T>(representedInterface, xmlModernizationPaverMap);
	}
}
