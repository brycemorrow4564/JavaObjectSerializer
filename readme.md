# Testing 
You need to to have JUnit5 added to your project build path in order to run the tests for the serializer. 

# Top Down Recursive Descent Logical Serialization Grammar 

```
Some notes on Syntax: 
	* The character '|' represents the logical OR 
	* The character '*' represents an n-times repetition of some grammar component where n is the integer value of the previous grammar component. 
		this is frequently used when we have a collection of items (list, string, etc.) and we want to know how many elements to parse/encode. 
	* There is a difference between <Type Serialization> and <Type Serialization as bytes or string>. The former represents the pattern as defined 
		by the grammar and the latter represents the value placed in the buffer (ByteBuffer or StringBuffer) during encoding/decoding.
		<Type Serialization as bytes or string> will contain only the atomic value while <Type Serialization> will contain the class name encoded
		as a string followed by the atomic value. The difference is subtle but essential. Compare the implementations of base value serializers with 
		type free serializers to see the differences between the two cases more clearly. 
	
	
----- OBJECT/VALUE SERIALIZATION -----
<Object Serialization> → <Class Header> <Value Serialization>
<Value Serialization> → 	<Base Serialization> | 
						<Collection Serialization> | 
						<Map Serialization> | 
						<Type Independent Serialization> | 
						<Null Serialization> 


----- BASE SERIALIZATION -----
<Base Serialization> → 	<Boolean Serialization> | 
						<Double Serialization> | 
						<Float Serialization> | 
						<Type Independent Serialization> | 
						<Null Serialization> 
						
<Boolean Serialization> -> 	<Class Name> <Integer Serialization (truth value: 1 for true, 0 for false)> 
<Double Serialization> -> 	<Class Name> <Double Serialization as bytes or String> 
<Float Serialization> -> 	<Class Name> <Float Serialization as bytes or String> 
<Long Serialization> -> 		<Class Name> <Long Serialization as bytes or String> 
<Integer Serialization> -> 	<Class Name> <Integer Serialization as bytes or String> 
<Short Serialization> -> 	<Class Name> <Short Serialization as bytes or String> 
<String Serialization> -> 	<Class Name> <Integer Serialization (length of String)> <Character Serialization as bytes or String>* 


----- COLLECTION SERIALIZATION -----
<Collection Serialization> →	<ArrayList Serialization> | 
							<HashSet Serialization> | 
							<Vector Serialization> 
							
<ArrayList Serialization> -> <Class Name> <Integer as bytes or String (Size of collection)> <Object Serialization>*
<HashSet Serialization> -> 	<Class Name> <Integer as bytes or String (Size of collection)> <Object Serialization>*
<ArrayList Serialization> -> <Class Name> <Integer as bytes or String (Size of collection)> <Object Serialization>*


----- MAP SERIALIZATION -----
<Map Serialization> →	<HashMap Serialization> | 
						<Hashtable Serialization>
						
<HashMap Serialization> -> 	<Class Name> <Integer as bytes or String (Number of keys)> [<Object serialization (Key)> <Object serialization (Value)>]*
<Hashtable Serialization> ->	<Class Name> <Integer as bytes or String (Number of keys)> [<Object serialization (Key)> <Object serialization (Value)>]*


----- TYPE INDEPENDENT SERIALIZATION -----
<TypeIndependent Serialization> →	<Array Serialization> | 
									<Bean Serialization> | 
									<Enum Serialization> |
									<ListPattern Serialization> 
									
<Array Serialization> ->			<TypeFree Header> <String Serialization (Class name)> <String Serialization (Component class name)> <Integer Serialization (Size of Array)> <Object Serialization>*
<Bean Serialization> -> 			<TypeFree Header> <String Serialization (Class name)> <Integer Serialization (Number bean properties)> [<String Serialization (Prop name)> <Object Serialization (Prop Value)>]*
<Enum Serialization> ->			<TypeFree Header> <String Serialization (Class name)> <String Serialization (Result of enum.toString())> 
<List Pattern Serialization> ->	<TypeFree Header> <String Serialization (Class name)> <Integer Serialization (Size of list)> <Object Serialization>*


----- NULL SERIALIZATION -----
<Null Serialization> -> <Null Header> <String as bytes or String (Null value)> 
```




