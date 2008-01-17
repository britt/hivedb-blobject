package org.hivedb.versioning;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import org.hivedb.serialization.Serializer;
import org.hivedb.serialization.XmlXStreamSerializationProvider;
import org.hivedb.serialization.XmlXStreamSerializer;
import org.hivedb.util.GenerateInstance;
import org.hivedb.util.ReflectionTools;
import org.hivedb.util.functional.Pair;
import org.hivedb.util.functional.Transform;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class TestXmlVersioning {
	
	@BeforeClass
	public void beforeClass() {
		XmlXStreamSerializationProvider.initialize(Arrays.asList(new Class[] {NewFoo.class}), 
				Transform.toMap((Pair<Class<?>, XmlModernizationPaver<?>>[])
						new Pair[] {
								new Pair<Class<?>, XmlModernizationPaver<?>>(NewFoo.class, new NewFooXmlModernizationPaver(NewFooXmlModernizationPaver.NEW_FOO_XML_VERSION)),
								new Pair<Class<?>, XmlModernizationPaver<?>>(NewBar.class, new NewBarXmlModernizationPaver(NewFooXmlModernizationPaver.NEW_FOO_XML_VERSION))
						}));
	}
	
	@Test
	public void testBlobVersioning()
	{
		OldFoo foo = new GenerateInstance<OldFoo>(OldFoo.class).generate();
		NewFoo newFoo = serializeAndDeserializeAsNewFoo(foo);
		Collection<NewBar> newBars = newFoo.getBars();
		// Make sure our deserialized instance notes that it came from the OldFoo version, not the NewFoo version
		Assert.assertEquals(newFoo.getBlobVersion(), (Integer)1);
		// We deleted the OrientationId from the new XML version so it shouldn't be deserialized
		Assert.assertEquals(newFoo.getOrientationId(), null);
		// We deleted Bar.Border also
		for (NewBar newBar : newBars)
			Assert.assertEquals(newBar.getBorder(), null);
		// We flipped the value of Active
		Assert.assertEquals(newFoo.getActive(), (Boolean)!foo.getActive());
		// We added a new color
		Assert.assertTrue(newFoo.getColors().contains(71));
		// We set a new field in NewFoo
		Assert.assertEquals(newFoo.getGluttony(), "glutton");
		// We also set a new field in NewBar
		for (NewBar newBar : newBars)
			Assert.assertEquals(((NewBar)newBar).getShininess(), "shininess");
		
		// Reserialize to upgrade the "stored" version to the NewFoo version
		// When we deserialize, no modernizer should be called, since it's already at the newest version
		Serializer<Object, InputStream> newFooSerializer = getNewFooSerializer();
		NewFoo deserializedNewFoo = (NewFoo) newFooSerializer.deserialize(
			newFooSerializer.serialize(newFoo));
		// upgrade the version of the original to make them equal
		newFoo.setBlobVersion(NewFooXmlModernizationPaver.NEW_FOO_XML_VERSION);
		Assert.assertEquals(deserializedNewFoo, newFoo, ReflectionTools.getDifferingFields(newFoo, deserializedNewFoo, NewFoo.class).toString());
	}
	
	private NewFoo serializeAndDeserializeAsNewFoo(OldFoo oldFoo)
	{
		Serializer<OldFoo,InputStream> productSerializer = getOldFooSerializer();
		InputStream xmlInputStream = productSerializer.serialize(oldFoo);
		Serializer<Object,InputStream> newFooSerializer = getNewFooSerializer();
		return (NewFoo) newFooSerializer.deserialize(xmlInputStream);
	}

	private Serializer<OldFoo,InputStream> getOldFooSerializer() {
		return new XmlXStreamSerializer<OldFoo>(
				OldFoo.class, 
				Transform.toMap((Pair<Class<?>, XmlModernizationPaver<?>>[])new Pair[] {}));
	}
	
	private Serializer<Object,InputStream> getNewFooSerializer() {
		return XmlXStreamSerializationProvider.getInstance().getSerializer(NewFoo.class);
	}
}
