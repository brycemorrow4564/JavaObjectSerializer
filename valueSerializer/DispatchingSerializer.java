package valueSerializer;

import util.annotations.Tags;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import util.annotations.Comp533Tags;

@Tags({Comp533Tags.DISPATCHING_SERIALIZER})
public interface DispatchingSerializer {
	
	void objectToBuffer (Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects) 
			throws NotSerializableException;

	Object objectFromBuffer(Object anInputBuffer, ArrayList<Object> retrievedObjects) 
			throws StreamCorruptedException, NotSerializableException;

}
