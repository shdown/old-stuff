package app;

import javax.microedition.lcdui.*;
import java.util.Vector;
import calc.*;
import ral.Real;

public class CalcCanvas extends Canvas {

	protected class Button {
		public int id;
		public String text;
		public boolean isPressed = false;
		
		public Button(int id, String text) {
			this.id = id;
			this.text = text;
		}
	}
	
	protected class MyVariableProducer implements VariableProducer {
		public Real x = null;
		
		public MyVariableProducer() {
		}
		
		public Real getVariableValue(int id) throws MissingVariableException {
			switch(id) {
				case GodObject.ID_ANS:
					if(answer == null)
						throw new MissingVariableException(id, "no answer yet");
					return new Real(answer);
				case GodObject.ID_X:
					if(x == null)
						throw new MissingVariableException(id, "no x (what?)");
					return x;
				default:
					throw new UnknownTokenException("unknown variable: ID " + id);
			}
		}
	}

	Button buttons[] = {
		new Button(GodObject.ID_ONE, "1"), new Button(GodObject.ID_TWO, "2"), new Button(GodObject.ID_THREE, "3"),
		new Button(GodObject.ID_FOUR, "4"), new Button(GodObject.ID_FIVE, "5"), new Button(GodObject.ID_SIX, "6"),
		new Button(GodObject.ID_SEVEN, "7"), new Button(GodObject.ID_EIGHT, "8"), new Button(GodObject.ID_NINE, "9"),
		new Button(GodObject.ID_MINUS_OF_UNKNOWN_ARITY, "-"), new Button(GodObject.ID_ZERO, "0"), new Button(GodObject.ID_PERIOD, "."),

		new Button(GodObject.ID_PLUS, "+"), new Button(GodObject.ID_MINUS, "-"),   new Button(GodObject.ID_MODULUS, "%"),
		new Button(GodObject.ID_DIV, "/"), new Button(GodObject.ID_MUL, "*"),   new Button(GodObject.ID_FACT, "!"),
		new Button(GodObject.ID_OPENING_BRACE, "("), new Button(GodObject.ID_INPUT_BRACES, "( )"), new Button(GodObject.ID_CLOSING_BRACE, ")"),
		new Button(GodObject.ID_POW, "^"), new Button(GodObject.ID_SQR, "^2"),  new Button(GodObject.ID_SQRT, "sqrt"),

		new Button(GodObject.ID_SIN, "sin"), new Button(GodObject.ID_ASIN, "asin"), new Button(GodObject.ID_LN, "ln"),
		new Button(GodObject.ID_COS, "cos"), new Button(GodObject.ID_ACOS, "acos"), new Button(GodObject.ID_EXP, "exp"),
		new Button(GodObject.ID_TAN, "tan"), new Button(GodObject.ID_ATAN, "atan"), new Button(GodObject.ID_E, "e"),
		new Button(GodObject.ID_COT, "cot"), new Button(GodObject.ID_ACOT, "acot"), new Button(GodObject.ID_PI, "pi"),

		new Button(GodObject.ID_FLOOR, "floor"), new Button(GodObject.ID_ABS, "abs"),   new Button(GodObject.ID_SGN, "sgn"),
		new Button(GodObject.ID_ROUND, "round"), new Button(GodObject.ID_AND, " AND "), new Button(GodObject.ID_EPS, "Eps"),
		new Button(GodObject.ID_CEIL, "ceil"),  new Button(GodObject.ID_OR, " OR "),  new Button(GodObject.ID_ANS, "Ans"),
		new Button(GodObject.ID_FRAC, "frac"),  new Button(GodObject.ID_XOR, " XOR "), new Button(GodObject.ID_X, "x"),
	};
	
	int currentScreen = 0;
	int screensNumber = buttons.length / 12;
	Vector input = new Vector();
	int cursorPos = 0;

	Real answer = null;
	String answerString = new String();
	boolean errorHappened = false;
	MyVariableProducer vp = new MyVariableProducer();
	
	int screenWidth, screenHeight;
	int buttonsOffset, buttonWidth, buttonHeight;
	Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
	int fontHeight = font.getHeight();
	int inputLineHeight = fontHeight * 2;
	int menuBarHeight = fontHeight;
	int colorBg = 0xffffff,
		colorFg = 0,
		colorButtonBg = 0xbbbbbb,
		colorPressedButtonBg = 0x4444ff,
		colorError = 0xff0000;

	public CalcCanvas() {
		setFullScreenMode(true);
		
		screenWidth = getWidth();
		screenHeight = getHeight();
		
		buttonsOffset = screenWidth / 48;
		
		buttonWidth = (screenWidth - buttonsOffset*4) / 3;
		buttonHeight = (screenHeight - inputLineHeight - menuBarHeight - buttonsOffset*5) / 4;
	}

	public void paint(Graphics g) {
		g.setFont(font);

		g.setColor(colorBg);
		g.fillRect(0, 0, screenWidth, screenHeight);
		g.setColor(colorFg);
		
		if(cursorPos < 0 || cursorPos > input.size()) {
			g.drawString("Error: cursor is out of bounds.", 0, 0, 0);
			return;
		}
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
		g.drawLine(0, fontHeight*2, screenWidth, fontHeight*2);

		for(int row = 0; row < 4; ++row) {
			for(int column = 0; column < 3; ++column) {
				int index = currentScreen * 12 + row * 3 + column;
				int x = buttonsOffset + column * (buttonWidth + buttonsOffset);
				int y = inputLineHeight + buttonsOffset + row * (buttonHeight + buttonsOffset);
				g.setColor(buttons[index].isPressed ? colorPressedButtonBg : colorButtonBg);
				g.fillRect(x, y, buttonWidth, buttonHeight);
				g.setColor(colorFg);
				g.drawString(buttons[index].text, x + buttonWidth/2, y + buttonHeight/2, g.HCENTER|g.BASELINE);
			}
		}

		g.drawLine(0, screenHeight - menuBarHeight, screenWidth, screenHeight - menuBarHeight);

		g.drawString("Menu", 0, screenHeight, g.LEFT|g.BOTTOM);
		g.drawString("=", screenWidth/2, screenHeight, g.HCENTER|g.BOTTOM);
		g.drawString("C", screenWidth, screenHeight, g.RIGHT|g.BOTTOM);
	}
	
