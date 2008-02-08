package org.hivedb.serialization;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.hivedb.annotations.AnnotationHelper;
import org.hivedb.annotations.Ignore;
import org.hivedb.util.GeneratedInstanceInterceptor;
import org.hivedb.util.PrimitiveUtils;
import org.hivedb.util.ReflectionTools;
import org.hivedb.util.functional.Filter;
import org.hivedb.util.functional.Predicate;
import org.hivedb.util.functional.Transform;
import org.hivedb.util.functional.Unary;
import org.hivedb.util.functional.Transform.IdentityFunction;
import org.hivedb.versioning.Modernizer;
import org.hivedb.versioning.XmlModernizationPaver;
import org.hivedb.versioning.XmlModernizationPaverImpl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.SingleValueConverterWrapper;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * An XStream implementation of Serializer. Serializes and deserializes from the given class
 * and a compressed InputStream of XML. 
 * @author alikuski@cafepress.com
 *
 * @param <RAW>
 */
public class XmlXStreamSerializer<RAW> implements Serializer<RAW, InputStream> {
	private Class<RAW> clazz;
	private XStream xStream;
	private Map<Class<?>, ClassXmlTransformer<?>> classXmlTransformerMap;
	
	/**
	 * Constructs a serializer for the given interface. The implementation of the interface
	 * is generated by CGLib and named based on the @GeneratedClass attribute on the interface.
	 * If an implementation is provided instead of an interface then no generation occurs.
	 * @param clazz
	 */
	public XmlXStreamSerializer(final Class<?> clazz)
	{
		this(clazz, new Hashtable<Class<?>, XmlModernizationPaver<?>>());
	}
	
	/**
	 * Constructs a serializer like the other constructor, but additionally accepts an XmlModernizationPaver
	 * that modernizes old versions of XML to the current version. An XmlModernizationPaver is only
	 * neccessary when explicit modifications are needed to make an old blob compatible with the 
	 * current version of the class (e.g. deletion of a property.) 
	 * @param clazz
	 * @param xmlModernizationPaverMap
	 */
	@SuppressWarnings("unchecked")
	public XmlXStreamSerializer(final Class<?> clazz, final Map<Class<?>, XmlModernizationPaver<?>> xmlModernizationPaverMap)
	{
		this.clazz = (Class<RAW>) clazz;
		Collection<Class<?>> propertyTypes =  ReflectionTools.getUniqueComplexPropertyTypes(Collections.singletonList(clazz));
		classXmlTransformerMap = Transform.toMap(
				new IdentityFunction<Class<?>>(),
				new Unary<Class<?>, ClassXmlTransformer<?>>() {
					public ClassXmlTransformer<?> f(Class propertyType ) {
						Collection classes = xmlModernizationPaverMap.keySet();
						final Class whichIsImplemented = ReflectionTools.whichIsImplemented(
								propertyType,
								classes);
						return new ClassXmlTransformerImpl(
								propertyType, 
								whichIsImplemented != null
									? xmlModernizationPaverMap.get(whichIsImplemented)
									: XmlModernizationPaverImpl.getDefaultXmlModernizationPaver());
				}},
				propertyTypes);
		
		this.xStream = new XStream();
		for (ClassXmlTransformer<?> classXmlTransformer : classXmlTransformerMap.values()) {
			xStream.registerConverter(new ClassConverter(classXmlTransformer));
			Class generatedClass = GeneratedInstanceInterceptor.getGeneratedClass(classXmlTransformer.getRespresentedInterface());
			xStream.alias(classXmlTransformer.getClassAbbreviation(), generatedClass);
		}
	}
	
	/**
	 * Serializes the given instance to a compressed XML InputStream. Compression of the XML is a two
	 * step process. First the element names are abbreviated using the @Abbreviate element on each
	 * class name or getter, or using the default deterministic abbreviation algorithm. Second the
	 * XML is streamed and compressed using GZIP.
	 */
	@SuppressWarnings("unchecked")
	public InputStream serialize(final RAW raw) {	
		RAW objectToSerialize = (RAW) resolveClassXmlTransformer(raw).wrapInSerializingImplementation(raw);
		final String xml = xStream.toXML(objectToSerialize);
		return Compression.compress(xml);
	}
	
