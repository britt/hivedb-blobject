package org.hivedb.versioning;

import org.hivedb.annotations.Index;
import org.hivedb.serialization.Abbreviation;

public interface Bar {
	@Index
	@Abbreviation("i")
	Integer getId();
	@Abbreviation("a")
	Short getAlignment();
	@Abbreviation("p")
	Short getPosition();
	@Abbreviation("h")
	Float getHeight();
	@Abbreviation("r")
	Integer getRotation();
	@Abbreviation("b")
	Float getBorder();
	@Abbreviation("k")
	String getKey();
}
