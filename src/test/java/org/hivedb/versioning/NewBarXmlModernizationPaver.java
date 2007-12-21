/**
 * 
 */
package org.hivedb.versioning;

import java.util.Map;

import org.hivedb.util.functional.Pair;
import org.hivedb.util.functional.Transform;

public class NewBarXmlModernizationPaver extends XmlModernizationPaverImpl<NewBar> {

	public NewBarXmlModernizationPaver(Integer currentXmlVersion) {
		super(currentXmlVersion, getModernizerMap());
	}

	private static Map<Integer,Modernizer<NewBar>> modernizerMap;
	@SuppressWarnings("unchecked")
	protected static Map<Integer,Modernizer<NewBar>> getModernizerMap() {
	if (modernizerMap == null)	
		modernizerMap = Transform.toMap((Pair<Integer,Modernizer<NewBar>>[])new Pair[] { 
			
			// Instructions to modernize to the current blob version
			new Pair(NewFooXmlModernizationPaver.NEW_FOO_XML_VERSION, new Modernizer<NewBar>() {
				public String getNewAbreviatedElementName(String abreviatedElementName) {
					if("r".equals(abreviatedElementName))
						// Sample abreviated name update: update the abreviated name of Rotation to current name
						return "tn";
					return abreviatedElementName;
				}
				public String getNewElementName(String elementName) {
					if("rotation".equals(elementName))
						// Sample field name update: update name of Rotation to current name
						return "turning";
					return elementName;
				}
				public Boolean isDeletedElement(String elementName) {
					return elementName.equals("border");
				}
				public Object getUpdatedElementValue(String elementName, Object elementValue) {
					return elementValue;
				}
				public NewBar modifyInstance(NewBar instance) {
					// Sample instance modification to default a new field
					((NewBar)instance).setShininess("shininess");
					return instance;
				}
			}),
		});		
		return modernizerMap;
	}
}