package valueSerializer;

import java.io.NotSerializableException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Set;

import general.AStringBuffer;
import general.SerializerRegistry;
import util.annotations.Tags;
import util.misc.RemoteReflectionUtility;
import util.annotations.Comp533Tags;

@Tags({Comp533Tags.DISPATCHING_SERIALIZER})
public class ADispatchingSerializer implements DispatchingSerializer {
	
	/* TODO: 
	 * Add STRING_SERIALIZER tag to string serializer class when dewan fixes issue 
	 * Add tags for logical binary/logical serializer factories. 
	 */

	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		//Handle null
		if (anObject == null) {
			SerializerRegistry.getNullSerializer().objectToBuffer(anOutputBuffer, anObject, visitedObjects);
			return; 
		}
		Class targetClass = anObject.getClass();
		//Check to see if we have already serialized object of this type 
		if (visitedObjects.contains(anObject)) {
			int offset = visitedObjects.indexOf(anObject);
			if (ByteBuffer.class.isAssignableFrom(anOutputBuffer.getClass())) {
				((ByteBuffer) anOutputBuffer).put(SerializerRegistry.REFERENCE_HEADER.getBytes());
				((ByteBuffer) anOutputBuffer).putInt(offset);
			} else if (anOutputBuffer.getClass() == AStringBuffer.class) {
				Object[] args = {SerializerRegistry.REFERENCE_HEADER + offset + ValueSerializer.DELIMETER};
				((AStringBuffer) anOutputBuffer).executeStringBufferMethod(SerializerRegistry.stringBufferAppend, args);
			} else {
				throw new NotSerializableException("Buffer of unsupported type provided to dispatching serializer");
			}
			return; 
		}
		//Check if we can serialize with a registered ValueSerializer 
		Iterator classIterator = SerializerRegistry.getRegisteredValueSerializerClasses().iterator();
		while (classIterator.hasNext()) {
			if (targetClass == (Class) classIterator.next()) {
				if (!isPrimitiveType(anObject)) { visitedObjects.add(anObject); }
				SerializerRegistry.getValueSerializer(targetClass).objectToBuffer(anOutputBuffer, anObject, visitedObjects);
				return; 
			}
		}
		//If we can't serialize with a registered ValueSerializer, this must be instance of Type Free object 
		if (targetClass.isArray()) {
			SerializerRegistry.getArraySerializer().objectToBuffer(anOutputBuffer, anObject, visitedObjects);
		} else if (targetClass.isEnum()) {
			SerializerRegistry.getEnumSerializer().objectToBuffer(anOutputBuffer, anObject, visitedObjects);
		} else if (RemoteReflectionUtility.isList(targetClass)) {
			SerializerRegistry.getListPatternSerializer().objectToBuffer(anOutputBuffer, anObject, visitedObjects);
		} else if (Serializable.class.isAssignableFrom(targetClass)) {
			SerializerRegistry.getBeanSerializer().objectToBuffer(anOutputBuffer, anObject, visitedObjects);
		} else {
			throw new NotSerializableException("Object passed to dispatching serializer did not have recognizable type");
		}
	}
	
	@Override
	public Object objectFromBuffer(Object anInputBuffer, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException {
		//Handle null
		if (bufferHeadMatchesString(anInputBuffer, SerializerRegistry.NULL_HEADER)) {
			removeHeader(anInputBuffer, SerializerRegistry.NULL_HEADER);
			return SerializerRegistry.getNullSerializer().objectFromBuffer(anInputBuffer, null, retrievedObjects);
		}
		//Check if we can deserialize with a registered ValueSerializer 
		Iterator classIterator = SerializerRegistry.getRegisteredValueSerializerClasses().iterator();
		while (classIterator.hasNext()) {
			Class aClass = (Class) classIterator.next(); 
			if (bufferHeadMatchesString(anInputBuffer, aClass.getName())) {
				removeHeader(anInputBuffer, aClass.getName());
				Object obj = SerializerRegistry.getValueSerializer(aClass).objectFromBuffer(anInputBuffer, aClass, retrievedObjects);
				if (!isPrimitiveType(aClass)) { retrievedObjects.add(obj); }
				return obj; 
			}
		}
		//Check if we can deserialize with type free serializer 
		if (bufferHeadMatchesString(anInputBuffer, SerializerRegistry.TYPE_FREE_HEADER)) {
			removeHeader(anInputBuffer, SerializerRegistry.TYPE_FREE_HEADER);
			String className = (String) objectFromBuffer(anInputBuffer, retrievedObjects);
			Class aClass = null; 
			try {
				aClass = Class.forName(className);
			} catch (ClassNotFoundException e) {
				throw new NotSerializableException("Nonexistent class type: " + aClass.getName());
			}
			if (aClass.isArray()) {
				return SerializerRegistry.getArraySerializer().objectFromBuffer(anInputBuffer, aClass, retrievedObjects);
			} else if (aClass.isEnum()) {
				return SerializerRegistry.getEnumSerializer().objectFromBuffer(anInputBuffer, aClass, retrievedObjects);
			} else if (RemoteReflectionUtility.isList(aClass) && Serializable.class.isAssignableFrom(aClass)) {
				return SerializerRegistry.getListPatternSerializer().objectFromBuffer(anInputBuffer, aClass, retrievedObjects);
			} else if (Serializable.class.isAssignableFrom(aClass)) {
				return SerializerRegistry.getBeanSerializer().objectFromBuffer(anInputBuffer, aClass, retrievedObjects);
			} else {
				throw new NotSerializableException("Unrecognized type free class: " + aClass.getName());
			}
		}
		//Check if this is a reference header for object of previously serialized type 
		if (bufferHeadMatchesString(anInputBuffer, SerializerRegistry.REFERENCE_HEADER)) {
			removeHeader(anInputBuffer, SerializerRegistry.REFERENCE_HEADER);
			int off = (Integer) objectFromBuffer(anInputBuffer, retrievedObjects);
			if (off >= 0) {
				return retrievedObjects.get(off);
			} else {
				throw new StreamCorruptedException("Offset for referenced object was incorrect");
			}
		}
		return null; 
	}
	
	public static void removeHeader(Object anInputBuffer, String header) throws NotSerializableException {
		if (anInputBuffer instanceof ByteBuffer) {
			ByteBuffer bBuff = (ByteBuffer) anInputBuffer; 
			int byteLen = header.getBytes().length;
			byte[] bytes = new byte[byteLen];
			bBuff.get(bytes);
		} else if (anInputBuffer instanceof AStringBuffer) {
			AStringBuffer sBuff = (AStringBuffer) anInputBuffer; 
			sBuff.readCharacters(header.length());
		} else {
			throw new NotSerializableException("Unsupported buffer type when parsing header in dispatching serializer");
		}
	}
	
	public static boolean bufferHeadMatchesString(Object anInputBuffer, String header) throws NotSerializableException {
		if (anInputBuffer instanceof ByteBuffer) {
			ByteBuffer bBuff = (ByteBuffer) anInputBuffer; 
			bBuff.mark(); 
			byte[] bytes = new byte[bBuff.remaining()];
			bBuff.get(bytes);
			bBuff.reset(); 
			return (new String(bytes)).startsWith(header); //does not advance internal pointer of AStringBuffer
		} else if (anInputBuffer instanceof AStringBuffer) {
			AStringBuffer sBuff = (AStringBuffer) anInputBuffer; 
			return sBuff.startsWith(header); //does not advance internal pointer of AStringBuffer 
		} else {
			throw new NotSerializableException("Unsupported buffer type when parsing header in dispatching serializer");
		}
	}
	
	public static boolean isPrimitiveType(Object anObject) {
		boolean retVal = true;  
		for (Class c : new ArrayList<Class>(Arrays.asList(Integer.class, Short.class, Long.class, Double.class, 
				Float.class, Boolean.class, String.class))) {
			retVal = retVal || anObject.getClass() == c; 
		}
		return retVal; 
	}

}
