package app;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Display;

public class Calc extends MIDlet {
	
	Display display = Display.getDisplay(this);
	CalcCanvas canvas = new CalcCanvas();

	public Calc() {
		display.setCurrent(canvas);
	}
	
	public void startApp() {
	}
	
	public void pauseApp() {
	}
	
	public void destroyApp(boolean notify) {
	}
}
