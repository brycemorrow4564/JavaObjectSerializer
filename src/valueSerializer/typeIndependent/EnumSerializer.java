package valueSerializer.typeIndependent;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

import general.AStringBuffer;
import general.SerializerRegistry;
import general.TypeIndependentSerializer;
import util.annotations.Comp533Tags;
import util.annotations.Tags;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationFinished;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationInitiated;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationFinished;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationInitiated;
import valueSerializer.DispatchingSerializer;
import valueSerializer.ValueSerializer;

@Tags({Comp533Tags.ENUM_SERIALIZER})
public class EnumSerializer implements ValueSerializer, TypeIndependentSerializer {

	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (anObject.getClass().isEnum()) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			Class bufferClass = anOutputBuffer.getClass(); 
			if (ByteBuffer.class.isAssignableFrom(bufferClass)) {
				((ByteBuffer) anOutputBuffer).put(SerializerRegistry.TYPE_FREE_HEADER.getBytes());
			} else if (bufferClass == AStringBuffer.class) {
				String str = SerializerRegistry.TYPE_FREE_HEADER;
				((AStringBuffer) anOutputBuffer).append(str);
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to Enum value serializer");
			}
			Enum en = (Enum) anObject; 
			ValueSerializer ss = SerializerRegistry.getValueSerializer(String.class);
			ss.objectToBuffer(anOutputBuffer, en.getClass().getName(), visitedObjects);
			ss.objectToBuffer(anOutputBuffer, en.toString(), visitedObjects);
			ExtensibleValueSerializationFinished.newCase(this, anObject, anOutputBuffer, visitedObjects);
		} else {
			throw new NotSerializableException("Tried to serialize a non Array type with the Enum value serializer");
		}
	}

	@Override
	public Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException {
		if (aClass.isEnum()) {
			ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, aClass);
			String enumStr = (String) SerializerRegistry.getDispatchingSerializer()
					.objectFromBuffer(anInputBuffer, retrievedObjects);
			Enum en = Enum.valueOf(aClass, enumStr);
			ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, en, retrievedObjects);
			return en; 
		} else {
			throw new NotSerializableException("Tried to deserialize to non Enum type in Enum value serializer");
		}
	}

}
