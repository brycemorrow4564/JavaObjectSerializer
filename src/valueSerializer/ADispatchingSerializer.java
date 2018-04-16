package valueSerializer;

import java.io.NotSerializableException;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import util.annotations.Tags;
import util.annotations.Comp533Tags;

@Tags({Comp533Tags.DISPATCHING_SERIALIZER })
public class ADispatchingSerializer implements DispatchingSerializer {

	@Override
	public void objectToBuffer(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects)
			throws NotSerializableException {
		
	}

	@Override
	public Object objectFromBuffer(Object anInputBuffer, ArrayList<Object> retrievedObjects)
			throws StreamCorruptedException, NotSerializableException {
		
		return null;
	}

}
