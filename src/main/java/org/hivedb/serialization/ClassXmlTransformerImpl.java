  package org.hivedb.serialization;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import org.hivedb.util.GenerateInstance;
import org.hivedb.util.GeneratedInstanceInterceptor;
import org.hivedb.util.PropertiesAccessor;
import org.hivedb.util.PropertiesAccessorForPropertySetter;
import org.hivedb.util.PropertySetter;
import org.hivedb.util.ReflectionTools;
import org.hivedb.util.functional.Filter;
import org.hivedb.util.functional.Pair;
import org.hivedb.util.functional.Transform;
import org.hivedb.util.functional.Unary;
import org.hivedb.util.functional.Transform.MapToValueFunction;

public class ClassXmlTransformerImpl<T> implements ClassXmlTransformer<T> {

	private XmlModernizationPaver<T> xmlModernizationPaver;

	private Class<T> clazz;
	private Map<String,String> abbrevationMap;
	private String classAbbreviation;
	
	public ClassXmlTransformerImpl(Class<T> representedInterface, XmlModernizationPaver<T> xmlModernizationPaver)
	{
		this.clazz = representedInterface;
		this.xmlModernizationPaver = xmlModernizationPaver;
		this.abbrevationMap = createAbbreviationMap(
			representedInterface, 
			Filter.grepUnique(ReflectionTools.getPropertiesOfGetters(representedInterface)));
		this.classAbbreviation = abbreviate(
			representedInterface,
			representedInterface.getSimpleName().toLowerCase());
		
	}
	
	public Class<T> getRespresentedInterface() {
		return clazz;
	}
	
	@SuppressWarnings("unchecked")
	public PropertiesAccessor getPropertiesAccessor(final Object instance) {	
		return propertiesAccessorForPropertySetter;					
	}
	private final PropertiesAccessorForPropertySetter propertiesAccessorForPropertySetter = new PropertiesAccessorForPropertySetter(clazz) {
		public Object get(String propertyName, Object instance) {
			return getCurrentXmlVersion(); // ignore the stored version; we always deserialize to the newest.
		}
		public void set(final String propertyName, final Object instance, final Object value) {
			if (propertyName.equals("blobVersion"))
				((Blobbable)instance).setBlobVersion((Integer)value);
		}
	};	  
	
	public final String abbreviate(String propertyName) {
		return abbrevationMap.get(propertyName);
	}
	public final String getClassAbbreviation() {
		return classAbbreviation;
	}
	
	// Use a simple abbreviation strategy to abbreviate the collection of names.
	// If a conflict occurs an exception is thrown. This is purposely not a dynamic
	// abbreviator because we do not want existing abbreviations to change when a new
	// property is introduced.
	// TODO use annotations on the representedInterface to resolve abbreviation names.
	private Map<String,String> createAbbreviationMap(final Class<?> representedInterface, Collection<String> names) {
		Map<String,String> abbreviationMap =  Transform.toMap(Filter.getUnique(
			Transform.map(new Unary<String,Entry<String,String>>() {
				public Entry<String, String> f(String name) {					
					return new Pair<String,String>(name, abbreviate(representedInterface, name));
				}
			}, names),
			new MapToValueFunction<String,String>()));
		if (abbreviationMap.size() != names.size())
			throw new RuntimeException(String.format("Interface %s properties cannot be abbreviated. The following properties have abbreviations in use by another property %s",
					representedInterface.getSimpleName(),
					Filter.grepFalseAgainstList(abbreviationMap.keySet(),names)));
		return abbreviationMap;
	}
				
	private String abbreviate(Class<?> representedInterface, String name) {

		Abbreviation abbreviation = representedInterface.getSimpleName().toLowerCase().equals(name)
			? representedInterface.getAnnotation(Abbreviation.class)
			: getAbbreviationOfMethod(representedInterface, name);
		if (abbreviation != null)
			return abbreviation.value();
		String camelized = name.replaceAll("[^A-Z]","");
		if (camelized.length() > 0) 
			return name.substring(0,camelized.length() < 3 ? 4-camelized.length() : 1)+camelized;
		return name.length() > 4 
			? name.substring(0,4)
			: name;
	}

	private Abbreviation getAbbreviationOfMethod(Class<?> representedInterface, String name) {
		Method methodOfOwner = ReflectionTools.getGetterOfProperty(
			ReflectionTools.getOwnerOfMethod(representedInterface, name),
			name);
		return methodOfOwner.getAnnotation(Abbreviation.class);
	}
	
	public T createInstance() {
		return clazz.isInterface()
			? GeneratedInstanceInterceptor.newInstance(clazz)
			: ReflectionTools.carefreeConstructor(clazz);
	}

	public T wrapInSerializingImplementation(T instance) {
		((Blobbable)instance).setBlobVersion(getCurrentXmlVersion());
		if (instance instanceof PropertySetter)
			return instance;
		return (T) new GenerateInstance<T>(clazz).generateAndCopyProperties(instance);
	}

	public Collection<ClassXmlTransformer> getRequiredTransformers() {
		Collection<ClassXmlTransformer> transformers = new ArrayList<ClassXmlTransformer>();
		transformers.add(this);
		return transformers;
	}

	public Modernizer<T> getModernizer(Integer fromVersion, Integer toVersion) {
		return xmlModernizationPaver.getModernizer(fromVersion, toVersion);
	}

	public Integer getCurrentXmlVersion() {
		return xmlModernizationPaver.getCurrentXmlVersion();
	}
}
