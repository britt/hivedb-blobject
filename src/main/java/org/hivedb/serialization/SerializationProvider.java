package org.hivedb.serialization;

import java.io.InputStream;
import java.util.Collection;

/**
 * Provides a repository of serializers identified by class.
 * @author bcrawford@cafepress.com
 *
 */
public interface SerializationProvider {
	public Serializer<Object, InputStream> getSerializer(Class<?> clazz);
	public Collection<Class> getSerializableInterfaces();
}
