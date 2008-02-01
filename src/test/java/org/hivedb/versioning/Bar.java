package org.hivedb.versioning;

import org.hivedb.annotations.Index;
import org.hivedb.serialization.Abreviation;

public interface Bar {
	@Index
	@Abreviation("i")
	Integer getId();
	@Abreviation("a")
	Short getAlignment();
	@Abreviation("p")
	Short getPosition();
	@Abreviation("h")
	Float getHeight();
	@Abreviation("r")
	Integer getRotation();
	@Abreviation("b")
	Float getBorder();
	@Abreviation("k")
	String getKey();
}
