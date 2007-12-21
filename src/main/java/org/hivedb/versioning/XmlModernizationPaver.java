package org.hivedb.versioning;



/**
 *  Describes how to modernize the XML of a domain object from an old version to the current version.
 * @author alikuski@cafepress.com
 *
 * @param <T> the domain class
 */
public interface XmlModernizationPaver<T> {
	/**
	 * Gets the modernizer that modernizes from one XML version to another. There should be 
	 * a modernizer that modernizes from any version of the XML to the current version,
	 * until a former XML version is no longer supportable.
	 * @param fromVersion
	 * @param toVersion
	 * @return
	 */
	public Modernizer<T> getModernizer(Integer fromVersion, Integer toVersion);
	/**
	 * Gets the current XML version of the class represented by the XMLModernizationPaver.
	 * The current version should be statically declared in code or configuration.
	 * @return
	 */
	public Integer getCurrentXmlVersion();
}
