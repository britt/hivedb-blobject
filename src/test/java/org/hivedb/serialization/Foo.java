package org.hivedb.serialization;

import org.hivedb.annotations.GeneratedClass;
import org.hivedb.annotations.Index;

@GeneratedClass("FooImpl")
public interface Foo {
	@Index
	Integer getFooId();
	void setFooId(Integer fooId);
	String getBar();
	void setBar(String bar);
}
