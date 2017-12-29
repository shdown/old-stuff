package app;

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;
import options.*;

public class Calc extends MIDlet {
	
	protected Display display = Display.getDisplay(this);

	protected CalcCanvas canvas;
	protected Menu menu;
	protected AnswerBox answerBox;
	protected OptionsForm optionsForm;	
	protected PlotCanvas plotCanvas;
	protected PlotMenu plotMenu;
	protected PlotManager plotManager;
	
	public void showAlert(String title, String message) {
		display.setCurrent(new Alert(title, message, null, null));
	}

	public void showCanvas() {
		display.setCurrent(canvas);
	}
	
	public void showMenu() {
		display.setCurrent(menu);
	}
	
	public void showAnswerBox() {
		String answer = canvas.getAnswerString();
		if(answer == null) {
			showAlert("Error", "No answer yet");
		} else {
			answerBox.update(answer);
			display.setCurrent(answerBox);
		}
	}
	
	public void showOptions() {
		display.setCurrent(optionsForm);
	}
	
	public void showPlotCanvas() {
		display.setCurrent(plotCanvas);
	}
	
	public void showPlotMenu() {
		display.setCurrent(plotMenu);
	}
	
	public void exit() {
		notifyDestroyed();
	}
	
	protected void loadingOptionsFailed(String exception) {
		Form form = new Form("Error");
		display.setCurrent(form);
		form.append("Failed to load options: " + exception);
		form.append("\nTrying to clear options...");
		try {
			OptionsManager.clear();
			form.append("\nDone. Now, please restart the application.");
			form.append("\nOptions will be reset to defaults.");
		} catch(OptionsException ex) {
			form.append("\nAnother error occurred: " + ex.toString());
		}
	}
	
	public Calc() {
		try {
			OptionsManager.load();
		} catch(OptionsException ex) {
			loadingOptionsFailed(ex.toString());
			return;
		} catch(IllegalArgumentException ex) {
			loadingOptionsFailed(ex.toString());
			return;
		}
		
		plotManager = new PlotManager();
		
		canvas = new CalcCanvas(this, plotManager);
		menu = new Menu(this);
		answerBox = new AnswerBox(this);
		optionsForm = new OptionsForm(this);
		plotCanvas = new PlotCanvas(this);
		plotMenu = new PlotMenu(this, plotManager);
		
		plotManager.setPlotMenu(plotMenu);
		plotManager.setPlotCanvas(plotCanvas);
		
		showCanvas();
	}
	
	public void startApp() {
	}
	
	public void pauseApp() {
	}
	
	public void destroyApp(boolean notify) {
		exit();
	}
}