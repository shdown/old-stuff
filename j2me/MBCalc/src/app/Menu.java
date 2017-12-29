package app;

import javax.microedition.lcdui.*;

public class Menu extends List implements CommandListener {
	protected Calc midlet;
	protected Command selectCommand = List.SELECT_COMMAND;
	protected Command backCommand = new Command("Back", Command.BACK, 2);

	public Menu(Calc midlet) {
		super(
			"Menu", List.IMPLICIT, new String[] {
				"Show answer",
				"Options",
				"Exit",
			}, null);
		this.midlet = midlet;
		
		setCommandListener(this);
		addCommand(backCommand);
		addCommand(selectCommand);
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == selectCommand) {
			switch(this.getSelectedIndex()) {
			case 0:
				midlet.showAnswerBox();
				break;
			case 1:
				midlet.showOptions();
				break;
			case 2:
				midlet.exit();
				break;
			}
		} else if(command == backCommand) {
			midlet.showCanvas();
		}
	}
}