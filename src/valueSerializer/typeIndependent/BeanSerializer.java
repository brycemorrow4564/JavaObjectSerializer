package valueSerializer.typeIndependent;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.NotSerializableException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import general.AStringBuffer;
import general.SerializerRegistry;
import util.annotations.Comp533Tags;
import util.annotations.Tags;
import util.misc.RemoteReflectionUtility;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationFinished;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationInitiated;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationFinished;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationInitiated;
import valueSerializer.DispatchingSerializer;
import valueSerializer.ValueSerializer;

@Tags({Comp533Tags.BEAN_SERIALIZER})
public class BeanSerializer implements ValueSerializer {

	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (anObject == null || anObject instanceof Serializable) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			Class bufferClass = anOutputBuffer.getClass(); 
			if (ByteBuffer.class.isAssignableFrom(bufferClass)) {
				//Binary encoding 
				ByteBuffer bBuff = (ByteBuffer) anOutputBuffer;
				bBuff.put(SerializerRegistry.TYPE_FREE_HEADER.getBytes());
			} else if (bufferClass == AStringBuffer.class) {
				//Textual encoding 
				AStringBuffer sBuff = (AStringBuffer) anOutputBuffer;
				Object[] args = {SerializerRegistry.TYPE_FREE_HEADER};
				sBuff.executeStringBufferMethod(SerializerRegistry.stringBufferAppend, args);
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to Bean value serializer");
			}
			PropertyDescriptor[] info = null; 
			try {
				info = Introspector.getBeanInfo(anObject.getClass()).getPropertyDescriptors();
				HashMap<String, Object> beanMap = new HashMap<>(); 
				for (PropertyDescriptor pd : info) {
					Method readMethod = pd.getReadMethod();
					Method writeMethod = pd.getWriteMethod();
					if (readMethod == null || writeMethod == null || RemoteReflectionUtility.isTransient(readMethod)) {
						continue; 
					} else {
						beanMap.put(pd.getName(), readMethod.invoke(anObject, null));
					}
				}
				DispatchingSerializer ds = SerializerRegistry.getDispatchingSerializer();
				ds.objectToBuffer(anOutputBuffer, beanMap.keySet().size(), visitedObjects);
				Iterator<String> iter = beanMap.keySet().iterator();
				while (iter.hasNext()) {
					String key = iter.next(); 
					ds.objectToBuffer(anOutputBuffer, key, visitedObjects);
					ds.objectToBuffer(anOutputBuffer, beanMap.get(key), visitedObjects);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			ExtensibleValueSerializationFinished.newCase(this, anObject, anOutputBuffer, visitedObjects);
		} else {
			throw new NotSerializableException("Tried to serialize a non Serializable type with the Bean value serializer");
		}
	}

	@Override
	public Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException {
		if (aClass == null || aClass instanceof Serializable) {
			ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, aClass);
			DispatchingSerializer ds = SerializerRegistry.getDispatchingSerializer();
			try {
				Object bean = aClass.newInstance();
				Integer beanSize = (Integer) ds.objectFromBuffer(anInputBuffer, retrievedObjects);
				HashMap<String, PropertyDescriptor> pMap = new HashMap<String, PropertyDescriptor>();
				for (PropertyDescriptor d : Introspector.getBeanInfo(aClass).getPropertyDescriptors()) { 
					pMap.put(d.getName(), d); 
				}
				for (int i = 0; i < beanSize; i++) {
					String pName = (String) ds.objectFromBuffer(anInputBuffer, retrievedObjects);
					Object pValue = ds.objectFromBuffer(anInputBuffer, retrievedObjects);
					PropertyDescriptor p = pMap.get(pName);
					if (p != null) { p.getWriteMethod().invoke(bean, pValue); }
				}
				retrievedObjects.add(bean);
				ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, bean, retrievedObjects);
				return aClass.cast(bean);	
			} catch (Exception e) {
				e.printStackTrace();
				return null; 
			}
		} else {
			throw new NotSerializableException("Tried to deserialize to non Serializable type in Bean value serializer");
		}
	}
	
}
