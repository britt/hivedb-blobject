package org.hivedb.serialization;

import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.property.Setter;
import org.hivedb.annotations.AnnotationHelper;
import org.hivedb.util.ReflectionTools;
import org.springframework.beans.BeanUtils;

/**
 * Implements Hibernate's Setter interface to deserialize blob persisted by Hibernate 
 * back to an instance of the owning class.
 *
 * @author bcrawford@cafepress.com
 *
 */
public class BlobSetter implements Setter {
	private static final long serialVersionUID = 1;
	private Log log = LogFactory.getLog(BlobSetter.class);
	public Method getMethod() {
		// optional method @see hibernate docs
		return null;
	}

	public String getMethodName() {
		// optional method @see hibernate docs
		return null;
	}

	@SuppressWarnings("unchecked")
	public void set(Object target, Object value, SessionFactoryImplementor sessionFactory) throws HibernateException {
		InputStream stream;
		try {
			stream = ((Blob) value).getBinaryStream();
			log.debug(stream);
		} catch (SQLException e) {
			throw new HibernateException(e);
		}
		Object defrosted = XmlXStreamSerializationProvider.instance().getSerializer(target.getClass()).deserialize(stream);
		Class<?> clazz = 
			ReflectionTools.whichIsImplemented(
					(Class)defrosted.getClass(), 
					(Collection<Class>)XmlXStreamSerializationProvider.instance().getSerializableInterfaces());
		if (clazz == null)
			throw new RuntimeException(String.format("Could not find a serializable interface matching defrosted class %s", defrosted.getClass()));
		for(Method get : ReflectionTools.getGetters(clazz)) {
			if(get.getDeclaringClass().equals(Object.class)
				|| AnnotationHelper.getAnnotationDeeply(clazz, ReflectionTools.getPropertyNameOfAccessor(get), SerializerIgnore.class) != null)
				continue;
			Object propertyValue;
			try {
				propertyValue = get.invoke(defrosted, new Object[]{});
			} catch (IllegalArgumentException e) {
				throw new HibernateException(e);
			} catch (IllegalAccessException e) {
				throw new HibernateException(e);
			} catch (InvocationTargetException e) {
				throw new HibernateException(e);
			}
			
			ReflectionTools.invokeSetter(target, BeanUtils.findPropertyForMethod(get).getName(), propertyValue);
		}
	}

}
