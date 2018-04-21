package valueSerializer;

import util.annotations.Tags;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import util.annotations.Comp533Tags;

@Tags({Comp533Tags.VALUE_SERIALIZER, Comp533Tags.LOGICAL_BINARY_SERIALIZER, Comp533Tags.LOGICAL_TEXTUAL_SERIALIZER})
public interface ValueSerializer {
	
	public static final Character DELIMETER = ':';
	
  void objectToBuffer (Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects) 
		  throws NotSerializableException;
  
  Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects) 
		  throws StreamCorruptedException, NotSerializableException;
  
}
