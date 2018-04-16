package valueSerializer.Collection;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;

import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationFinished;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationInitiated;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationFinished;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationInitiated;
import valueSerializer.ValueSerializer;

public class HashSetSerializer implements ValueSerializer {

	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (anObject instanceof HashSet) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			HashSet value = (HashSet) anObject; 
			Class bufferClass = anOutputBuffer.getClass(); 
			if (bufferClass == ByteBuffer.class) {
				ByteBuffer bBuff = (ByteBuffer) anOutputBuffer;
//				bBuff.put(HashSet.class.getName().getBytes());
//				bBuff.putHashSet(value);
			} else if (bufferClass == StringBuffer.class) {
				StringBuffer sBuff = (StringBuffer) anOutputBuffer; 
//				sBuff.append(HashSet.class.getName() + value + ValueSerializer.DELIMETER);
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to HashSet value serializer");
			}
			ExtensibleValueSerializationFinished.newCase(this, anObject, anOutputBuffer, visitedObjects);
		} else {
			throw new NotSerializableException("Tried to serialize a non String type with the HashSet value serializer");
		}
	}

	@Override
	public Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException {
		HashSet retVal = null; 
		if (aClass == HashSet.class) {
			ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, aClass);
			if (anInputBuffer instanceof ByteBuffer) {
				ByteBuffer bBuff = (ByteBuffer) anInputBuffer; 
//				retVal = bBuff.getHashSet();
			} else if (anInputBuffer instanceof StringBuffer) {
				StringBuffer sBuff = (StringBuffer) anInputBuffer;
				int delimeterIndex = sBuff.indexOf(ValueSerializer.DELIMETER);
//				retVal = HashSet.parseHashSet(sBuff.substring(0, delimeterIndex));
				sBuff.delete(0, sBuff.length()); //clear the buffer after we read out the value 
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to HashSet value serializer");
			}
			ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, retVal, retrievedObjects);
			return retVal;  
		} else {
			throw new NotSerializableException("Tried to deserialize a non HashSet type with the HashSet value serialzer");
		}
	}

}
