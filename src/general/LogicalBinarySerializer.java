package general;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import serialization.Serializer;
import util.annotations.Comp533Tags;
import util.annotations.Tags;
import valueSerializer.DispatchingSerializer;

@Tags({Comp533Tags.LOGICAL_BINARY_SERIALIZER})
public class LogicalBinarySerializer implements Serializer {

	@Override
	public Object objectFromInputBuffer(ByteBuffer inputBuffer) throws StreamCorruptedException {
		Object deserializedObj = null; 
		try {
			deserializedObj = SerializerRegistry.getDispatchingSerializer().objectFromBuffer(inputBuffer, new ArrayList<Object>());
			inputBuffer.flip();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deserializedObj;
	}

	@Override
	public ByteBuffer outputBufferFromObject(Object object) throws NotSerializableException {
		ByteBuffer anOutputBuffer = ByteBuffer.allocate(8 * 1024);
		SerializerRegistry.getDispatchingSerializer().objectToBuffer(anOutputBuffer, object, new ArrayList<Object>());
		anOutputBuffer.flip(); 
		return anOutputBuffer;
	}

}
