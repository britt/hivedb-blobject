/**
 * 
 */
package org.hivedb.versioning;

import org.hivedb.annotations.GeneratedClass;
import org.hivedb.serialization.Abbreviation;

@GeneratedClass("NewBarImpl")
@Abbreviation("b")
public interface NewBar extends Bar {
	
	// We are renaming the field Rotation to Turning, and thus changing the
	// name of the XML attribute
	@Abbreviation("tn")
	Integer getTurning();
	void setTurning(Integer value);
	
	// We are adding a new field to this version of image configuration
	String getShininess();
	void setShininess(String value);
}