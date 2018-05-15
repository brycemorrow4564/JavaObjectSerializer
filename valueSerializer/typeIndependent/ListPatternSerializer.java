package valueSerializer.typeIndependent;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import general.AStringBuffer;
import general.SerializerRegistry;
import general.TypeIndependentSerializer;
import util.annotations.Comp533Tags;
import util.annotations.Tags;
import util.misc.RemoteReflectionUtility;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationFinished;
import util.trace.port.serialization.extensible.ExtensibleBufferDeserializationInitiated;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationFinished;
import util.trace.port.serialization.extensible.ExtensibleValueSerializationInitiated;
import valueSerializer.DispatchingSerializer;
import valueSerializer.ValueSerializer;

@Tags({Comp533Tags.LIST_PATTERN_SERIALIZER})
public class ListPatternSerializer implements ValueSerializer, TypeIndependentSerializer {
	
	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		if (RemoteReflectionUtility.isList(anObject.getClass())) {
			ExtensibleValueSerializationInitiated.newCase(this, anObject, anOutputBuffer);
			Class bufferClass = anOutputBuffer.getClass(); 
			if (ByteBuffer.class.isAssignableFrom(bufferClass)) {
				//Binary encoding 
				ByteBuffer bBuff = (ByteBuffer) anOutputBuffer;
				bBuff.put(SerializerRegistry.TYPE_FREE_HEADER.getBytes());
			} else if (bufferClass == AStringBuffer.class) {
				//Textual encoding 
				AStringBuffer sBuff = (AStringBuffer) anOutputBuffer;
				String str = SerializerRegistry.TYPE_FREE_HEADER;
				sBuff.append(str);
			} else {
				throw new NotSerializableException("Buffer of unsupported type passed to List Pattern value serializer");
			}
			DispatchingSerializer ds = SerializerRegistry.getDispatchingSerializer(); 
			String className = anObject.getClass().getName(); 
			Integer arrLen = RemoteReflectionUtility.listSize(anObject);
			ds.objectToBuffer(anOutputBuffer, className, visitedObjects);
			ds.objectToBuffer(anOutputBuffer, arrLen, visitedObjects);
			for (int i = 0; i < arrLen; i++) {
				ds.objectToBuffer(anOutputBuffer, RemoteReflectionUtility.listGet(anObject, i), visitedObjects);
			}
			ExtensibleValueSerializationFinished.newCase(this, anObject, anOutputBuffer, visitedObjects);
		} else {
			throw new NotSerializableException("Tried to serialize a non List type with the ListPattern value serializer");
		}
	}

	@Override
	public Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException {
		if (RemoteReflectionUtility.isList(aClass)) {
			ExtensibleBufferDeserializationInitiated.newCase(this, null, anInputBuffer, aClass);
			DispatchingSerializer ds = SerializerRegistry.getDispatchingSerializer();
			Object list = null; 
			try { list = aClass.newInstance(); } 
			catch (Exception e) { e.printStackTrace(); }
			retrievedObjects.add(list);
			Integer len = (Integer) ds.objectFromBuffer(anInputBuffer, retrievedObjects);
			for (int i = 0; i < len; i++) {
				RemoteReflectionUtility.listAdd(list, ds.objectFromBuffer(anInputBuffer, retrievedObjects));
			}
			ExtensibleBufferDeserializationFinished.newCase(this, null, anInputBuffer, list, retrievedObjects);
			return list; 
		} else {
			throw new NotSerializableException("Tried to deserialize to non List type in List Pattern value serializer");
		}
	}


}
