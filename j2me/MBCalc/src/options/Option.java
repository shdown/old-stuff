package options;

import java.io.*;

public abstract class Option {
	public abstract void write(DataOutputStream stream) throws IOException;
	public abstract void read(DataInputStream stream) throws IOException;
}