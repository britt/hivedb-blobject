package org.hivedb.versioning;


/**
 *  Describes how to modernize an old version of a domain class instance's XML to the modern version of the
 *  domain class. It allows for XML element name changes and deserialized value changes. This interface
 *  is used as a delegate to implementations of Serializer.
 * @author alikuski@cafepress.com
 *
 * @param <T> The domain class that is being deserialized
 */
public interface Modernizer<T> {
	/**
	 *  Modifies any attribute or node name abreviation that should be changed to a new name.
	 * @param abreviatedElementName The existing abbreviated element name to modify
	 * @return The modified abbreviated element name
	 */
	String getNewAbreviatedElementName(String abreviatedElementName);
	/**
	 *  Modifies any attribute or node full name that should be changed to a new name to correspond
	 *  to a change in field name in the domain object class.
	 * @param elementName The existing element name to modify
	 * @return The modified element name
	 */
	String getNewElementName(String elementName);
	/**
	 *  Returns true if the given attribute or node has been deleted from the new version of the XML.
	 *  This will normally occur if the corresponding domain object class property has been deleted.
	 *  Returning true indicates to the deserializer to not set the corresponding property of the
	 *  instance being deserialized.
	 * @param elementName The full name corresponding to the field of the domain class (not the abreviated
	 * XML name.)
	 * @return
	 */
	Boolean isDeletedElement(String elementName);
	/**
	 *  Modifies the value of a deserialized attribute or node.
	 * @param elementName The name corresponding to the field of the domain class (not the abreviated
	 * XML name.)
	 * @param elementValue The value that was deserialized from the XML
	 * @return The new value to be assigned to the deserialized instance's property.
	 */
	Object getUpdatedElementValue(String elementName, Object elementValue);
	/**
	 *  Modifies an instance in ways unrelated to the current deserialized properties. This is 
	 *  most naturally used to initialize a new property that did not exist in the deserialized
	 *  XML.
	 * @param instance
	 * @return
	 */
	T modifyInstance(T instance);

}
