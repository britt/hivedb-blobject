package org.hivedb.serialization;

/**
 * Serializes and deserializes the given types
 * @author alikuski@cafepress.com
 *
 * @param <RAW> The class type to be serialized
 * @param <SERIAL> The serialized form, e.g. Byte[], InputStream
 */
public interface Serializer<RAW,SERIAL> {
	SERIAL serialize(RAW raw);
	RAW deserialize(SERIAL serial);
	/**
	 *  The latest serialized version number.
	 * @return
	 */
	Integer getCurrentClassVersion();
}
