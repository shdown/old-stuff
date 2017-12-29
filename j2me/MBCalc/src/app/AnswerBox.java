package app;

import javax.microedition.lcdui.*;

public class AnswerBox extends TextBox implements CommandListener {
	protected Calc midlet;
	protected Command backCommand = new Command("Back", Command.SCREEN, 1);

	public AnswerBox(Calc midlet) {
		super("Answer", "", 256, TextField.ANY);
		this.midlet = midlet;
		
		setCommandListener(this);
		addCommand(backCommand);
	}
	
	public void update(String answer) {
		this.setString(answer);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == backCommand) {
			midlet.showMenu();
		}
	}
}