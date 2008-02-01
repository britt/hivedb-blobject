package org.hivedb.versioning;

import java.util.Collection;

import org.hivedb.annotations.GeneratedClass;
import org.hivedb.serialization.Abreviation;

/**
 *  A modern version of product, to which we're going to update original Product XML
 *  We will test the different types of modernization that can occur
 * @author andylikuski
 *
 */
@GeneratedClass("NewFooImpl")
@Abreviation("f")
public interface NewFoo extends Foo {
	
	// We are renaming the class field DefaultTN to DefaultThumbNail, and thus changing the
	// name of the XML attribute (See UltraModernProductXmlTransformer)
	@Abreviation("dtn")
	public Integer getDefaultThumbnail();
	public void setDefaultThumbnail(Integer value);
	
	// We are adding a new field to this version of product
	public String getGluttony();
	public void setGluttony(String value);
	
	@Abreviation("bs")
	public Collection<NewBar> getBars();
	public void setBars(Collection<NewBar> newBars);
}