	/**
	 * Deserializes the given InputStream by decompressing the GZIP to the underlying XML and then
	 * elongating the abbreviated XML element names to their full class and property names. During
	 * deserialization, the given XMLModernizationPaver may modify various element names or their
	 * values to bring the XML data up to date with the current version of the class. 
	 * 
	 * Additionally, if properties exist in the class that are not present in the deserialized XML,
	 * the properties will be initialized to empty lists if they are collections and left null otherwise.
	 * It is ideal to have no null properties, so in the future all fields may require initialization
	 * to default values.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public RAW deserialize(InputStream serial) {
		return (RAW)xStream.fromXML(Compression.decompress(serial));
	}
	
	private ClassXmlTransformer resolveClassXmlTransformer(final Object instance) {
		return classXmlTransformerMap.get(
			Filter.grepSingle(new Predicate<Class>() {
				public boolean f(Class classXmlClass) {
					return ReflectionTools.doesImplementOrExtend(instance.getClass(), classXmlClass);
				}},
				classXmlTransformerMap.keySet()));
	}
	
	public class ClassConverter implements Converter {    

		private ClassXmlTransformer classXmlTransformer;
		public ClassConverter(ClassXmlTransformer classXmlTransformer) {
			this.classXmlTransformer = classXmlTransformer;
		}
		
		@SuppressWarnings("unchecked")
		public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {	
			marshalAttributes(source, writer);
	       	marshalNodes(source, writer, context);			
	    }

		@SuppressWarnings("unchecked")
		private void marshalAttributes(Object source, HierarchicalStreamWriter writer) {
	       	final Class<?> respresentedInterface = classXmlTransformer.getRespresentedInterface();
			for (String propertyName : grepNotIgnored(respresentedInterface, Filter.grepUnique(ReflectionTools.getPropertiesOfPrimitiveGetters(respresentedInterface))))
	       	{		
	       		Object value = ReflectionTools.invokeGetter(source, propertyName);
	       		Class<?> fieldClass = ReflectionTools.getPropertyType(respresentedInterface, propertyName);
	       		if (value != null)
	       			try {
	       				writer.addAttribute(
	       					classXmlTransformer.abbreviate(propertyName),
	       					((SingleValueConverterWrapper)xStream.getConverterLookup().lookupConverterForType(fieldClass)).toString(value));
	       			}
	       			catch (Exception e) {
	       				throw new RuntimeException(e);
	       			}
	       	}
		}
		
		@SuppressWarnings("unchecked")
		private void marshalNodes(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		 	final Class<?> respresentedInterface = classXmlTransformer.getRespresentedInterface();
		 	for (String propertyName : grepNotIgnored(respresentedInterface, Filter.grepUnique(ReflectionTools.getPropertiesOfComplexGetters(respresentedInterface))))
	       	{		
	       		Object value = ReflectionTools.invokeGetter(source, propertyName);
	       		if (new NullOrEmptyRejector().filter(value)) {
	       			writer.startNode(classXmlTransformer.abbreviate(propertyName));		       		
					context.convertAnother(value);
		       		writer.endNode();
	       		}
	       	}
		}
								
	    private Collection<String> grepNotIgnored(final Class<?> respresentedInterface, Collection<String> collection) {
			return Filter.grep(new Predicate<String>() {
				public boolean f(String propertyName) {
					return AnnotationHelper.getAnnotationDeeply(respresentedInterface, propertyName, SerializerIgnore.class) == null;
				}
			}, collection);
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
	    	Object instance = classXmlTransformer.createInstance();
	    	
	    	Integer xmlVersion = seekXmlVersion(reader, context, (ClassXmlTransformer<Object>) classXmlTransformer);
			UnmarshallInterceptor<Object> unmarshallInterceptor = new UnmarshalModernizer<Object>(
	    			(Modernizer<Object>) classXmlTransformer.getModernizer(xmlVersion, classXmlTransformer.getCurrentXmlVersion()));
	    	
	    	unmarshalAttributes(reader, instance, unmarshallInterceptor);		 
	    	unmarshalNodes(reader, context, instance, unmarshallInterceptor);	
	    	
			return unmarshallInterceptor.postUnmarshalInstanceModifier(instance);	    	
	    }

	    @SuppressWarnings("unchecked")
		private void unmarshalAttributes(HierarchicalStreamReader reader, Object instance, UnmarshallInterceptor<Object> unmarshalInterceptor) {
			Iterator iterator = reader.getAttributeNames();
			
			final Class<?> respresentedInterface = classXmlTransformer.getRespresentedInterface();
			Map<String,String> abbreviatedNameToPropertyName = Transform.toMap(
				new Unary<String,String>() {
					public String f(String propertyName) {
						return classXmlTransformer.abbreviate(propertyName);
					}},
				new IdentityFunction<String>(),
				ReflectionTools.getPropertiesOfPrimitiveGetters(respresentedInterface));
			
			while (iterator.hasNext())
	    	{
	    		String abreviatedAttributeName = (String)iterator.next();
	    		if (!unmarshalInterceptor.isElementedDeleted(abreviatedAttributeName)) {
		    			
		    		String updatedAbreviatedAttributeName = unmarshalInterceptor.preUnmarshalAbreviatedElementNameTransformer(abreviatedAttributeName);
		    		if (!abbreviatedNameToPropertyName.containsKey(updatedAbreviatedAttributeName))
						throw new RuntimeException(String.format("The abreviated attribute name %s is not recognized by the ClassXmlTransformer for %s", updatedAbreviatedAttributeName, respresentedInterface.getName()));
		    		String fullAttributeName = abbreviatedNameToPropertyName.get(updatedAbreviatedAttributeName);
		    		
		    		// propertyName will match fullAttributeName unless the interceptor changes it
					String propertyName = unmarshalInterceptor.preUnmarshalElementNameTransformer(
						fullAttributeName);
		    		
		    		// Assume attributes are simple converters
		    		SingleValueConverterWrapper converter = getAttributeConverter(
		    				ReflectionTools.getPropertyType(respresentedInterface, propertyName));
		    		try {
		    			ReflectionTools.invokeSetter(
		    				instance, 
		    				propertyName,
		    				unmarshalInterceptor.postUnmarshalElementValueTransformer(
		    					propertyName,
		    					converter.fromString(
		    							reader.getAttribute(abreviatedAttributeName))));
		    		}
		    		catch (Exception e) {
		    			throw new RuntimeException(String.format("Error unmarshalling attribute %s of class %s", abreviatedAttributeName, instance.getClass().getSimpleName()), e);
		    		}
	    		}
	    	}
		}
	    
		@SuppressWarnings("unchecked")
		private void unmarshalNodes(HierarchicalStreamReader reader, UnmarshallingContext context, Object instance, UnmarshallInterceptor<Object> unmarshalInterceptor) {
			final Class<?> respresentedInterface = classXmlTransformer.getRespresentedInterface();
			Map<String,String> abbreviatedNameToPropertyName = Transform.toMap(
				new Unary<String,String>() {
					public String f(String propertyName) {
						return classXmlTransformer.abbreviate(propertyName);
					}},
				new IdentityFunction<String>(),
				ReflectionTools.getPropertiesOfComplexGetters(respresentedInterface));
			
			while (reader.hasMoreChildren())
	    	{
				reader.moveDown();
				String abreviatedNodeName = reader.getNodeName();
				if (!unmarshalInterceptor.isElementedDeleted(abreviatedNodeName)) {	
					String updatedAbreviatedNodeName = unmarshalInterceptor.preUnmarshalAbreviatedElementNameTransformer(abreviatedNodeName);
					if (!abbreviatedNameToPropertyName.containsKey(updatedAbreviatedNodeName))
						throw new RuntimeException(String.format("The abreviated node name %s is not recognized by the ClassXmlTransformer of %s", updatedAbreviatedNodeName, classXmlTransformer.getRespresentedInterface().getName()));
		    		
					String fullNodeName = abbreviatedNameToPropertyName.get(updatedAbreviatedNodeName);
					// propertyName will match fullNodeName unless the interceptor changes it
					String propertyName = unmarshalInterceptor.preUnmarshalElementNameTransformer(fullNodeName);
					
			    	Class fieldClass = ReflectionTools.isCollectionProperty(
			    			respresentedInterface, 
			    			propertyName)
			    		? ArrayList.class
			    		: ReflectionTools.getPropertyType(clazz, propertyName);
			    	ReflectionTools.invokeSetter(
			    			instance,
			    			propertyName,
			    			unmarshalInterceptor.postUnmarshalElementValueTransformer(
			    				propertyName,
			    				context.convertAnother(instance,fieldClass)));
				}
		    	reader.moveUp();
	    	}
			// Initialize missing collections to empty, null is inappropriate
			for (Method getter: ReflectionTools.getCollectionGetters(respresentedInterface))
				try {
					if (getter.invoke(instance, new Object[] {}) == null)
						GeneratedInstanceInterceptor.setProperty(instance, ReflectionTools.getPropertyNameOfAccessor(getter), new ArrayList());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
		}

		final String BLOB_VERSION_ATTRIBUTE_ABREVIATION = Blobbable.BLOB_VERSION_ABBREVIATION;
	    private Integer seekXmlVersion(HierarchicalStreamReader reader, UnmarshallingContext context, ClassXmlTransformer<Object> classXmlTransformer) {
			
	    	String version = reader.getAttribute(BLOB_VERSION_ATTRIBUTE_ABREVIATION);
	    	if (version != null)
	    		context.put(BLOB_VERSION_ATTRIBUTE_ABREVIATION, version);
	    	else
	    		version = (String) context.get(BLOB_VERSION_ATTRIBUTE_ABREVIATION);
	    	if (version == null)
	    		throw new RuntimeException("No XML blob version found in XML or MarshallingContext");
	    	SingleValueConverterWrapper converter = getAttributeConverter(Integer.class);
	    	return (Integer) converter.fromString(version);
		}
	    
		private SingleValueConverterWrapper getAttributeConverter(Class fieldClass) {
			return (SingleValueConverterWrapper)xStream.getConverterLookup().lookupConverterForType(fieldClass);
		}
	    
		public boolean canConvert(Class type) {
	    	return !PrimitiveUtils.isPrimitiveClass(type)
	    	  && ReflectionTools.doesImplementOrExtend(
    				type,
    				this.classXmlTransformer.getRespresentedInterface());
	    }
	   
	}
	
	
	
	// This interface maps one-to-one to Modernizer, but is named with more general methods
	// for other possible uses.
	protected interface UnmarshallInterceptor<T>
	{
		String preUnmarshalAbreviatedElementNameTransformer(String abreviatedElementName);
		String preUnmarshalElementNameTransformer(String elementName);
		Boolean isElementedDeleted(String abreviatedElementName);
		Object postUnmarshalElementValueTransformer(String elementName, Object elementValue);
		T postUnmarshalInstanceModifier(T instance);
	}
	
	protected static class UnmarshalModernizer<T> implements UnmarshallInterceptor<T> {
		Modernizer<T> modernizer;
		public UnmarshalModernizer(Modernizer<T> modernizer)
		{
			this.modernizer = modernizer;
		}
		
		public String preUnmarshalAbreviatedElementNameTransformer(String abreviatedElementName) {
			return modernizer.getNewAbreviatedElementName(abreviatedElementName);
		}
		
		/**
	     *  Preprocesses an XML element name, either an attribute or node name, and possibly modifies the name
	     * @param elementName the name of the XML attribute or node
	     * @return The optionally modified name
	     */
	    public String preUnmarshalElementNameTransformer(String elementName) {
	    	return modernizer.getNewElementName(elementName);
		}
	    
	    /**
	     * Postproccess an object deserialized from XML, either an attribute value or a node. 
	     * @param object
	     * @return
	     */
	    public Object postUnmarshalElementValueTransformer(String elementName, Object elementValue) {
	    	return modernizer.getUpdatedElementValue(elementName, elementValue);
		}

	    /**
	     * Reports whether or not an element represented by the given name, either of an attribute or node,
	     * has been deleted and is no longer represented in the instance.
	     */
		public Boolean isElementedDeleted(String elementName) {
			return modernizer.isDeletedElement(elementName);
		}

		public T postUnmarshalInstanceModifier(T instance) {
			return modernizer.modifyInstance(instance);
		}

	
	}
	public static class NullOrEmptyRejector
	{
		public boolean filter(Object value) {
			return value != null 
				&& !(ReflectionTools.doesImplementOrExtend(value.getClass(), Collection.class) && ((Collection)value).size()==0);
		}		
	}
	public Integer getCurrentClassVersion() {
		return classXmlTransformerMap.get(clazz).getCurrentXmlVersion();
	}
}
