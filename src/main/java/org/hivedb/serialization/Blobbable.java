package org.hivedb.serialization;

import java.sql.Blob;

import org.hivedb.annotations.Ignore;

public interface Blobbable {
	@Ignore
	public Blob getBlob();
	public void setBlob(Blob blob);
}
