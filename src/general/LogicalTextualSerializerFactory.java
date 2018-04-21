package general;

import serialization.Serializer;
import serialization.SerializerFactory;

public class LogicalTextualSerializerFactory implements SerializerFactory {

	@Override
	public Serializer createSerializer() {
		return new LogicalTextualSerializer(); 
	}

}
