package valueSerializer.base;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import general.AStringBuffer;
import general.SerializerRegistry;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationFinished;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationInitiated;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationFinished;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationInitiated;
import valueSerializer.ValueSerializer;

public class IntegerSerializer implements ValueSerializer {

	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (anObject instanceof Integer) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			Integer value = (Integer) anObject; 
			Class bufferClass = anOutputBuffer.getClass(); 
			if (bufferClass == ByteBuffer.class) {
				//Binary encoding 
				ByteBuffer bBuff = (ByteBuffer) anOutputBuffer;
				bBuff.put(Integer.class.getName().getBytes());
				bBuff.putInt(value);
			} else if (bufferClass == AStringBuffer.class) {
				//Textual encoding 
				AStringBuffer sBuff = (AStringBuffer) anOutputBuffer;
				Object[] args = {Integer.class.getName() + value + ValueSerializer.DELIMETER};
				sBuff.executeStringBufferMethod(SerializerRegistry.sbAppend, args);
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
		Integer retVal = null; 
		if (aClass == Integer.class) {
			ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, aClass);
			if (anInputBuffer instanceof ByteBuffer) {
				//Decode from binary 
				ByteBuffer bBuff = (ByteBuffer) anInputBuffer; 
				int strLen = bBuff.getInt();
				byte[] strBytes = new byte[strLen]; 
				bBuff.get(strBytes);
				retVal = Integer.parseInt(new String(strBytes));
			} else if (anInputBuffer instanceof AStringBuffer) {
				//Decode from textual representation 
				AStringBuffer sBuff = (AStringBuffer) anInputBuffer;
				String c = null;
				StringBuilder sb = new StringBuilder(); 
				while (true) {
					c = sBuff.readCharacter(); 
					if (c == null) {
						throw new NotSerializableException("Unexpectedly reached end of StringBuffer");
					} else if (c.equals(ValueSerializer.DELIMETER)) {
						break; 
					} else {
						sb.append(c);
					}
				}
				retVal = Integer.parseInt(sb.toString());
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to Integer value serializer");
			}
			ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, retVal, retrievedObjects);
			return retVal;  
		} else {
			throw new NotSerializableException("Tried to deserialize a non Integer type with the Integer value serialzer");
		}
	}

}