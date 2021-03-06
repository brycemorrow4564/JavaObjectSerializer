package valueSerializer.map;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import general.AStringBuffer;
import general.SerializerRegistry;
import util.annotations.Comp533Tags;
import util.annotations.Tags;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationFinished;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationInitiated;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationFinished;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationInitiated;
import valueSerializer.DispatchingSerializer;
import valueSerializer.ValueSerializer;

@Tags({Comp533Tags.MAP_SERIALIZER})
public class HashMapSerializer implements ValueSerializer {

	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (anObject instanceof HashMap) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			HashMap map = (HashMap) anObject; 
			Class bufferClass = anOutputBuffer.getClass(); 
			if (ByteBuffer.class.isAssignableFrom(bufferClass)) {
				ByteBuffer bBuff = (ByteBuffer) anOutputBuffer;
				bBuff.put(HashMap.class.getName().getBytes());
				bBuff.putInt(map.size());
			} else if (bufferClass == AStringBuffer.class) {
				AStringBuffer sBuff = (AStringBuffer) anOutputBuffer;
				String str = HashMap.class.getName() + map.size() + ValueSerializer.DELIMETER;
				sBuff.append(str);
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to HashMap value serializer");
			}
			DispatchingSerializer ds = SerializerRegistry.getDispatchingSerializer(); 
			Iterator iter = map.keySet().iterator();
			Object key = null; 
			while (iter.hasNext()) {
				key = iter.next(); 
				ds.objectToBuffer(anOutputBuffer, key, visitedObjects);
				ds.objectToBuffer(anOutputBuffer, map.get(key), visitedObjects);
			}
			ExtensibleValueSerializationFinished.newCase(this, anObject, anOutputBuffer, visitedObjects);
		} else {
			throw new NotSerializableException("Tried to serialize a non HashMap type with the HashMap value serializer");
		}
	}

	@Override
	public Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException {
		if (aClass == HashMap.class) {
			ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, aClass);
			DispatchingSerializer ds = SerializerRegistry.getDispatchingSerializer();
			HashMap map = new HashMap();
			retrievedObjects.add(map);
			Integer numKeys = (Integer) SerializerRegistry.getValueSerializer(Integer.class)
					.objectFromBuffer(anInputBuffer, Integer.class, retrievedObjects);
			for (int i = 0; i < numKeys; i++) {
				Object k = ds.objectFromBuffer(anInputBuffer, retrievedObjects);
				Object v = ds.objectFromBuffer(anInputBuffer, retrievedObjects);
				map.put(k, v);
			}
			ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, map, retrievedObjects);
			return map;
		} else {
			throw new NotSerializableException("Tried to serialize a non HashMap type with the HashMap value serializer");
		}
	}

}
