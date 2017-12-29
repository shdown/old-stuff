package app;

import javax.microedition.lcdui.*;
import java.util.Vector;
import calc.*;
import options.OptionsManager;
import ral.Real;

public class CalcCanvas extends Canvas {

	protected static class Button {
		public int id;
		public String text;
		public boolean isPressed = false;
		
		public Button(int id, String text) {
			this.id = id;
			this.text = text;
		}
	}
	
	protected class MyVariableProducer implements VariableProducer {
		public Real getVariableValue(int id) throws MissingVariableException {
			switch(id) {
			case GodObject.ID_ANS:
				if(answer == null)
					throw new MissingVariableException(id, "no answer yet");
				return answer;
			case GodObject.ID_X:
				throw new MissingVariableException(id, "going to plot");
			default:
				throw new UnknownTokenException("unknown variable: ID " + id);
			}
		}
	}

	protected final static Button buttons[] = {
		new Button(GodObject.ID_ONE, "1"),                    new Button(GodObject.ID_TWO, "2"),                    new Button(GodObject.ID_THREE, "3"),
		new Button(GodObject.ID_FOUR, "4"),                   new Button(GodObject.ID_FIVE, "5"),                   new Button(GodObject.ID_SIX, "6"),
		new Button(GodObject.ID_SEVEN, "7"),                  new Button(GodObject.ID_EIGHT, "8"),                  new Button(GodObject.ID_NINE, "9"),
		new Button(GodObject.ID_MINUS_OF_UNKNOWN_ARITY, "-"), new Button(GodObject.ID_ZERO, "0"),                   new Button(GodObject.ID_PERIOD, "."),

		new Button(GodObject.ID_PLUS, "+"),                   new Button(GodObject.ID_MINUS_OF_UNKNOWN_ARITY, "-"), new Button(GodObject.ID_MODULUS, "%"),
		new Button(GodObject.ID_DIV, "/"),                    new Button(GodObject.ID_MUL, "\u00b7"),               new Button(GodObject.ID_FACT, "!"),
		new Button(GodObject.ID_OPENING_BRACE, "("),          new Button(GodObject.ID_INPUT_BRACES, "( )"),         new Button(GodObject.ID_CLOSING_BRACE, ")"),
		new Button(GodObject.ID_POW, "^"),                    new Button(GodObject.ID_SQR, "\u00B2"),               new Button(GodObject.ID_SQRT, "\u221A"),

		new Button(GodObject.ID_SIN, "sin"),                  new Button(GodObject.ID_ASIN, "asin"),                new Button(GodObject.ID_LN, "ln"),
		new Button(GodObject.ID_COS, "cos"),                  new Button(GodObject.ID_ACOS, "acos"),                new Button(GodObject.ID_EXP, "exp"),
		new Button(GodObject.ID_TAN, "tan"),                  new Button(GodObject.ID_ATAN, "atan"),                new Button(GodObject.ID_E, "e"),
		new Button(GodObject.ID_COT, "cot"),                  new Button(GodObject.ID_ACOT, "acot"),                new Button(GodObject.ID_PI, "\u03C0"),

		new Button(GodObject.ID_FLOOR, "floor"),              new Button(GodObject.ID_ABS, "abs"),                  new Button(GodObject.ID_SGN, "sgn"),
		new Button(GodObject.ID_ROUND, "round"),              new Button(GodObject.ID_AND, " AND "),                new Button(GodObject.ID_EPS, "\u03B5"),
		new Button(GodObject.ID_CEIL, "ceil"),                new Button(GodObject.ID_OR, " OR "),                  new Button(GodObject.ID_ANS, "Ans"),
		new Button(GodObject.ID_FRAC, "frac"),                new Button(GodObject.ID_XOR, " XOR "),                new Button(GodObject.ID_X, "x"),
	};
	protected final static int screensNumber = buttons.length / 12;
	
	protected Calc midlet;
	protected PlotManager plotManager;

	protected int currentScreen = 0;
	protected Vector input = new Vector();
	protected int cursorPos = 0;

	protected Real answer = null;
	protected String answerString = "";
	protected boolean errorHappened = false;
	protected MyVariableProducer variableProducer = new MyVariableProducer();

	protected int screenWidth, screenHeight;
	protected int buttonsOffset, buttonWidth, buttonHeight;
	protected Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
	protected int fontHeight = font.getHeight();
	protected int inputLineHeight = fontHeight * 2;
	protected int menuBarHeight = fontHeight;

	protected int
		colorBg = 0xffffff,
		colorFg = 0,
		colorButtonBg = 0xcccccc,
		colorPressedButtonBg = 0x4444ff,
		colorError = 0xff0000;
	
	protected void recalculatePixelsizes() {
		screenWidth = getWidth();
		screenHeight = getHeight();
		
		buttonsOffset = 3;
		buttonWidth = (screenWidth - buttonsOffset*4) / 3;
		buttonHeight = (screenHeight - inputLineHeight - menuBarHeight - buttonsOffset*5) / 4;		
	}
	
