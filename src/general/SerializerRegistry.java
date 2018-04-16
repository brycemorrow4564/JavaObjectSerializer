package general;

import util.annotations.Tags;
import valueSerializer.DispatchingSerializer;
import valueSerializer.ValueSerializer;
import valueSerializer.base.DoubleSerializer;
import valueSerializer.base.FloatSerializer;
import valueSerializer.base.IntegerSerializer;
import valueSerializer.base.LongSerializer;
import valueSerializer.base.ShortSerializer;
import valueSerializer.base.StringSerializer;
import valueSerializer.typeIndependent.ArraySerializer;
import valueSerializer.typeIndependent.BeanSerializer;
import valueSerializer.typeIndependent.EnumSerializer;
import valueSerializer.typeIndependent.ListPatternSerializer;
import valueSerializer.typeIndependent.NullSerializer;

import java.lang.reflect.Method;
import java.util.HashMap;

import util.annotations.Comp533Tags;

@Tags({Comp533Tags.SERIALIZER_REGISTRY})
public class SerializerRegistry {
	
	protected static HashMap<Class, ValueSerializer> 	classToValueSerializerMap; 
	
	protected static DispatchingSerializer 	dispatchingSerializer; 
	protected static ArraySerializer 		arraySerializer; 
	protected static BeanSerializer 			beanSerializer; 
	protected static ListPatternSerializer 	listPatterSerializer; 
	protected static EnumSerializer 			enumSerializer; 
	protected static NullSerializer 			nullSerializer; 
	
	public static Method sbAppend; 
	
	static {
		try { 
			Class<?>[] paramTypes = {String.class};
			SerializerRegistry.sbAppend = StringBuffer.class.getMethod("appends", paramTypes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		classToValueSerializerMap = new HashMap<Class, ValueSerializer>();
		//Atomic value serializer registration 
		registerValueSerializer(String.class, new StringSerializer());
		registerValueSerializer(Short.class, new ShortSerializer());
		registerValueSerializer(Long.class, new LongSerializer());
		registerValueSerializer(Double.class, new DoubleSerializer());
		registerValueSerializer(Float.class, new FloatSerializer());
		registerValueSerializer(Integer.class, new IntegerSerializer());
	}

	public static void registerValueSerializer (Class aClass, ValueSerializer anExternalSerializer) {
		if (classToValueSerializerMap.containsKey(aClass)) {
			classToValueSerializerMap.remove(aClass);
		}
		classToValueSerializerMap.put(aClass, anExternalSerializer);
	}

	public static ValueSerializer getValueSerializer (Class aClass) {
		return classToValueSerializerMap.get(aClass); //can possibly return null if no serializer registered for class 
	}
	
	public static DispatchingSerializer getDispatchingSerializer() {
		return SerializerRegistry.dispatchingSerializer;
	}
	
	public static void registerDispatchingSerializer(DispatchingSerializer newVal) {
		SerializerRegistry.dispatchingSerializer = newVal; 
	}
	
}