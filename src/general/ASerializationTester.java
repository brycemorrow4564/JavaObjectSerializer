package general;

import examples.serialization.SerializationTester;
import serialization.SerializerSelector;
import util.trace.port.serialization.extensible.ExtensibleSerializationTraceUtility;

public class ASerializationTester extends SerializationTester {
	
	public static void main(String[] args) {
		ExtensibleSerializationTraceUtility.setTracing();
		//SerializerSelector.setSerializerFactory(new LogicalTextualSerializerFactory());
		SerializerSelector.setSerializerFactory(new LogicalBinarySerializerFactory());
		SerializationTester.main(args);
	}

}
