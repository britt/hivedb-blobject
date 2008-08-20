package org.hivedb.versioning;

import org.hivedb.util.functional.*;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

/**
 * The default implementation of XmlModernizationPaver. This class creates an aggregate Modernizer
 * that chains together any custom modernizers that are provided to address the request for a modernizer
 * between two XML versions. If no custom modernizers are provided for a given version delta, 
 * this class will provide the IdentityModernizer which does nothing.
 * 
 * For example, if a Modernizer between version 2 and 3 is provided, and one between 5 and 7, 
 * the request getModernizer(1,7) will chain together the two given modernizers and return the aggregate.
 * On the other hand, the request getModernizer(6,7) will return the IdentityModernizer since 
 * no provided Modernizer falls within the version delta.
 *
 * @author alikuski@cafepress.com
 *
 * @param <T>
 */
public class XmlModernizationPaverImpl<T>  implements XmlModernizationPaver<T> {
	private Integer currentXmlVersion;
	private Map<Integer,Modernizer<T>> modernizerMap;
	public XmlModernizationPaverImpl(	 
			Integer currentXmlVersion,
			Map<Integer,Modernizer<T>> modernizerMap)
	{
		this.currentXmlVersion = currentXmlVersion;
		this.modernizerMap = modernizerMap;
	}
	
	public XmlModernizationPaverImpl(Integer currentXmlVersion)
	{
		this(currentXmlVersion, new Hashtable<Integer,Modernizer<T>>());
	}
	
	public static <T> XmlModernizationPaverImpl<T> getDefaultXmlModernizationPaver() {
		return new XmlModernizationPaverImpl<T>((Integer)0, new Hashtable<Integer,Modernizer<T>>());
	}
	
	public Modernizer<T> getModernizer(Integer fromVersion, Integer toVersion) {
		return chainModernizers(fromVersion,toVersion);
	}
	
	public Integer getCurrentXmlVerson() {
		return currentXmlVersion;
	}
	
	private Modernizer<T> chainModernizers(Integer fromVersion, Integer toVersion) {
		
		// Collect all modernizers in oldest to newest
		Collection<Modernizer<T>> modernizers = Transform.map(new Transform.MapKeyToValueFunction<Integer, Modernizer<T>>(modernizerMap),
							Filter.grep(new Predicate<Integer>() {
								public boolean f(Integer version) {
									return modernizerMap.containsKey(version);
								}	
							}, new NumberIterator(fromVersion, toVersion)));
		// If none exist return an IdentityModernizer
		if (modernizers.size() == 0)
			return new IdentityModernizer<T>();
		
		// Chain the modernizers together from oldest to newest and return the resulting modernizer
		return (Modernizer<T>) Amass.join(new Joiner<Modernizer<T>, Modernizer<T>>() {
			public Modernizer<T> f(final Modernizer<T> toModernizer, final Modernizer<T> fromModernizer) {
				return new Modernizer<T>() {
					public String getNewAbreviatedElementName(String abreviatedElementName) {
						return toModernizer.getNewAbreviatedElementName(
									fromModernizer.getNewAbreviatedElementName(abreviatedElementName));
					}
					public String getNewElementName(String elementName) {
						return toModernizer.getNewElementName(
									fromModernizer.getNewElementName(elementName));
					}
					public Boolean isDeletedElement(String elementName) {
						return fromModernizer.isDeletedElement(elementName) ||toModernizer.isDeletedElement(elementName);
					}
					public Object getUpdatedElementValue(String elementName, Object elementValue) {
						return toModernizer.getUpdatedElementValue(elementName,
								fromModernizer.getUpdatedElementValue(elementName,elementValue));
					}
					public T modifyInstance(T instance) {
						return toModernizer.modifyInstance(
								fromModernizer.modifyInstance(instance));
					}
					
				};
			}},
			modernizers
		);
		
	}

	public Integer getCurrentXmlVersion() {
		return currentXmlVersion;
	}
}
