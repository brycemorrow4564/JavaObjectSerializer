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

@Tags({Comp533Tags.STRING_SERIALIZER})
public class StringSerializer implements ValueSerializer {

	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (anObject instanceof String) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			String str = (String) anObject; 
			Class bufferClass = anOutputBuffer.getClass(); 
			if (ByteBuffer.class.isAssignableFrom(bufferClass)) {
				ByteBuffer bBuff = (ByteBuffer) anOutputBuffer;
				bBuff.put(String.class.getName().getBytes());
				bBuff.putInt(str.length());
				bBuff.put(str.getBytes());
			} else if (bufferClass == AStringBuffer.class) {
				AStringBuffer sBuff = (AStringBuffer) anOutputBuffer;
				String s = String.class.getName() + str.length() + ValueSerializer.DELIMETER + str;
				sBuff.append(s);
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to String value serializer");
			}
			ExtensibleValueSerializationFinished.newCase(this, anObject, anOutputBuffer, visitedObjects);
		} else {
			throw new NotSerializableException("Tried to serialize a non String type with the String value serializer");
		}
	}
	
	@Override
	public Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException {
		Object retVal = null; 
		if (aClass == String.class) {
			ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, aClass);
			if (ByteBuffer.class.isAssignableFrom(anInputBuffer.getClass())) {
				ByteBuffer bBuff = (ByteBuffer) anInputBuffer; 
				int strLen = bBuff.getInt();
				byte[] strBytes = new byte[strLen]; 
				bBuff.get(strBytes);
				retVal = new String(strBytes);
			} else if (anInputBuffer instanceof AStringBuffer) {
				AStringBuffer sBuff = (AStringBuffer) anInputBuffer;
				int strLen = Integer.parseInt(sBuff.readStringBufferTillDelimiter());
				retVal = sBuff.readCharacters(strLen);
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to String value serializer");
			}
			ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, retVal, retrievedObjects);
			return aClass.cast(retVal);   
		} else {
			throw new NotSerializableException("Tried to deserialize a non String type with the String value serialzer");
		}
	}

}
