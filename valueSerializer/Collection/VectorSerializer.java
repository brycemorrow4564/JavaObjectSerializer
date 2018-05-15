package valueSerializer.Collection;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Vector;
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
public class VectorSerializer implements ValueSerializer {

	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (anObject instanceof Vector) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			Vector coll = (Vector) anObject; 
			Class bufferClass = anOutputBuffer.getClass(); 
			if (ByteBuffer.class.isAssignableFrom(bufferClass)) {
				ByteBuffer bBuff = (ByteBuffer) anOutputBuffer;
				bBuff.put(Vector.class.getName().getBytes());
				bBuff.putInt(coll.size());
			} else if (bufferClass == AStringBuffer.class) {
				AStringBuffer sBuff = (AStringBuffer) anOutputBuffer;
				String str = Vector.class.getName() + coll.size() + ValueSerializer.DELIMETER;
				sBuff.append(str);
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to Vector value serializer");
			}
			DispatchingSerializer ds = SerializerRegistry.getDispatchingSerializer(); 
			Iterator iter = coll.iterator(); 
			while (iter.hasNext()) {
				ds.objectToBuffer(anOutputBuffer, iter.next(), visitedObjects);
			}
			ExtensibleValueSerializationFinished.newCase(this, anObject, anOutputBuffer, visitedObjects);
		} else {
			throw new NotSerializableException("Tried to serialize a non Vector type with the Vector value serializer");
		}
	}

	@Override
	public Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException { 
		if (aClass == Vector.class) {
			ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, aClass);
			DispatchingSerializer ds = SerializerRegistry.getDispatchingSerializer();
			ValueSerializer intSerializer = SerializerRegistry.getValueSerializer(Integer.class);
			Integer collLen = (Integer) intSerializer.objectFromBuffer(anInputBuffer, Integer.class, retrievedObjects);
			Vector coll = new Vector<>();
			retrievedObjects.add(coll);
			for (int i = 0; i < collLen; i++) {
				Object o = ds.objectFromBuffer(anInputBuffer, retrievedObjects);
				coll.add(o);
			}
			ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, coll, retrievedObjects);
			return aClass.cast(coll); 
		} else {
			throw new NotSerializableException("Tried to deserialize a non Vector type with the Vector value serialzer");
		}
	}

}

