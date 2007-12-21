package org.hivedb.serialization;

import java.util.Collection;

import org.hivedb.util.PropertiesAccessor;
import org.hivedb.versioning.Modernizer;

/**
 * Describes how to translate between a class instance and XML. This interface is used as the configuration
 * by a Serializer implementation.
 * 
 * @author alikuski@cafepress.com
 */ 
public interface ClassXmlTransformer<T> {
	/**
	 *  Returns the current version of the XML blob for this class. XML retrieved of o
	 * @return
	 */
	Integer getCurrentXmlVersion();

	String abbreviate(String propertyName);
	String getClassAbbreviation();
	/**
	 *  The interface transformed by the transformer.
	 * @return
	 */
	Class<T> getRespresentedInterface();
	/**
	 *  Returns a new instance of the transformed class to be populated during deserialization
	 * @return
	 */
	T createInstance();
	/**
	 *  Optionally wraps an instance of the transformed interface in the actual implementation used for serialization. 
	 * @param instance
	 * @return
	 */
	T wrapInSerializingImplementation(T instance);
	/**
	 *  Return a collection of the required transformer instnaces, including an instance of this
	 *  implementing class. If this class has no dependencies, the list will only have one element--
	 *  and instance of this class.
	 * @return
	 */
	Collection<ClassXmlTransformer> getRequiredTransformers();
	/**
	 *  Returns an object that knows how to modernize any version of a serialized object to the modern version
	 * @return
	 */
	Modernizer<T> getModernizer(Integer fromVersion, Integer toVersion);

}
