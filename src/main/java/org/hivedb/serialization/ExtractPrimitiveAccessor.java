package org.hivedb.serialization;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.property.Getter;
import org.hibernate.property.PropertyAccessor;
import org.hibernate.property.Setter;
import org.hivedb.annotations.AnnotationHelper;
import org.hivedb.annotations.Index;
import org.hivedb.util.ReflectionTools;
import org.hivedb.util.functional.Atom;
import org.hivedb.util.functional.Transform;
import org.hivedb.util.functional.Unary;

public class ExtractPrimitiveAccessor implements PropertyAccessor {

	public Getter getGetter(Class clazz, final String propertyName)
			throws PropertyNotFoundException {

		Class collectionType = ReflectionTools.getCollectionItemType(clazz, propertyName);
		final Method indexMethod = Atom.getFirstOrThrow(AnnotationHelper.getAllMethodsWithAnnotation(collectionType, Index.class));
		return new Getter() {
		
			public Object get(Object instance) throws HibernateException {
				Collection collection = (Collection) ReflectionTools.invokeGetter(instance, propertyName);
				
				return Transform.map(new Unary<Object, Object>() {
					public Object f(Object item) {
						try {
							return indexMethod.invoke(item, new Object[] {});
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}, collection);
			}

			public Object getForInsert(Object arg0, Map arg1,
					SessionImplementor arg2) throws HibernateException {
				return null;
			}

			public Method getMethod() {
				return null;
			}

			public String getMethodName() {
				// TODO Auto-generated method stub
				return null;
			}

			public Class getReturnType() {
				return indexMethod.getReturnType();
			}
			
		};
	}

	public Setter getSetter(Class clazz, final String propertyName)
			throws PropertyNotFoundException {
		return setter;
	}
	private static Setter setter = new Setter() {
		public Method getMethod() {
			return null;
		}
		public String getMethodName() {
			return null;
		}

		public void set(Object target, Object value, SessionFactoryImplementor sessionFactory)
				throws HibernateException {
		}
	};
}
