package org.hivedb.serialization;

import java.sql.Blob;

import org.hivedb.annotations.EntityVersion;
import org.hivedb.annotations.Ignore;

public interface Blobbable {
	public final String BLOB_VERSION_ABBREVIATION = "bv";
	@Ignore
	Blob getBlob();
	void setBlob(Blob blob);
	
	@EntityVersion
	@Abbreviation(BLOB_VERSION_ABBREVIATION)
	@Ignore
	Integer getBlobVersion();
	void setBlobVersion(Integer blobVersion);
}