	protected void showNotify() {
		setFullScreenMode(true);
		recalculatePixelsizes();
	}
	
	protected void sizeChanged(int newWidth, int newHeight) {
		recalculatePixelsizes();
	}

	public CalcCanvas(Calc midlet, PlotManager plotManager) {
		this.midlet = midlet;
		this.plotManager = plotManager;
		
		showNotify();
	}

/*****************************************************************************/		
	public String getAnswerString() {
		return answer == null ? null : answer.toString();
	}

/*****************************************************************************/	
	protected int getButtonX(int column) {
		return buttonsOffset + column * (buttonWidth + buttonsOffset);
	}
	
	protected int getButtonY(int row) {
		return inputLineHeight + buttonsOffset + row * (buttonHeight + buttonsOffset);
	}

/*****************************************************************************/
	protected void paint(Graphics g) {
		g.setFont(font);

		g.setColor(colorBg);
		g.fillRect(0, 0, screenWidth, screenHeight);
		g.setColor(colorFg);

		int inputPos = input.size() - 1;
		boolean cursorDrawn = false;
		if(cursorPos == input.size()) {
			g.drawLine(screenWidth-1, 1, screenWidth-1, fontHeight-2);
			cursorDrawn = true;
		}
		while(true) {
			int x = screenWidth;
			for(; inputPos != -1; --inputPos) {
				String s = ((Button)input.elementAt(inputPos)).text;
				x -= font.stringWidth(s);
				g.drawString(s, x, 0, 0);
				if(x < 0)
					break;
				if(inputPos == cursorPos) {
					g.drawLine(x, 1, x, fontHeight-2);
					cursorDrawn = true;
				}
			}
			if(!cursorDrawn) {
				g.setColor(colorBg);
				g.fillRect(0, 0, screenWidth, fontHeight);
				g.setColor(colorFg);
			} else
				break;
		}
		
		g.drawLine(0, fontHeight, screenWidth, fontHeight);
		g.setColor(errorHappened ? colorError : colorFg);
		g.drawString(answerString, screenWidth, fontHeight, g.RIGHT|g.TOP);
		g.setColor(colorFg);
		g.drawLine(0, fontHeight*2-1, screenWidth, fontHeight*2-1);

		for(int row = 0; row < 4; ++row) {
			for(int column = 0; column < 3; ++column) {
				int index = currentScreen * 12 + row * 3 + column;
				int x = getButtonX(column);
				int y = getButtonY(row);
				g.setColor(buttons[index].isPressed ? colorPressedButtonBg : colorButtonBg);
				g.fillRect(x, y, buttonWidth, buttonHeight);
				g.setColor(colorFg);
				g.drawString(buttons[index].text, x + buttonWidth/2, y + buttonHeight/2 - fontHeight/2, g.HCENTER|g.TOP);
			}
		}

		g.drawLine(0, screenHeight - menuBarHeight, screenWidth, screenHeight - menuBarHeight);

		g.drawString("Menu", 0, screenHeight, g.LEFT|g.BOTTOM);
		g.drawString("=", screenWidth/2, screenHeight, g.HCENTER|g.BOTTOM);
		g.drawString("C", screenWidth, screenHeight, g.RIGHT|g.BOTTOM);
	}

/*****************************************************************************/	
	protected void repaintButton(int screenIndex) {
		int x = getButtonX(screenIndex % 3);
		int y = getButtonY(screenIndex / 3);
		repaint(x, y, buttonWidth, buttonHeight);
	}
	
	protected void repaintInput() {
		repaint(0, 0, screenWidth, fontHeight);
	}
	
	protected void repaintAnswer() {
		repaint(0, fontHeight, screenWidth, fontHeight);
	}

/*****************************************************************************/
	protected void setError(String message) {
		answer = null;
		errorHappened = true;
		answerString = message;
	}

	protected void setAnswer(Real answer) {
		this.answer = answer;
		errorHappened = false;
		answerString = "= " + answer.toString();
	}
	
	protected String getExpressionString() {
		StringBuffer buf = new StringBuffer();
		for(int i = 0; i < input.size(); ++i)
			buf.append(((Button)input.elementAt(i)).text);
		return buf.toString();
	}
	
