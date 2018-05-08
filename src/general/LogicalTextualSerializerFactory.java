package general;

import serialization.Serializer;
import serialization.SerializerFactory;
import util.annotations.Comp533Tags;
import util.annotations.Tags;

@Tags({Comp533Tags.LOGICAL_TEXTUAL_SERIALIZER_FACTORY})
public class LogicalTextualSerializerFactory implements SerializerFactory {

	@Override
	public Serializer createSerializer() {
		return new LogicalTextualSerializer(); 
	}

}
