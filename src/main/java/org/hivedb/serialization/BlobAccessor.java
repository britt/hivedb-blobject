package org.hivedb.serialization;

import org.hibernate.PropertyNotFoundException;
import org.hibernate.property.Getter;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.Setter;

/***
 *  Allows a Hibernate class to be stored as a blob of data rather than storing each property
 *  in an individual table column. Classes stored as blobs must declare a dummy property, such as
 *  blob (see Blobbable) with which to configure BlobAccessor in a Hibernate configuration. This
 *  property is never read or write but must be present due to a Hibernate defect. Upon saving
 *  BlobAccessor will serialize all properties of the class and store them in the designated
 *  Hibernate table as a blob. Upon retrieval BlobAccessor will deserialize the blob and populate
 *  all the properties of the class. You may additionally store individual properties as columns
 *  for indexing purposes, but only the blob will be used to populate the fields on retrieval.
 * @author bcrawford@cafepress.com
 *
 */
public class BlobAccessor implements PropertyAccessor {

	public Getter getGetter(Class clazz, String propertyName) throws PropertyNotFoundException {
		return new BlobGetter();
	}

	public Setter getSetter(Class clazz, String propertyName) throws PropertyNotFoundException {
		return new BlobSetter();
	}

}
