package app;

import javax.microedition.lcdui.*;

public class PlotMenu extends List implements CommandListener {
	protected Calc midlet;
	protected Command backCommand = new Command("Back", Command.BACK, 1);
	protected Command deleteCommand = new Command("Delete", Command.ITEM, 2);
	protected PlotManager plotManager;
	
	protected final static int iconSize;
	static {
		List list = new List("foo", List.IMPLICIT);
		list.append("bar", null);
		iconSize = list.getFont(0).getHeight();
	}
	
	protected static Image getColorHint(int color) {
		Image image = Image.createImage(iconSize, iconSize);
		Graphics g = image.getGraphics();
		g.setColor(color);
		g.fillRect(0, 0, iconSize, iconSize);
		return image;
	}

	public PlotMenu(Calc midlet, PlotManager plotManager) {
		super("Plot list", List.IMPLICIT);
		this.midlet = midlet;
		this.plotManager = plotManager;
		setCommandListener(this);
		addCommand(backCommand);
		addCommand(deleteCommand);
	}
	
	public void addFunction(String function) {
		int index = size();
		append(function, getColorHint(PlotManager.getColor(index)));
	}
	
	public void removeFunction(int index) {
		delete(index);
		for(int i = index; i < size(); ++i) {
			set(i, getString(i), getColorHint(PlotManager.getColor(i)));
		}
	}
	
	public void commandAction(Command command, Displayable displayable) {
		if(command == deleteCommand) {
			if(size() > 0) {
				plotManager.removeFunction(getSelectedIndex());
			}
		} else if(command == backCommand) {
			midlet.showPlotCanvas();
		}
	}
}