package org.hivedb.serialization;

import java.io.InputStream;
import java.util.Map;

import org.hivedb.versioning.XmlModernizationPaver;

/**
 * Creates a serializer based on the given class and required modernizations to modernize old serialized
 * versions to the current version of the class
 * @author alikuski@cafepress.com
 *
 */
public class SerializerFactory {
	public static <T> Serializer<T, InputStream> createInstance(
			Class<T> representedInterface,
			Map<Class<?>, XmlModernizationPaver<?>> xmlModernizationPaverMap)
	{
		return new XmlXStreamSerializer<T>(representedInterface, xmlModernizationPaverMap);
	}
}
