/**
 * 
 */
package org.hivedb.versioning;

import java.util.List;
import java.util.Map;

import org.hivedb.util.functional.Pair;
import org.hivedb.util.functional.Transform;

public class NewFooXmlModernizationPaver extends XmlModernizationPaverImpl<NewFoo> {
	
	public static final Integer NEW_FOO_XML_VERSION = 100;
	public NewFooXmlModernizationPaver(Integer blobVersion)
	{
		super(blobVersion, getModernizerMap());
	}

	private static Map<Integer,Modernizer<NewFoo>> modernizerMap;
	@SuppressWarnings("unchecked")
	protected static Map<Integer,Modernizer<NewFoo>> getModernizerMap() {
	if (modernizerMap == null)	
		modernizerMap = Transform.toMap((Pair<Integer,Modernizer<NewFoo>>[])new Pair[] { 
			
			// Instructions to modernize to the current blob version
			new Pair(NEW_FOO_XML_VERSION, new Modernizer<NewFoo>() {
				public String getNewAbreviatedElementName(String abreviatedElementName) {
					if("dt".equals(abreviatedElementName))
						// Sample abreviated name update: update the abreviated name of DefaultTN to current name
						return "dtn";
					return abreviatedElementName;
				}
				public String getNewElementName(String elementName) {
					if("defaultTN".equals(elementName))
						// Sample field name update: update name of DefaultTN to current name
						return "defaultThumbnail";
					return elementName;
				}
				public Boolean isDeletedElement(String abreviatedElementName) {
					return abreviatedElementName.equals("o");
				}
				public Object getUpdatedElementValue(String elementName, Object elementValue) {
					if ("active".equals(elementName))
						// Sample update: flip value of "active"
						elementValue =  !((Boolean)elementValue);
					else if ("colors".equals(elementName))
						// Sample update: add a new color
						((List)elementValue).add(71);
					return elementValue;
				}
				public NewFoo modifyInstance(NewFoo instance) {
					// Sample instance modification to default a new field
					((NewFoo)instance).setGluttony("glutton");
					return instance;  
				}
			}),
		});		
		return modernizerMap;
	}
}