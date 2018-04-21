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

@Tags({Comp533Tags.BOOLEAN_SERIALIZER})
public class BooleanSerializer implements ValueSerializer {

	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (anObject instanceof Boolean) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			Boolean value = (Boolean) anObject; 
			Class bufferClass = anOutputBuffer.getClass(); 
			if (ByteBuffer.class.isAssignableFrom(bufferClass)) {
				ByteBuffer bBuff = (ByteBuffer) anOutputBuffer;
				bBuff.put(Boolean.class.getName().getBytes());
				bBuff.putInt(value == true ? 1 : 0);
			} else if (bufferClass == AStringBuffer.class) {
				AStringBuffer sBuff = (AStringBuffer) anOutputBuffer;
				Object[] args = {Boolean.class.getName() + (value == true ? 1 : 0) + ValueSerializer.DELIMETER};
				sBuff.executeStringBufferMethod(SerializerRegistry.stringBufferAppend, args);
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to Boolean value serializer");
			}
			ExtensibleValueSerializationFinished.newCase(this, anObject, anOutputBuffer, visitedObjects);
		} else {
			throw new NotSerializableException("Tried to serialize a non Boolean type with the Boolean value serializer");
		}
	}

	@Override
	public Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException {
		Object retVal = null; 
		if (aClass == Boolean.class) {
			ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, aClass);
			if (ByteBuffer.class.isAssignableFrom(anInputBuffer.getClass())) {
				retVal = ((ByteBuffer) anInputBuffer).getInt() == 1 ? true : false;
			} else if (anInputBuffer instanceof AStringBuffer) {
				retVal = Integer.parseInt(((AStringBuffer) anInputBuffer).readStringBufferTillDelimiter()) == 1 ? true : false;
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to Boolean value serializer");
			}
			ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, retVal, retrievedObjects); 
		} else {
			throw new NotSerializableException("Tried to deserialize a non Boolean type with the Boolean value serialzer");
		}
		return aClass.cast(retVal); 
	}

}