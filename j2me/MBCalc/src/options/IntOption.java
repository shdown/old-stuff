package options;

import java.io.*;

public class IntOption extends Option {
	public int value;
	
	public IntOption(int value) {
		setValue(value);
	}
	
	public int getValue() {
		return value;
	}
	
	public void setValue(int value) {
		this.value = value;
	}
	
	public void write(DataOutputStream stream) throws IOException {
		stream.writeInt(value);
	}
	
	public void read(DataInputStream stream) throws IOException {
		setValue(stream.readInt());
	}
}