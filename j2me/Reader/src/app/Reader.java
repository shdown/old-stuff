package app;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;
import java.io.InputStream;

public class Reader extends MIDlet {
	protected Display display = Display.getDisplay(this);
	protected ReaderCanvas readerCanvas;
	protected FileSystemBrowser fsBrowser;
	
	protected void show(Displayable d) {
		display.setCurrent(d);
	}
	
	public void showCanvas() {
		show(readerCanvas);
	}
	
	public void showBrowser() {
		show(fsBrowser);
	}
	
	public Reader() {
		readerCanvas = new ReaderCanvas(this);
		if(System.getProperty("device.model").equals("wtk-emulator")) {
			readFile(getClass().getResourceAsStream("/sample.txt"), "sample.txt");
		} else {
			fsBrowser = new FileSystemBrowser(this);
			showBrowser();
		}
	}
	
	public void readFile(InputStream stream, String fileName) {
		readerCanvas.readFile(stream, fileName, "UTF-8");
		showCanvas();
	}
	
	public void fatal(String title, String message) {
		Form form = new Form("fatal() invoked");
		form.append(new StringItem(title + ": ", message));
		show(form);
	}
	
	public void startApp() {
	}
	
	public void pauseApp() {
	}
	
	public void destroyApp(boolean notify) {
	}
}
