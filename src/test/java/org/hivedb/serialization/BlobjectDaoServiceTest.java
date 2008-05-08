package org.hivedb.serialization;

import org.hivedb.util.database.test.ClassDaoServiceTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Demonstrates persistence of a class to hivedb via Hibernate using blobs as the primary form
 * of storage.
 * @author bcrawford@cafepress.com
 *
 */
public class BlobjectDaoServiceTest extends ClassDaoServiceTest {

	@Override
	@BeforeClass
	public void initializeDataProvider() {
		
		XmlXStreamSerializationProvider.initialize(SimpleBlobject.class);
		addEntity(SimpleBlobject.class, SimpleBlobjectSchema.getInstance());
	}
	
	@Test
	public void pluginDetectable(){}
}