	void evaluate() {
		int ids[] = new int[input.size()];
		for(int i = 0; i < ids.length; ++i)
			ids[i] = ((Button)input.elementAt(i)).id;
		try {
			answer = Evaluator.evaluate(
				InfixToPostfixConverter.convert(DigitPartsMerger.merge(ids)),
				vp);
			answerString = "= " + answer.toString();
			errorHappened = false;
		} catch(InvalidExpressionException ex) {
			answer = null;
			answerString = ex.getMessage();
			errorHappened = true;
		} catch(MissingVariableException ex) {
			answer = null;
			errorHappened = true;
			switch(ex.id) {
			case GodObject.ID_ANS:		
				answerString = "no answer yet";
				break;
			case GodObject.ID_X:
				answerString = "'x' is unimplemented yet";
				break;
			default:
				answerString = "unknown variable missing (what?)";
				break;
			}
		}
		repaint();
	}

/*****************************************************************************/
	Button findButtonById(int id) {
		for(int i = 0; i < buttons.length; ++i)
			if(buttons[i].id == id)
				return buttons[i];
		return null;
	}
	
	void insertBraces() {
		input.insertElementAt(findButtonById(GodObject.ID_OPENING_BRACE), cursorPos++);
		input.insertElementAt(findButtonById(GodObject.ID_CLOSING_BRACE), cursorPos);
	}
	
	void buttonPressed(int index) {
		Button b = buttons[currentScreen * 12 + index];
		if(b.id == GodObject.ID_INPUT_BRACES) {
			insertBraces();
		} else {
			input.insertElementAt(b, cursorPos++);
		}
		b.isPressed = true;
		repaint();
	}

	void buttonReleased(int index) {
		buttons[currentScreen * 12 + index].isPressed = false;
		currentScreen = 0;
		repaint();
	}
	
	void backspacePressed() {
		int removePos = (cursorPos == 0) ? 0 : (cursorPos - 1);
		if(0 <= removePos && removePos < input.size()) {
			input.removeElementAt(removePos);
			cursorPos = removePos;
		}
		repaint();
	}
	
	void screenSwitchRequested(int direction) {
		currentScreen += direction;
		if(currentScreen < 0)
			currentScreen = screensNumber - 1;
		if(currentScreen >= screensNumber)
			currentScreen = 0;
		repaint();
	}
	
	void cursorMovementRequested(int direction) {
		cursorPos += direction;
		if(cursorPos < 0)
			cursorPos = input.size();
		if(cursorPos > input.size())
			cursorPos = 0;
		repaint();
	}
	
	void clearInput() {
		input.removeAllElements();
		cursorPos = 0;
		repaint();
	}
	
	void cursorHome() {
		cursorPos = 0;
		repaint();
	}

	void cursorEnd() {
		cursorPos = input.size();
		repaint();
	}
	
/*****************************************************************************/
	public final static int
		K_UNKNOWN = 0, BUTTON_UNKNOWN = -1,
		K_NUMERIC = 1,
		K_JOY_RIGHT = 2,
		K_JOY_LEFT = 3,
		K_JOY_DOWN = 4,
		K_JOY_UP = 5,
		K_JOY_FIRE = 6,
		K_MENU_RIGHT = 7,
		K_MENU_LEFT = 8;

	int classifyKey(int keyCode) {
		if(('0' <= keyCode && keyCode <= '9') || keyCode == '*' || keyCode == '#')
			return K_NUMERIC;
		switch(keyCode) {
		case -7:
			return K_MENU_RIGHT;
		case -6:
			return K_MENU_LEFT;
		case -5:
			return K_JOY_FIRE;
		case -4:
			return K_JOY_RIGHT;
		case -3:
			return K_JOY_LEFT;
		case -2:
			return K_JOY_DOWN;
		case -1:
			return K_JOY_UP;
		default:
			return K_UNKNOWN;
		}
	}

	int keyCodeToButtonIndex(int keyCode) {		
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

	public void keyPressed(int keyCode) {
		switch(classifyKey(keyCode)) {
		case K_NUMERIC:
			buttonPressed(keyCodeToButtonIndex(keyCode));
			break;
		case K_MENU_RIGHT:
			backspacePressed();
			break;
		case K_MENU_LEFT:
			/* unimplemented */
			break;
		case K_JOY_UP:
			screenSwitchRequested(-1);
			break;
		case K_JOY_DOWN:
			screenSwitchRequested(1);
			break;
		case K_JOY_LEFT:
			cursorMovementRequested(-1);
			break;
		case K_JOY_RIGHT:
			cursorMovementRequested(1);
			break;
		case K_JOY_FIRE:
			evaluate();
			break;
		}
	}

	public void keyReleased(int keyCode) {
		int button = keyCodeToButtonIndex(keyCode);
		if(button != BUTTON_UNKNOWN)
			buttonReleased(button);
	}

	public void keyRepeated(int keyCode) {
		switch(classifyKey(keyCode)) {
			case K_MENU_RIGHT:
				clearInput();
				break;
			case K_JOY_LEFT:
				cursorHome();
				break;
			case K_JOY_RIGHT:
				cursorEnd();
				break;
		}
	}

/*****************************************************************************/
}
