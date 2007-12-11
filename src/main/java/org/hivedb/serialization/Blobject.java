package org.hivedb.serialization;

import java.sql.Blob;

public class Blobject implements Blobbable {
	protected Blob blob;

	public Blob getBlob() {
		return blob;
	}
	public void setBlob(Blob blob) {
		this.blob = blob;
	}
}
