package org.hivedb.serialization;

import java.sql.Blob;

import org.hivedb.annotations.Ignore;

/**
 * 
 * Default implementation of Blobbable. Note that the blob property is never accessed.
 * @author bcrawford@cafepress.com
 *
 */
public class Blobject implements Blobbable{
	private Blob blob;

	@Ignore
	public Blob getBlob() {
		return null;
	}
	public void setBlob(Blob blob) {
	}
	
	private Integer blobVersion;
	public Integer getBlobVersion() {
		return blobVersion;
	}
	public void setBlobVersion(Integer blobVersion) {
		this.blobVersion = blobVersion;
	}
}
