package app;

import javax.microedition.lcdui.*;
import calc.*;
import ral.Real;
import java.util.Vector;
import options.OptionsManager;

public class PlotCanvas extends Canvas {

	protected class MyVariableProducer implements VariableProducer {
		public Real getVariableValue(int id) throws MissingVariableException {
			switch(id) {
			case GodObject.ID_X:
				return xCurrent;
			case GodObject.ID_ANS:
				throw new MissingVariableException(id, "'Ans' with 'x' is unimplemented");
			default:
				throw new UnknownTokenException("unknown variable: ID " + id);
			}
		}
	}

	protected Calc midlet;
	
	protected int screenWidth, screenHeight, maxAbsoluteCoordValue;
	protected Real scale, xOffset, yOffset, xCurrent;
	protected Vector functions = new Vector();
	protected MyVariableProducer variableProducer = new MyVariableProducer();

	protected final static int
		colorBg = 0xffffff,
		colorFg = 0,
		colorDust = 0x999999,
		colorTimer = 0x0000ff;

	protected Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
	protected final static Real scaleMultiplier = new Real("1.25");
	protected final static Real.NumberFormat shortFormat = new Real.NumberFormat();
	
	protected void recalculatePixelsizes() {
		screenWidth = getWidth();
		screenHeight = getHeight();
		maxAbsoluteCoordValue = screenWidth * screenHeight;
	}
		
	protected void showNotify() {
		setFullScreenMode(true);
		recalculatePixelsizes();
	}
	
	protected void sizeChanged(int newWidth, int newHeight) {
		recalculatePixelsizes();
	}

	public PlotCanvas(Calc midlet) {
		this.midlet = midlet;
		
		showNotify();
		
		xOffset = new Real(-10);
		yOffset = new Real(-10);
		scale = new Real(10 * 2);
		scale.div(screenWidth);
		
		xCurrent = new Real(); // assigned in paint()
	}

	/*****************************************************************************/
	// axis = (-off / scale)
	// unit = (1 / scale)
	// line_off = axis % unit
	// n = axis / unit
	protected int getOffset(Real offset, Real unit) {
		Real result = new Real(unit);
		result.mul(offset);
		result.neg();
		result.mod(unit);
		result.round();
		return result.toInteger();
	}
	
	protected int safelyNegate(int value) {
		return value == Integer.MIN_VALUE ? Integer.MAX_VALUE : -value;
	}
		
	public void paint(Graphics g) {
		long timer = System.currentTimeMillis();

		g.setFont(font);
		g.setColor(colorBg);
		g.fillRect(0, 0, screenWidth, screenHeight);
		
		Real unit = new Real(scale);
		unit.recip();
		boolean drawAuxLines = unit.greaterEqual(4);
		int startX = getOffset(xOffset, unit);
		int startY = getOffset(yOffset, unit);
		int xAxisNumber = safelyNegate(xOffset.toInteger());
		int yAxisNumber = safelyNegate(yOffset.toInteger());
		Real lineOffset = new Real(0);
		for(int i = 0; ; lineOffset.add(unit), ++i) {
			int value = lineOffset.toInteger();
			if(startX+value >= screenWidth && startY+value >= screenHeight) {
				break;
			}
			if(drawAuxLines || i == xAxisNumber) {
				g.setColor(i == xAxisNumber ? colorFg : colorDust);
				g.drawLine(startX+value, 0, startX+value, screenHeight);
			}
			if(drawAuxLines || i == yAxisNumber) {
				g.setColor(i == yAxisNumber ? colorFg : colorDust);
				g.drawLine(0, startY+value, screenWidth, startY+value);
			}
		}

		boolean previousYExists = false;
		int previousY = 0;
		for(int i = 0; i < functions.size(); ++i) {
			g.setColor(PlotManager.getColor(i));
			xCurrent.assign(xOffset);
			for(int x = 0; x < screenWidth; xCurrent.add(scale), ++x) {
				Real result;
				try {
					result = Evaluator.evaluate((Vector)functions.elementAt(i), variableProducer, false);
				} catch(InvalidExpressionException ex) {
					ex.printStackTrace();
					continue;
				} catch(MissingVariableException ex) {
					ex.printStackTrace();
					continue;
				}
				if(result.isFinite()) {
					result.add(yOffset);
					result.div(scale);
					result.round();
					int y = safelyNegate(result.toInteger());
					if(previousYExists)
						g.drawLine(x, y, x-1, previousY);
					else
						g.drawLine(x, y, x, y);
					previousY = y;
					previousYExists = true;
				} else {
					previousYExists = false;
				}
			}
		}
		
		g.setColor(colorDust);

		Real yBottom = new Real(scale);
		yBottom.mul(screenHeight);
		yBottom.add(yOffset);
		yBottom.neg();
		
		Real yTop = new Real(yOffset);
		yTop.neg();
		
		shortFormat.maxwidth = OptionsManager.plotterMaxHintChars.getValue();
		
		g.drawString(yTop.toString(shortFormat), screenWidth/2, 0, g.TOP|g.HCENTER);
		g.drawString(xOffset.toString(shortFormat), 0, screenHeight/2, g.BASELINE|g.LEFT);
		g.drawString(xCurrent.toString(shortFormat), screenWidth, screenHeight/2, g.BASELINE|g.RIGHT);
		g.drawString(yBottom.toString(shortFormat), screenWidth/2, screenHeight, g.BOTTOM|g.HCENTER);
		
		g.setColor(colorFg);
		g.drawString("Menu", 0, screenHeight, g.LEFT|g.BOTTOM);
		g.drawString("Back", screenWidth, screenHeight, g.RIGHT|g.BOTTOM);
		
		timer = System.currentTimeMillis() - timer;
		g.setColor(colorTimer);
		g.drawString(timer + " ms", 1, 0, 0);
	}

/*****************************************************************************/		
	public void addFunction(int[] ids)
		throws InvalidExpressionException, MissingVariableException
	{
		Vector expr = InfixToPostfixConverter.convert(DigitPartsMerger.merge(ids));
		// check if it evaluates OK
		xCurrent.assign(0);
		Evaluator.evaluate(expr, variableProducer, false);

		functions.addElement(expr);
	}
	
	public void removeFunction(int index) {
		functions.removeElementAt(index);
	}

/*****************************************************************************/	
	protected Real getOffset(int dimension) {
		Real result = new Real(dimension / 4);
		result.mul(scale);
		return result;
	}
	
	protected void keyPressed(int keyCode) {
		switch(KeyClassifier.classifyKey(keyCode)) {
		case KeyClassifier.K_JOY_UP:
			yOffset.sub(getOffset(screenHeight));
			break;
		case KeyClassifier.K_JOY_DOWN:
			yOffset.add(getOffset(screenHeight));
			break;
		case KeyClassifier.K_JOY_LEFT:
			xOffset.sub(getOffset(screenWidth));
			break;
		case KeyClassifier.K_JOY_RIGHT:
			xOffset.add(getOffset(screenWidth));
			break;
		case KeyClassifier.K_MENU_RIGHT:
			midlet.showCanvas();
			break;
		case KeyClassifier.K_MENU_LEFT:
			midlet.showPlotMenu();
			break;
		case KeyClassifier.K_NUMERIC:
			switch(keyCode) {
			case '2':
				scale.div(scaleMultiplier);
				xOffset.div(scaleMultiplier);
				yOffset.div(scaleMultiplier);
				break;
			case '8':
				scale.mul(scaleMultiplier);
				xOffset.mul(scaleMultiplier);
				yOffset.mul(scaleMultiplier);
				break;
			}
			break;
		}
		repaint();
	}
}