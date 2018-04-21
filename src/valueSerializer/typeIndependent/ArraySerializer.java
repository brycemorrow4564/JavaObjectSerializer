package valueSerializer.typeIndependent;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
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
import valueSerializer.DispatchingSerializer;
import valueSerializer.ValueSerializer;

@Tags({Comp533Tags.ARRAY_SERIALIZER})
public class ArraySerializer implements ValueSerializer {
	
	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (anObject.getClass().isArray()) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			Class bufferClass = anOutputBuffer.getClass(); 
			if (ByteBuffer.class.isAssignableFrom(bufferClass)) {
				ByteBuffer bBuff = (ByteBuffer) anOutputBuffer;
				bBuff.put(SerializerRegistry.TYPE_FREE_HEADER.getBytes());
			} else if (bufferClass == AStringBuffer.class) {
				AStringBuffer sBuff = (AStringBuffer) anOutputBuffer;
				Object[] args = {SerializerRegistry.TYPE_FREE_HEADER};
				sBuff.executeStringBufferMethod(SerializerRegistry.stringBufferAppend, args);
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to Array value serializer");
			}
			DispatchingSerializer ds = SerializerRegistry.getDispatchingSerializer(); 
			ds.objectToBuffer(anOutputBuffer, anObject.getClass().getComponentType().getName(), visitedObjects);
			ds.objectToBuffer(anOutputBuffer, Array.getLength(anObject), visitedObjects);
			for (int i = 0; i < Array.getLength(anObject); i++) {
				ds.objectToBuffer(anOutputBuffer, Array.get(anObject, i), visitedObjects);
			}
			ExtensibleValueSerializationFinished.newCase(this, anObject, anOutputBuffer, visitedObjects);
		} else {
			throw new NotSerializableException("Tried to serialize a non Array type with the Array value serializer");
		}
	}

	@Override
	public Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException {
		if (aClass.isArray()) {
			ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, aClass);
			DispatchingSerializer ds = SerializerRegistry.getDispatchingSerializer();
			String componentClassName = (String) ds.objectFromBuffer(anInputBuffer, retrievedObjects);
			Integer arrLen = (Integer) ds.objectFromBuffer(anInputBuffer, retrievedObjects);
			Class componentClass = null;
			try {
				componentClass = Class.forName(componentClassName);
			} catch (ClassNotFoundException e) { e.printStackTrace(); }
			Object arr = Array.newInstance(componentClass, arrLen);
			for (int i = 0; i < arrLen; i++) {
				Array.set(arr, i, ds.objectFromBuffer(anInputBuffer, retrievedObjects));
			}
			retrievedObjects.add(arr);
			ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, arr, retrievedObjects);
			return aClass.cast(arr);	
		} else {
			throw new NotSerializableException("Tried to deserialize to non Array type in Array value serializer");
		}
	}

}
