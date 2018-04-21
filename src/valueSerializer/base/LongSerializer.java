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

@Tags({Comp533Tags.LONG_SERIALIZER})
public class LongSerializer implements ValueSerializer {

	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (anObject instanceof Long) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			Long value = (Long) anObject; 
			Class bufferClass = anOutputBuffer.getClass(); 
			if (ByteBuffer.class.isAssignableFrom(bufferClass)) {
				ByteBuffer bBuff = (ByteBuffer) anOutputBuffer;
				bBuff.put(Long.class.getName().getBytes());
				bBuff.putLong(value);
			} else if (bufferClass == AStringBuffer.class) {
				AStringBuffer sBuff = (AStringBuffer) anOutputBuffer;
				Object[] args = {Long.class.getName() + value + ValueSerializer.DELIMETER};
				sBuff.executeStringBufferMethod(SerializerRegistry.stringBufferAppend, args);
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to Long value serializer");
			}
			ExtensibleValueSerializationFinished.newCase(this, anObject, anOutputBuffer, visitedObjects);
		} else {
			throw new NotSerializableException("Tried to serialize a non Long type with the Long value serializer");
		}
	}

	@Override
	public Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException {
		Object retVal = null; 
		if (aClass == Long.class) {
			ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, aClass);
			if (ByteBuffer.class.isAssignableFrom(anInputBuffer.getClass())) {
				retVal = ((ByteBuffer) anInputBuffer).getLong();
			} else if (anInputBuffer instanceof AStringBuffer) {
				retVal = Long.parseLong(((AStringBuffer) anInputBuffer).readStringBufferTillDelimiter());
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to Long value serializer");
			}
			ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, retVal, retrievedObjects); 
		} else {
			throw new NotSerializableException("Tried to deserialize a non Boolean type with the Long value serialzer");
		}
		return aClass.cast(retVal); 
	}

}