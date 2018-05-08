package valueSerializer.base;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import general.AStringBuffer;
import general.SerializerRegistry;
import util.annotations.Comp533Tags;
import util.annotations.Tags;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationFinished;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationInitiated;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationFinished;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationInitiated;
import valueSerializer.ValueSerializer;

@Tags({Comp533Tags.INTEGER_SERIALIZER})
public class IntegerSerializer implements ValueSerializer {

	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (anObject instanceof Integer) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			Integer value = (Integer) anObject; 
			Class bufferClass = anOutputBuffer.getClass(); 
			if (ByteBuffer.class.isAssignableFrom(bufferClass)) {
				ByteBuffer bBuff = (ByteBuffer) anOutputBuffer;
				bBuff.put(Integer.class.getName().getBytes());
				bBuff.putInt(value);
			} else if (bufferClass == AStringBuffer.class) {
				AStringBuffer sBuff = (AStringBuffer) anOutputBuffer;
				String str = Integer.class.getName() + value + ValueSerializer.DELIMETER;
				sBuff.append(str);
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to Integer value serializer");
			}
			ExtensibleValueSerializationFinished.newCase(this, anObject, anOutputBuffer, visitedObjects);
		} else {
			throw new NotSerializableException("Tried to serialize a non Integer type with the Integer value serializer");
		}
	}

	@Override
	public Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException {
		Object retVal = null; 
		if (aClass == Integer.class) {
			ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, aClass);
			if (ByteBuffer.class.isAssignableFrom(anInputBuffer.getClass())) {
				retVal = ((ByteBuffer) anInputBuffer).getInt();
			} else if (anInputBuffer instanceof AStringBuffer) {
				retVal = Integer.parseInt(((AStringBuffer) anInputBuffer).readStringBufferTillDelimiter());
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to Integer value serializer");
			}
			ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, retVal, retrievedObjects); 
		} else {
			throw new NotSerializableException("Tried to deserialize a non Integer type with the Integer value serialzer");
		}
		return aClass.cast(retVal);  
	}

}