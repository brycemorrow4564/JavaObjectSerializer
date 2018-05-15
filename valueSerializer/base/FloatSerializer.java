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

@Tags({Comp533Tags.FLOAT_SERIALIZER})
public class FloatSerializer implements ValueSerializer {

	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (anObject instanceof Float) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			Float value = (Float) anObject; 
			Class bufferClass = anOutputBuffer.getClass(); 
			if (ByteBuffer.class.isAssignableFrom(bufferClass)) {
				ByteBuffer bBuff = (ByteBuffer) anOutputBuffer;
				bBuff.put(Float.class.getName().getBytes());
				bBuff.putFloat(value);
			} else if (bufferClass == AStringBuffer.class) {
				AStringBuffer sBuff = (AStringBuffer) anOutputBuffer;
				String str = Float.class.getName() + value + ValueSerializer.DELIMETER;
				sBuff.append(str);
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to Float value serializer");
			}
			ExtensibleValueSerializationFinished.newCase(this, anObject, anOutputBuffer, visitedObjects);
		} else {
			throw new NotSerializableException("Tried to serialize a non Float type with the Float value serializer");
		}
	}

	@Override
	public Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException {
		Object retVal = null; 
		if (aClass == Float.class) {
			ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, aClass);
			if (ByteBuffer.class.isAssignableFrom(anInputBuffer.getClass())) {
				retVal = ((ByteBuffer) anInputBuffer).getFloat();
			} else if (anInputBuffer instanceof AStringBuffer) {
				retVal = Float.parseFloat(((AStringBuffer) anInputBuffer).readStringBufferTillDelimiter());
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to Float value serializer");
			}
			ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, retVal, retrievedObjects); 
		} else {
			throw new NotSerializableException("Tried to deserialize a non Float type with the Float value serialzer");
		}
		return aClass.cast(retVal); 
	}

}