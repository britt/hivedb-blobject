package org.hivedb.serialization;

import java.sql.Blob;

import org.apache.cxf.aegis.type.java5.IgnoreProperty;
import org.hivedb.annotations.EntityVersion;
import org.hivedb.annotations.Ignore;

/***
 *  Used in conjunction with BlobAccessor to enable blob serialization of a class via Hibernate.
 *  The property blob is a dummy property needed by Hibernate due to a design defect. See
 *  the org.hivedb.serialization tests for examples.
 *  
 *  blobVersion:
 *  Indicates the version of the blob that is stored in the database. This version may be different
 *  than the newest blob version to indicate the delta in data structure between the stored blob and
 *  the deserialized classed. For instance, the blob may lack a property added to the class in a later
 *  version. See the org.hivedb.versioning tests for examples.
 *  
 * @author alikuski@cafepress.com, bcrawford@cafepress.com 
 *
 */
public interface Blobbable {
	public final String BLOB_VERSION_ABBREVIATION = "bv";
	@IgnoreProperty
	@Ignore
	@SerializerIgnore
	Blob getBlob();
	
	@EntityVersion
	@Abbreviation(BLOB_VERSION_ABBREVIATION)
	@Ignore
	Integer getBlobVersion();
	void setBlobVersion(Integer blobVersion);
}
