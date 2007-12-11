package org.hivedb.serialization;

import org.hivedb.hibernate.BaseDataAccessObject;
import org.hivedb.hibernate.ConfigurationReader;
import org.hivedb.services.BaseClassDaoService;
import org.hivedb.services.ClassDaoService;
import org.hivedb.util.database.test.ClassDaoServiceTest;
import org.hivedb.util.functional.Delay;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class BlobjectDaoServiceTest extends ClassDaoServiceTest {

	@Override
	@BeforeClass
	public void initializeDataProvider() {
		XmlXStreamSerializationProvider.initialize(SimpleBlobject.class);
		addEntity(SimpleBlobject.class, new SimpleBlobjectSchema(getConnectString(getHiveDatabaseName())));
	}
	
	@Test
	public void pluginDetectable(){}
}