	protected void evaluate() {
		int ids[] = new int[input.size()];
		for(int i = 0; i < ids.length; ++i)
			ids[i] = ((Button)input.elementAt(i)).id;

		try {
			setAnswer(Evaluator.evaluate(InfixToPostfixConverter.convert(
				DigitPartsMerger.merge(ids)),
				variableProducer,
				true));
		} catch(InvalidExpressionException ex) {
			setError(ex.getMessage());
		} catch(MissingVariableException ex) {
			switch(ex.id) {
			case GodObject.ID_ANS:		
				setError("no answer yet");
				break;
			case GodObject.ID_X:
				try {
					plotManager.addFunction(getExpressionString(), ids);
					midlet.showPlotCanvas();
				} catch(InvalidExpressionException ex2) {
					setError(ex2.getMessage());
				} catch(MissingVariableException ex2) {
					setError(ex2.getMessage());
				}
				break;
			default:
				throw new UnknownTokenException("unknown variable: ID " + ex.id);
			}
		}
		repaintAnswer();
	}

/*****************************************************************************/
	protected Button findButtonById(int id) {
		for(int i = 0; i < buttons.length; ++i)
			if(buttons[i].id == id)
				return buttons[i];
		return null;
	}
	
	protected void insertBraces() {
		input.insertElementAt(findButtonById(GodObject.ID_OPENING_BRACE), cursorPos++);
		input.insertElementAt(findButtonById(GodObject.ID_CLOSING_BRACE), cursorPos);
	}

	protected void buttonPressed(int screenIndex) {
		Button b = buttons[currentScreen * 12 + screenIndex];		
		if(b.id != GodObject.ID_INPUT_BRACES) {
			input.insertElementAt(b, cursorPos++);
		}
		boolean autoInsertBraces = OptionsManager.autoInsertBraces.getValue();
		if(b.id == GodObject.ID_INPUT_BRACES || (autoInsertBraces && Util.isFunction(b.id))) {
			insertBraces();
		}
		b.isPressed = true;
		repaintButton(screenIndex);
		repaintInput();
	}

	protected void buttonReleased(int screenIndex) {
		buttons[currentScreen * 12 + screenIndex].isPressed = false;
		if(currentScreen == 0) {
			repaintButton(screenIndex);
		} else {
			currentScreen = 0;
			repaint();
		}
	}
	
	protected void backspacePressed() {
		int removePos = (cursorPos == 0) ? 0 : (cursorPos - 1);
		if(0 <= removePos && removePos < input.size()) {
			input.removeElementAt(removePos);
			cursorPos = removePos;
		}
		repaintInput();
	}
	
	protected void screenSwitchRequested(int direction) {
		currentScreen += direction;
		if(currentScreen < 0)
			currentScreen = screensNumber - 1;
		if(currentScreen >= screensNumber)
			currentScreen = 0;
		repaint();
	}
	
	protected void cursorMovementRequested(int direction) {
		cursorPos += direction;
		if(cursorPos < 0)
			cursorPos = input.size();
		if(cursorPos > input.size())
			cursorPos = 0;
		repaintInput();
	}
	
	protected void clearInput() {
		input.removeAllElements();
		cursorPos = 0;
		repaintInput();
	}
	
	protected void cursorHome() {
		cursorPos = 0;
		repaintInput();
	}

	protected void cursorEnd() {
		cursorPos = input.size();
		repaintInput();
	}
	
/*****************************************************************************/
	protected final static int BUTTON_UNKNOWN = -1;

	protected int keyCodeToButtonIndex(int keyCode) {		
		if('1' <= keyCode && keyCode <= '9')
			return keyCode - '1';
		if(keyCode == '*')
			return 9;
		if(keyCode == '0')
			return 10;
		if(keyCode == '#')
			return 11;
		return BUTTON_UNKNOWN;
	}

	protected void keyPressed(int keyCode) {
		switch(KeyClassifier.classifyKey(keyCode)) {
		case KeyClassifier.K_NUMERIC:
			buttonPressed(keyCodeToButtonIndex(keyCode));
			break;
		case KeyClassifier.K_MENU_RIGHT:
			backspacePressed();
			break;
		case KeyClassifier.K_MENU_LEFT:
			midlet.showMenu();
			break;
		case KeyClassifier.K_JOY_UP:
			screenSwitchRequested(-1);
			break;
		case KeyClassifier.K_JOY_DOWN:
			screenSwitchRequested(1);
			break;
		case KeyClassifier.K_JOY_LEFT:
			cursorMovementRequested(-1);
			break;
		case KeyClassifier.K_JOY_RIGHT:
			cursorMovementRequested(1);
			break;
		case KeyClassifier.K_JOY_FIRE:
			evaluate();
			break;
		}
	}

	protected void keyReleased(int keyCode) {
		int button = keyCodeToButtonIndex(keyCode);
		if(button != BUTTON_UNKNOWN)
			buttonReleased(button);
	}

	protected void keyRepeated(int keyCode) {
		switch(KeyClassifier.classifyKey(keyCode)) {
		case KeyClassifier.K_MENU_RIGHT:
			clearInput();
			break;
		case KeyClassifier.K_JOY_LEFT:
			cursorHome();
			break;
		case KeyClassifier.K_JOY_RIGHT:
			cursorEnd();
			break;
		}
	}
}