package org.hivedb.versioning;

import java.util.Collection;

import org.hivedb.annotations.EntityId;
import org.hivedb.annotations.PartitionIndex;
import org.hivedb.serialization.Abbreviation;
import org.hivedb.serialization.Blobbable;

public interface Foo extends Blobbable {
	@EntityId
	@Abbreviation("i")
	Long getId();
	void setId(Long id);
	
	@PartitionIndex("Member")
	@Abbreviation("mb")
	Integer getMemberId();
	void setMemberId(Integer memberId);
	
	@Abbreviation("rv")
	Short getRevision();
	void setRevision(Short revision);
	
	@Abbreviation("a")
	Boolean getActive();
	void setActive(Boolean active);
	
	
	@Abbreviation("cl")
	Collection<Integer> getColors();
	void setColors(Collection<Integer> colors);

	@Abbreviation("o")
	Integer getOrientationId();
	void setOrientationId(Integer orientationId);
	
	@Abbreviation("dt")
	Integer getDefaultTN();
	void setDefaultTN(Integer defaultTN);
}
