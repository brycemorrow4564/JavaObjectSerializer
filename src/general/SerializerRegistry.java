package general;

import util.annotations.Tags;
import util.trace.port.serialization.extensible.ExtensibleSerializationTraceUtility;
import valueSerializer.ADispatchingSerializer;
import valueSerializer.DispatchingSerializer;
import valueSerializer.ValueSerializer;
import valueSerializer.Collection.ArrayListSerializer;
import valueSerializer.Collection.HashSetSerializer;
import valueSerializer.Collection.VectorSerializer;
import valueSerializer.base.BooleanSerializer;
import valueSerializer.base.DoubleSerializer;
import valueSerializer.base.FloatSerializer;
import valueSerializer.base.IntegerSerializer;
import valueSerializer.base.LongSerializer;
import valueSerializer.base.NullSerializer;
import valueSerializer.base.ShortSerializer;
import valueSerializer.base.StringSerializer;
import valueSerializer.map.HashMapSerializer;
import valueSerializer.map.HashtableSerializer;
import valueSerializer.typeIndependent.ArraySerializer;
import valueSerializer.typeIndependent.BeanSerializer;
import valueSerializer.typeIndependent.EnumSerializer;
import valueSerializer.typeIndependent.ListPatternSerializer;

import java.io.NotSerializableException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import util.annotations.Comp533Tags;

@Tags({Comp533Tags.SERIALIZER_REGISTRY})
public class SerializerRegistry {
	
	protected static HashMap<Class, ValueSerializer> 	classToValueSerializerMap; 
	protected static HashMap<Class, Class> 			classToAltDeserializerMap; 
	
	protected static DispatchingSerializer 	dispatchingSerializer; 
	
	protected static ArraySerializer 		arraySerializer; 
	protected static BeanSerializer 			beanSerializer; 
	protected static ListPatternSerializer 	listPatternSerializer; 
	protected static EnumSerializer 			enumSerializer; 
	protected static NullSerializer 			nullSerializer; 
	
	public static String NULL_HEADER = "NullClass";
	public static String NULL_VALUE = "null";
	public static String TYPE_FREE_HEADER = "TypeFreeH"; 
	public static String REFERENCE_HEADER = "ReferenceH";
	
	static {
		ExtensibleSerializationTraceUtility.setTracing();
		
		classToValueSerializerMap = new HashMap<Class, ValueSerializer>();
		classToAltDeserializerMap = new HashMap<Class, Class>();
		
		//Dispatching serializer
		registerDispatchingSerializer(new ADispatchingSerializer());
		
		//valueSerializer.base 
		registerValueSerializer(String.class, new StringSerializer());
		registerValueSerializer(Short.class, new ShortSerializer());
		registerValueSerializer(Long.class, new LongSerializer());
		registerValueSerializer(Double.class, new DoubleSerializer());
		registerValueSerializer(Float.class, new FloatSerializer());
		registerValueSerializer(Integer.class, new IntegerSerializer());
		registerValueSerializer(Boolean.class, new BooleanSerializer());
		
		//valueSerializer.collection
		registerValueSerializer(ArrayList.class, new ArrayListSerializer());
		registerValueSerializer(Vector.class, new VectorSerializer());
		registerValueSerializer(HashSet.class, new HashSetSerializer());
		
		//valueSerializer.map
		registerValueSerializer(Hashtable.class, new HashtableSerializer());
		registerValueSerializer(HashMap.class, new HashMapSerializer());
		
		//valueSerializer.typeIndependent
		registerEnumSerializer(new EnumSerializer());
		registerArraySerializer(new ArraySerializer());
		registerBeanSerializer(new BeanSerializer());
		registerListPatternSerializer(new ListPatternSerializer());
		registerNullSerializer(new NullSerializer());
		
		//Allow us to deserialize an ArrayList as a Vector
		registerDeserializingClass(ArrayList.class, Vector.class);
	}

	public static void registerValueSerializer (Class aClass, ValueSerializer anExternalSerializer) {
		classToValueSerializerMap.put(aClass, anExternalSerializer);
	}

	public static ValueSerializer getValueSerializer (Class aClass) {
		return classToValueSerializerMap.get(aClass);
	}
	
	public static DispatchingSerializer getDispatchingSerializer() {
		return SerializerRegistry.dispatchingSerializer;
	}
 	
	public static void registerDispatchingSerializer(DispatchingSerializer newVal) {
		SerializerRegistry.dispatchingSerializer = newVal; 
	}
	
	public static void registerEnumSerializer(EnumSerializer s) {
		SerializerRegistry.enumSerializer = s; 
	}
	
	public static void registerArraySerializer(ArraySerializer s) {
		SerializerRegistry.arraySerializer = s;
	}
	
	public static void registerBeanSerializer(BeanSerializer s) {
		SerializerRegistry.beanSerializer = s;
	}
	
	public static void registerListPatternSerializer(ListPatternSerializer s) {
		SerializerRegistry.listPatternSerializer = s;
	}
	
	public static void registerNullSerializer(NullSerializer s) {
		SerializerRegistry.nullSerializer = s; 
	}
	
	public static ValueSerializer getEnumSerializer() {
		return SerializerRegistry.enumSerializer; 
	}
	
	public static ValueSerializer getArraySerializer() {
		return SerializerRegistry.arraySerializer;
	}
	
	public static ValueSerializer getBeanSerializer() {
		return SerializerRegistry.beanSerializer;
	}
	
	public static ValueSerializer getListPatternSerializer() {
		return SerializerRegistry.listPatternSerializer;
	}
	
	public static ValueSerializer getNullSerializer() {
		return SerializerRegistry.nullSerializer; 
	}
	
	public static Set<Class> getRegisteredValueSerializerClasses() {
		return classToValueSerializerMap.keySet(); 
	}
	
	public static void registerDeserializingClass(Class instanceClass, Class deserializeClass) {
		classToAltDeserializerMap.put(instanceClass, deserializeClass);
	}
	
	public static Class getDeserializingClass(Class aClass) {
		return (classToAltDeserializerMap.get(aClass) != null) ?
				classToAltDeserializerMap.get(aClass) : 
					aClass; 
	}
	
}