/**
 * 
 */
package org.hivedb.versioning;

/**
 *  The default Modernizer that does nothing.
 * @author andylikuski
 *
 * @param <T>
 */
public class IdentityModernizer<T> implements Modernizer<T> {
	public String getNewAbreviatedElementName(String abreviatedElementName) {
		return abreviatedElementName;
	}
	public String getNewElementName(String elementName) {
		return elementName;
	}
	public Object getUpdatedElementValue(String elementName, Object elementValue) {
		return elementValue;
	}
	public Boolean isDeletedElement(String elementName) {
		return false;
	}
	public T modifyInstance(T instance) {
		return instance;
	}
}