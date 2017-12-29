package app;

import calc.*;

public class PlotManager {
	protected final static int plotColors[] = {
		0xff0000,
		0x0000ff,
		0x00aa00,
		0xaaaa00,
	};

	public static int getColor(int index) {
		return plotColors[index % plotColors.length];
	}	
	
	protected PlotMenu plotMenu = null;
	protected PlotCanvas plotCanvas = null;

	public PlotManager() {
	}
	
	public void setPlotMenu(PlotMenu plotMenu) {
		this.plotMenu = plotMenu;
	}
	
	public void setPlotCanvas(PlotCanvas plotCanvas) {
		this.plotCanvas = plotCanvas;
	}
	
	public void addFunction(String expression, int[] ids)
		throws InvalidExpressionException, MissingVariableException
	{
		plotMenu.addFunction(expression);
		plotCanvas.addFunction(ids);
	}
	
	public void removeFunction(int index) {
		plotMenu.removeFunction(index);
		plotCanvas.removeFunction(index);
	}
}