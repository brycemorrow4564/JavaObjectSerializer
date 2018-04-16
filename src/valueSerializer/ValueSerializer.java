package valueSerializer;

import util.annotations.Tags;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import util.annotations.Comp533Tags;

@Tags({Comp533Tags.VALUE_SERIALIZER})
public interface ValueSerializer {
	
	public static final String DELIMETER = ":";

	/* Note that the type of the buffers depends on whether or not we do BINARY or TEXTUAL serialization
	 * Binary: ByteBuffer
	 * Textual: <MutableString> (i.e. StringBuffer or StringReader)
	 * 
	 *  To determine which serialization scheme should be used, a value serializer 
	 *  should use the instance of operation on the passed input or output buffer. */
	
  void objectToBuffer (Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects) 
		  throws NotSerializableException;
  
  Object objectFromBuffer(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects) 
		  throws StreamCorruptedException, NotSerializableException;
  
}
