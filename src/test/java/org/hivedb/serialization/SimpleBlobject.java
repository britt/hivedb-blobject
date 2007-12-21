package org.hivedb.serialization;


import java.util.Collection;
import java.util.Date;

import org.hivedb.annotations.EntityId;
import org.hivedb.annotations.PartitionIndex;
import org.hivedb.annotations.Resource;
import org.hivedb.util.HiveUtils;

/**
 * Demonstrates a class persisted as a blob. Also demonstrates storing a collection class (Foo)
 * as an index table in Hibernate while storing the full instances as part of the blob.
 * @author andylikuski
 *
 */
@Resource("Blobject")
public class SimpleBlobject extends Blobject {
	private Integer id;
	private String mapped;
	private Date notMapped;
	
	public SimpleBlobject(){}
	
	public SimpleBlobject(Integer id, String mapped, Date notMapped) {
		super();
		this.id = id;
		this.mapped = mapped;
		this.notMapped = notMapped;
	}

	@EntityId
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	@PartitionIndex
	public String getMapped() {
		return mapped;
	}
	public void setMapped(String mapped) {
		this.mapped = mapped;
	}
	public Date getNotMapped() {
		return notMapped;
	}
	public void setNotMapped(Date notMapped) {
		this.notMapped = notMapped;
	}
	
	private Collection<Foo> foos;
	public Collection<Foo> getFoos() {
		return foos;
	}
	
	public void setFoos(Collection<Foo> foos) {
		this.foos = foos;
	}	
	
	
	@Override
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode();
	}

	@Override
	public int hashCode() {
		return HiveUtils.makeHashCode(id, mapped, notMapped);
	}
}
