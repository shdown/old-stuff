package app;

import java.io.*;

public class WordReader {
	protected InputStream stream;
	protected int position = 0;
	protected boolean endOfLine = false;
	protected boolean endOfFile = false;
	protected String encoding;

	protected byte[] cache = new byte[8192];
	protected int cacheStart = 0, cacheFill = 0;
	protected int available;
	
	public WordReader(InputStream stream, String encoding) throws IOException {
		this.stream = stream;
		this.encoding = encoding;
		available = stream.available();
		stream.mark(available);
	}
	
	public boolean isEOL() {
		return endOfLine;
	}
	
	public boolean isEOF() {
		return endOfFile;
	}
	
	public int getPosition() {
		return position;
	}
	
	protected boolean cacheMiss() {
		return position < cacheStart || position >= cacheStart+cacheFill;
	}
	
	protected void updateCache() throws IOException {
		cacheStart = position;
		cacheFill = stream.read(cache);
	}
	
	public void setPosition(int position) throws IOException {
		this.position = position;
		endOfFile = endOfLine = false;
		if(cacheMiss()) {
			stream.reset();
			stream.mark(available);
			int skipped = (int)stream.skip(position);
			if(skipped != position) {
				throw new IOException("skip() failed");
			}
			updateCache();
		}
	}
	
	protected int read() throws IOException {
		if(!cacheMiss()) {
			return cache[(position++) - cacheStart];
		} else if(position < available) {
			updateCache();
			++position;
			return cache[0];
		} else {
			return -1;
		}
	}
	
	public String nextWord() throws IOException {
		byte[] buf = new byte[32];
		int bytesRead = 0;
		boolean endOfWord = false;
		endOfLine = endOfFile = false;
		while(!endOfWord) {
			int code = read();
			if(code == -1) {
				endOfFile = endOfWord = true;
			} else if(code == ' ') {
				buf[bytesRead] = (byte)code;
				endOfWord = true;
			} else if(code == '\r') {
				// We ignore '\r', supposing '\n' will be read next.
			} else if(code == '\n') {
				endOfLine = endOfWord = true;
			} else {
				buf[bytesRead] = (byte)code;
			}
			++bytesRead;
			if(bytesRead == buf.length) {
				byte[] newBuf = new byte[buf.length * 2];
				for(int i = 0; i < buf.length; ++i)
					newBuf[i] = buf[i];
				buf = newBuf;
			}
		}
		return new String(buf, 0, bytesRead, encoding);
	}
	
	public void close() throws IOException {
		stream.close();
	}
}