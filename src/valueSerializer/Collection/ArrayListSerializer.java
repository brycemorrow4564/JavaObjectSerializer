package valueSerializer.Collection;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
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

@Tags({Comp533Tags.COLLECTION_SERIALIZER})
public class ArrayListSerializer implements ValueSerializer {
	
	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (anObject instanceof ArrayList) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			ArrayList coll = (ArrayList) anObject; 
			Class bufferClass = anOutputBuffer.getClass(); 
			if (ByteBuffer.class.isAssignableFrom(bufferClass)) {
				ByteBuffer bBuff = (ByteBuffer) anOutputBuffer;
				bBuff.put(ArrayList.class.getName().getBytes());
				bBuff.putInt(coll.size());
			} else if (bufferClass == AStringBuffer.class) {
				AStringBuffer sBuff = (AStringBuffer) anOutputBuffer;
				Object[] args = {ArrayList.class.getName() + coll.size() + ValueSerializer.DELIMETER};
				sBuff.executeStringBufferMethod(SerializerRegistry.stringBufferAppend, args);
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to ArrayList value serializer");
			}
			DispatchingSerializer ds = SerializerRegistry.getDispatchingSerializer();
			Iterator iter = coll.iterator();
			while (iter.hasNext()) {
				ds.objectToBuffer(anOutputBuffer, iter.next(), visitedObjects);
			}
			ExtensibleValueSerializationFinished.newCase(this, anObject, anOutputBuffer, visitedObjects);
		} else {
			throw new NotSerializableException("Tried to serialize a non ArrayList type with the ArrayList value serializer");
		}
	}

	@Override
	public Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException { 
		if (aClass == ArrayList.class) {
			ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, aClass);
			DispatchingSerializer ds = SerializerRegistry.getDispatchingSerializer();
			ArrayList<Object> coll = new ArrayList<>();
			Integer collLen = (Integer) SerializerRegistry.getValueSerializer(Integer.class)
					.objectFromBuffer(anInputBuffer, Integer.class, retrievedObjects);
			for (int i = 0; i < collLen; i++) {
				coll.add(ds.objectFromBuffer(anInputBuffer, retrievedObjects));
			}
			retrievedObjects.add(coll);
			ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, coll, retrievedObjects);
			return aClass.cast(coll); 
		} else {
			throw new NotSerializableException("Tried to deserialize a non ArrayList type with the ArrayList value serialzer");
		}
	}

}
