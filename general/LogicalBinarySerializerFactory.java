package general;

import serialization.Serializer;
import serialization.SerializerFactory;
import util.annotations.Comp533Tags;
import util.annotations.Tags;

@Tags({Comp533Tags.LOGICAL_BINARY_SERIALIZER_FACTORY})
public class LogicalBinarySerializerFactory implements SerializerFactory {

	@Override
	public Serializer createSerializer() {
		return new LogicalBinarySerializer(); 
	}

}
