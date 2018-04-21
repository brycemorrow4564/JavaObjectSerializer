package valueSerializer;

import java.io.NotSerializableException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import general.AStringBuffer;

public interface ModularSerializer {
	
	//Methods for serialization
	public void writeBinaryHeader(ByteBuffer anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects) 
			throws NotSerializableException;
	public void writeTextualHeader(AStringBuffer anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects) 
			throws NotSerializableException;
	public void writeBinaryValue(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects) 
			throws NotSerializableException;
	public void writeTextualValue(Object anOutputBuffer, Object anObject, ArrayList<Object> visitedObjects) 
			throws NotSerializableException;
	
	//Methods for deserialization 
	public void parseBinaryHeader(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws NotSerializableException;
	public void parseTextualHeader(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws NotSerializableException;
	public void parseBinaryValue(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws NotSerializableException;
	public void parseTextualValue(Object anInputBuffer, Class aClass, ArrayList<Object> retrievedObjects)
			throws NotSerializableException;

}
