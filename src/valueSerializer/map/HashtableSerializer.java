package valueSerializer.map;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
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
public class HashtableSerializer implements ValueSerializer {

	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (anObject instanceof Hashtable) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			Hashtable map = (Hashtable) anObject; 
			Class bufferClass = anOutputBuffer.getClass(); 
			if (ByteBuffer.class.isAssignableFrom(bufferClass)) {
				ByteBuffer bBuff = (ByteBuffer) anOutputBuffer;
				bBuff.put(Hashtable.class.getName().getBytes());
				bBuff.putInt(map.size());
			} else if (bufferClass == AStringBuffer.class) {
				AStringBuffer sBuff = (AStringBuffer) anOutputBuffer;
				Object[] args = {Hashtable.class.getName() + map.size() + ValueSerializer.DELIMETER};
				sBuff.executeStringBufferMethod(SerializerRegistry.stringBufferAppend, args);
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to Hashtable value serializer");
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
			throw new NotSerializableException("Tried to serialize a non Hashtable type with the Hashtable value serializer");
		}
	}

	@Override
	public Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException {
		if (aClass == Hashtable.class) {
			ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, aClass);
			DispatchingSerializer ds = SerializerRegistry.getDispatchingSerializer();
			Hashtable map = new Hashtable();
			Integer numKeys = (Integer) SerializerRegistry.getValueSerializer(Integer.class)
					.objectFromBuffer(anInputBuffer, Integer.class, retrievedObjects);
			for (int i = 0; i < numKeys; i++) {
				Object k = ds.objectFromBuffer(anInputBuffer, retrievedObjects);
				Object v = ds.objectFromBuffer(anInputBuffer, retrievedObjects);
				map.put(k, v);
			}
			retrievedObjects.add(map);
			ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, map, retrievedObjects);
			return map;
		} else {
			throw new NotSerializableException("Tried to serialize a non Hashtable type with the Hashtable value serializer");
		}
	}
}
