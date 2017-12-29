package app;

import javax.microedition.lcdui.*;
import java.io.*;
import java.util.Stack;

public class ReaderCanvas extends Canvas {
	protected Reader midlet;
	protected WordReader wordReader;
	
	protected Stack pagePositions;
	protected int nextPagePosition;
	protected String encoding;
	protected String fileName;
	
	protected Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
	protected int fontHeight = font.getHeight();

	protected int screenWidth;
	protected int screenHeight;
	protected int linesNumber;
	
	protected final static int
		bgColor = 0xf0fff0,
		fgColor = 0x000000,
		lineColor = 0x99a899,
		menubarBgColor = 0x99a899,
		menubarFgColor = 0xffffff;

	protected void recalculatePixelsizes() {
		screenWidth = getWidth();
		screenHeight = getHeight();
		linesNumber = (screenHeight - fontHeight) / fontHeight;
	}

	protected void showNotify() {
		setFullScreenMode(true);
		recalculatePixelsizes();
	}

	protected void sizeChanged(int newWidth, int newHeight) {
		recalculatePixelsizes();
	}

	public ReaderCanvas(Reader midlet) {
		this.midlet = midlet;
		showNotify();
	}

	public void readFile(InputStream stream, String fileName, String encoding) {
		this.fileName = fileName;
		this.encoding = encoding;
		try {
			wordReader = new WordReader(stream, encoding);
		} catch(IOException ex) {
			midlet.fatal("Can't create WordReader", ex.toString());
		}
		nextPagePosition = -1;
		pagePositions = new Stack();
		pagePositions.push(new Integer(0));
	}

	protected void finishReading() {
		try {
			wordReader.close();
		} catch(IOException ex) {
			midlet.fatal("Can't close file", ex.toString());
		}
		midlet.showBrowser();
	}

	public void paint(Graphics g) {
		g.setFont(font);
		
		g.setColor(bgColor);
		g.fillRect(0, 0, screenWidth, screenHeight);
		g.setColor(fgColor);
		
		try {
			wordReader.setPosition(((Integer)pagePositions.peek()).intValue());
		} catch(IOException ex) {
			midlet.fatal("Can't jump to a position", ex.toString());
			return;
		}
		
		nextPagePosition = -1;
		int x = 0;
		int y = screenHeight % fontHeight;
		for(int line = 0; line < linesNumber && !wordReader.isEOF();) {
			int prePosition = wordReader.getPosition();
			String word;
			try {
				word = wordReader.nextWord();
			} catch(IOException ex) {
				midlet.fatal("Can't read from file", ex.toString());
				return;
			}
			int wordWidth = font.stringWidth(word);
			if(x + wordWidth > screenWidth) {
				if(line == linesNumber-1) {
					nextPagePosition = prePosition;
					break;
				}
				if(x > 0) {
					x = 0;
					y += fontHeight;
					line++;
				}
				if(wordWidth > screenWidth) {
					StringBuffer drawnPart = new StringBuffer();
					boolean offScreen = false;
					do {
						int len = word.length();
						while(font.substringWidth(word, 0, len) > screenWidth)
							--len;
						g.drawString(word.substring(0, len), x, y, 0);
						
						drawnPart.append(word.substring(0, len));
						word = word.substring(len);
						
						if(line == linesNumber-1) {
							int drawnBytes;
							try {
								drawnBytes = drawnPart.toString().getBytes(encoding).length;
							} catch(UnsupportedEncodingException ex) {
								midlet.fatal("Encoding is unsupported", ex.toString());
								return;
							}
							nextPagePosition = prePosition + drawnBytes;
							offScreen = true;
							break;
						}
						x = 0; // it's already been 0.
						y += fontHeight;
						line++;
					} while(font.stringWidth(word) > screenWidth);
					if(offScreen) {
						break;
					}
					g.drawString(word, x, y, 0);
					wordWidth = font.stringWidth(word); // update wordWidth
				} else {
					g.drawString(word, x, y, 0);
				}
			} else {
				g.drawString(word, x, y, 0);
			}
			x += wordWidth;
			if(wordReader.isEOL()) {
				if(line == linesNumber-1) {
					nextPagePosition = wordReader.getPosition();
					break;
				}
				x = 0;
				y += fontHeight;
				line++;
				g.setColor(lineColor);
				g.drawLine(0, y, screenWidth, y);
				g.setColor(fgColor);				
			}
		}
		
		if(x > 0) {
			y += fontHeight;
		}
		g.setColor(lineColor);
		g.drawLine(0, y, screenWidth, y);
		
		g.setColor(menubarBgColor);
		g.fillRect(0, screenHeight - fontHeight, screenWidth, screenHeight);
		g.fillRect(0, 0, screenWidth, screenHeight % fontHeight);
		
		g.setColor(menubarFgColor);
		String back = "Back";
		g.drawString("Back | " + fileName, 0, screenHeight - fontHeight, 0);
	}

	protected void nextPage() {
		if(nextPagePosition != -1)
			pagePositions.push(new Integer(nextPagePosition));
	}

	protected void prevPage() {
		if(!pagePositions.isEmpty())
			pagePositions.pop();
		if(pagePositions.isEmpty())
			pagePositions.push(new Integer(0));
	}

	protected void keyPressed(int keyCode) {
		switch(KeyClassifier.classifyKey(keyCode)) {
		case KeyClassifier.K_JOY_UP:
			prevPage();
			repaint();
			break;
		case KeyClassifier.K_JOY_DOWN: 
			nextPage();
			repaint();
			break;
		case KeyClassifier.K_MENU_LEFT:
			finishReading();
			break;
		default:
			repaint();
			break;
		}
	}
}
