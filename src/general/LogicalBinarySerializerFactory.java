package general;

import serialization.Serializer;
import serialization.SerializerFactory;
import util.annotations.Comp533Tags;
import util.annotations.Tags;

public class LogicalBinarySerializerFactory implements SerializerFactory {

	@Override
	public Serializer createSerializer() {
		return new LogicalBinarySerializer(); 
	}

}
