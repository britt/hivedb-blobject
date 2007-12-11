package org.hivedb.serialization;

import java.sql.Blob;

import org.hivedb.annotations.Ignore;

public class Blobject implements Blobbable {
	protected Blob blob;

	@Ignore
	public Blob getBlob() {
		return blob;
	}
	public void setBlob(Blob blob) {
		this.blob = blob;
	}
}
