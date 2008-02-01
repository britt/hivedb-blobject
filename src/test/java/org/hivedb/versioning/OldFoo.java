package org.hivedb.versioning;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import org.hivedb.annotations.EntityId;
import org.hivedb.annotations.GeneratedClass;
import org.hivedb.annotations.Index;
import org.hivedb.annotations.IndexType;
import org.hivedb.annotations.PartitionIndex;
import org.hivedb.annotations.Resource;
import org.hivedb.serialization.Abreviation;
import org.hivedb.serialization.Blobbable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("message")
@GeneratedClass("OldFooImpl")
@Resource("Foo")
@Abreviation("f")
public interface OldFoo extends Foo, Serializable, Cloneable {
	// interface version
	static final long serialVersionUID = 1L;	

	
	@Index
	@Abreviation("bs")
	Collection<OldBar> getBars();
	void setBars(Collection<OldBar> bars);

}
