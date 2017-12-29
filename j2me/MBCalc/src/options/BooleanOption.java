package options;

import java.io.*;

public class BooleanOption extends Option {
	public boolean value;
	
	public BooleanOption(boolean value) {
		setValue(value);
	}
	
	public boolean getValue() {
		return value;
	}
	
	public void setValue(boolean value) {
		this.value = value;
	}
	
	public void write(DataOutputStream stream) throws IOException {
		stream.writeBoolean(value);
	}
	
	public void read(DataInputStream stream) throws IOException {
		setValue(stream.readBoolean());
	}
}

