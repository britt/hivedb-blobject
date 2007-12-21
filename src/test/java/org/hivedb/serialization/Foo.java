package org.hivedb.serialization;

import org.hivedb.annotations.GeneratedClass;
import org.hivedb.annotations.Index;

/**
 *  Demonstrates a collection class of the main test class, SimpleBlobject
 * @author alikuski@cafepress.com
 *
 */
@GeneratedClass("FooImpl")
public interface Foo {
	@Index
	Integer getFooId();
	void setFooId(Integer fooId);
	String getBar();
	void setBar(String bar);
}
