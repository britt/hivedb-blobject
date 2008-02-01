package org.hivedb.versioning;

import java.util.Collection;

import org.hivedb.annotations.EntityId;
import org.hivedb.annotations.PartitionIndex;
import org.hivedb.serialization.Abreviation;
import org.hivedb.serialization.Blobbable;

public interface Foo extends Blobbable {
	@EntityId
	@Abreviation("i")
	Long getId();
	void setId(Long id);
	
	@PartitionIndex("Member")
	@Abreviation("mb")
	Integer getMemberId();
	void setMemberId(Integer memberId);
	
	@Abreviation("rv")
	Short getRevision();
	void setRevision(Short revision);
	
	@Abreviation("a")
	Boolean getActive();
	void setActive(Boolean active);
	
	
	@Abreviation("cl")
	Collection<Integer> getColors();
	void setColors(Collection<Integer> colors);

	@Abreviation("o")
	Integer getOrientationId();
	void setOrientationId(Integer orientationId);
	
	@Abreviation("dt")
	Integer getDefaultTN();
	void setDefaultTN(Integer defaultTN);
}
