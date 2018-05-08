package general;

import java.io.NotSerializableException;
import java.lang.reflect.Method;

import valueSerializer.ValueSerializer;

public class AStringBuffer {
	
	protected StringBuffer sb;
	protected int position; 
	
	public AStringBuffer(StringBuffer aSb) {
		sb = aSb; 
		position = 0;
	}
	
	public int getPosition() {
		return position; 
	}
	
	public boolean startsWith(String header) {
		StringBuilder builder = new StringBuilder(); 
		if (sb.length() < header.length() + position) return false; 
		for (int i = position; i < position + header.length(); i++) { builder.append(sb.charAt(i)); }
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
	
	//returns null if no character to read 
	public Character readCharacter() {
		return (position >= sb.length()) ? null : sb.charAt(position++);
	}
	
	public void append(String str) {
		sb.append(str);
	}
	
	public int length() {
		return sb.length(); 
	}
	
	public String toString() {
		return sb.toString();
	}
	
	public String substring(int start, int end) {
		return sb.substring(start, end);
	}

}
