package org.hivedb.serialization;


import java.util.Arrays;
import java.util.Collection;

import org.hivedb.Schema;
import org.hivedb.meta.persistence.TableInfo;

public class SimpleBlobjectSchema extends Schema {
	
	private static SimpleBlobjectSchema INSTANCE = new SimpleBlobjectSchema();
	
	private SimpleBlobjectSchema(){
		super("blobject");
	}

	@Override
	public Collection<TableInfo> getTables(String uri) {
		return Arrays.asList(
			new TableInfo(
				"BLOBJECT", 
				"CREATE TABLE BLOBJECT (" +
				"ID INT NOT NULL PRIMARY KEY, " +
				"MAPPED VARCHAR(255));"),
			new TableInfo(
				"BLOBJECT_BLOB", 
				"CREATE TABLE BLOBJECT_BLOB (" + 
				"ID INT NOT NULL PRIMARY KEY, " + 
				"DATA BLOB);"),
			new TableInfo(
					"BLOBJECT_FOO", 
					"CREATE TABLE BLOBJECT_FOO (" + 
					"ID INT NOT NULL, " + 
					"FOO_ID INT NOT NULL);")
		);
	}
	
	public static SimpleBlobjectSchema getInstance() {
		return INSTANCE;
	}

}
