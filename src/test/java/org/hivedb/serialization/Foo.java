package org.hivedb.serialization;

import org.hivedb.annotations.Index;

public interface Foo {
	@Index
	Integer getFooId();
	void setFooId(Integer fooId);
	String getBar();
	void setBar(String bar);
}
