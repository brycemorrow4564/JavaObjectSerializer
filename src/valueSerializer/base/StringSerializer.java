package valueSerializer.base;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import general.AStringBuffer;
import general.SerializerRegistry;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationFinished;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationInitiated;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationFinished;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationInitiated;
import valueSerializer.ValueSerializer;

public class StringSerializer implements ValueSerializer {

	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (anObject instanceof String) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			String str = (String) anObject; 
			Class bufferClass = anOutputBuffer.getClass(); 
			if (bufferClass == ByteBuffer.class) {
				//Binary encoding 
				ByteBuffer bBuff = (ByteBuffer) anOutputBuffer;
				bBuff.put(String.class.getName().getBytes());
				bBuff.putInt(str.length());
				bBuff.put(str.getBytes());
			} else if (bufferClass == AStringBuffer.class) {
				//Textual encoding 
				AStringBuffer sBuff = (AStringBuffer) anOutputBuffer;
				Object[] args = {String.class.getName() + str.length() + ValueSerializer.DELIMETER + str};
				sBuff.executeStringBufferMethod(SerializerRegistry.sbAppend, args);
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
		String retVal = null; 
		if (aClass == String.class) {
			ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, aClass);
			if (anInputBuffer instanceof ByteBuffer) {
				//Decode from binary 
				ByteBuffer bBuff = (ByteBuffer) anInputBuffer; 
				int strLen = bBuff.getInt();
				byte[] strBytes = new byte[strLen]; 
				bBuff.get(strBytes);
				retVal = new String(strBytes);
			} else if (anInputBuffer instanceof AStringBuffer) {
				//Decode from textual representation 
				AStringBuffer sBuff = (AStringBuffer) anInputBuffer;
				String c = null;
				StringBuilder sb1 = new StringBuilder(); 
				//Locate delimiter and determine length of encoded string 
				while (true) {
					c = sBuff.readCharacter(); 
					if (c == null) {
						throw new NotSerializableException("Unexpectedly reached end of StringBuffer");
					} else if (c.equals(ValueSerializer.DELIMETER)) {
						break; 
					} else {
						sb1.append(c);
					}
				}
				int strLen = Integer.parseInt(sb1.toString());
				StringBuilder sb2 = new StringBuilder(); 
				for (int i = 0; i < strLen; i++) {
					sb2.append(sBuff.readCharacter());
				}
				retVal = sb2.toString();
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to String value serializer");
			}
			ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, retVal, retrievedObjects);
			return retVal;  
		} else {
			throw new NotSerializableException("Tried to deserialize a non String type with the String value serialzer");
		}
	}

}
