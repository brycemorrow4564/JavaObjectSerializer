package general;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import serialization.Serializer;
import util.annotations.Comp533Tags;
import util.annotations.Tags;

@Tags({Comp533Tags.LOGICAL_TEXTUAL_SERIALIZER})
public class LogicalTextualSerializer implements Serializer {

	@Override
	public Object objectFromInputBuffer(ByteBuffer inputBuffer) throws StreamCorruptedException {
		byte[] bytes = new byte[inputBuffer.remaining()]; 
		inputBuffer.get(bytes);
		inputBuffer.mark();
		AStringBuffer asb = new AStringBuffer(new StringBuffer(new String(bytes)));
		Object deserializedObj = null; 
		try {
			deserializedObj = SerializerRegistry.getDispatchingSerializer().objectFromBuffer(asb, new ArrayList<Object>());
			int numCharsRead = bytes.length - asb.length(); //number of chars read from string buffer 
			inputBuffer.reset(); 
			char c; 
			for (int i = 0; i < numCharsRead; i++) { 
				c = inputBuffer.getChar(); 
			}
			inputBuffer.flip(); 
			return deserializedObj; 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ByteBuffer outputBufferFromObject(Object object) throws NotSerializableException {
		AStringBuffer sBuff = new AStringBuffer(new StringBuffer());
		SerializerRegistry.getDispatchingSerializer().objectToBuffer(sBuff, object, new ArrayList());
		return ByteBuffer.wrap(sBuff.toString().getBytes());
	}

}
