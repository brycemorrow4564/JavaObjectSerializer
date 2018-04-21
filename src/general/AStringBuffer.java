package general;

import java.io.NotSerializableException;
import java.lang.reflect.Method;

import valueSerializer.ValueSerializer;

public class AStringBuffer {
	
	protected StringBuffer sb;
	protected int position; 
	
	public AStringBuffer(StringBuffer aSb) {
		sb = aSb; 
	}
	
	public AStringBuffer(StringBuffer aSb, int aPosition) {
		sb = aSb;
		position = aPosition;
	}
	
	public int getPosition() {
		return position; 
	}
	
	public boolean startsWith(String header) {
		StringBuilder builder = new StringBuilder(); 
		for (int i = position; i < header.length(); i++) {
			builder.append(sb.charAt(i));
		}
		return header.equals(builder.toString());
	}
	
	public String readStringBufferTillDelimiter() throws NotSerializableException {
		Character c = null;
		StringBuilder builder = new StringBuilder(); 
		while (true) {
			c = readCharacter(); 
			if (c == null) {
				throw new NotSerializableException("Unexpectedly reached end of StringBuffer");
			} else if (c == ValueSerializer.DELIMETER) {
				break; 
			} else {
				builder.append(c);
			}
		}
		return builder.toString();
	}
	
	public String readCharacters(int n) throws NotSerializableException {
		Character c = null;
		StringBuilder builder = new StringBuilder(); 
		for (int i = 0; i < n; i++) {
			c = readCharacter(); 
			if (c == null) {
				throw new NotSerializableException("Unexpectedly reached end of StringBuffer");
			} else {
				builder.append(c);
			}
		}
		return builder.toString();
	}
	
	public Character readCharacter() {
		if (position >= sb.length()) {
			return null;  
		}
		Character c = sb.charAt(position);
		position += 1; 
		return c; 
	}
	
	public Object executeStringBufferMethod(Method method, Object[] args) {
		/* Reflection based StringBuffer method executor. This isn't optimal but is a functional 
		 * workaround as the StringBuffer class is final and we cannot subclass it 
		 * directly. This has to do with thread safety issues but will not be impacted 
		 * by our wrapper class. */
		try {
			Class<?>[] paramTypes = method.getParameterTypes();
			String methodName = method.getName(); 
			if (sb.getClass().getMethod(methodName, paramTypes).equals(method)) {
				method.invoke(sb, args);
			} else {
				System.out.println("Error: called a methon a StringBuffer object that it does not have access to");
			}
		} catch (Exception e) {
			System.out.println("Error: called a methon a StringBuffer object that it does not have access to");
		}
		return method;
	}
	
	public String substring(int start, int end) {
		return sb.substring(start, end);
	}

}
