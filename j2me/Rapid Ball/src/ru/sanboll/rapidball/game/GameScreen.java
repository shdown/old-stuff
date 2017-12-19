package ru.sanboll.rapidball.game;
/** ������� ����� */
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import java.util.*;
import ru.sanboll.rapidball.Main;

public class GameScreen extends GameCanvas implements Runnable {
    
    /** ������ � ������ ������ */
    private int iWidth, iHeight;
    
    /** ���� ������������ ��� ��� */
    private boolean fIsPlaying;
    
    /** ������ ���� */
    private static Sprite ball;

    /** ������� ���� */
    private static int iBallDiameter;
    
    /** �������� ���� ���� */
    private int iBallVelocity;

    /** ����������� �������� ���� */
    private static int iMaxVelocity = 9;

    /** ��������� ���� ���� */
    private static int iBallAcceleration = 1;
    
    /** ���������, �� ������� �� ������� */
    private Vector vPlatforms;
    
    /** ����� */
    private int nPoints;

    /** ����� */
    private int nLives;
    
    /** ������������ ���������� ������ */
    private static int nMaxLives = 10;
    
    /** �������� ���� */
    private int iWorldSpeed;
    
    /** ������������ �������� ���� */
    private static int iMaxWorldSpeed = 7;
    
    /** ������� ���������� �������� ���� */
    private int tWorldSpeedInc;
    
    /** ��������� ��������� ����� */
    private static Random rand = new Random(System.currentTimeMillis());
    
    /** ������� ��������� ����� ��������� */
    private int tNewPlatform;

    /** ������������� ����� � ������� */
    private static int iAirControl = 5;
    
    /** �������� �������� ����� � �� */
    private static final long tlDelay = 24;

    private static int nFps = (int)(1000 / tlDelay);
    
    /** �����, ���������� � ���� */
    private long tlGameTime;
    
    /** ���������� �������� ���� */
    private int iDirection;

    /** ������ �� ������� ����� */
    private Main main;
    
    /** ����������� ���� � ������ */
    public static Image[] imgLetters;
    
    /** �������� �� ��� ��������� */
    boolean fCollides;
    
    /** ������ � ��������� ������ */
    public final static String strLetters = "SL1234567890";

    /** ��������� ��������� (��� ��������� ���������) */
    private int pointerState = 0;

    public GameScreen(Main main) {
        // ������������� ��������� ��������� ������
        super(true);
        // ������ �� ������� �����
        this.main = main;
        // ������������� �����
        setFullScreenMode(true);
        // �������� ������ � ������ ������
        iWidth = getWidth();
        iHeight = getHeight();
    }

    public static void loadStatic() {
        try {
            Image img = Image.createImage("/png/ball.png");
            ball = new Sprite(img);
            iBallDiameter = img.getWidth();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        try {
            int w[] = {13, 11, 6, 7, 7, 8, 7, 7, 7, 7, 7, 7}, x = 0;
            imgLetters = new Image[strLetters.length()];
            Image font = Image.createImage("/png/font.png");
            for(int i = 0; i < imgLetters.length; i++) {
                imgLetters[i] =
                    Image.createImage(font, x, 0, w[i], font.getHeight(), 0);
                x += w[i];
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void fntDrawString(String s, int x, int y, Graphics g) {
        int ax = x, j;
        for(int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            for(j = 0; j < strLetters.length(); j++)
                if(strLetters.charAt(j) == ch) break;
            g.drawImage(imgLetters[j], ax, y, 0);
            ax += imgLetters[j].getWidth();
        }
    }

    public static int fntStringWidth(String s) {
        int width = 0, j;
        for(int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            for(j = 0; j < strLetters.length(); j++)
                if(strLetters.charAt(j) == ch) break;
            width += imgLetters[j].getWidth();
        }
        return width;
    }

    public static int fntGetHeight() {
        return imgLetters[0].getHeight();
    }

    public void replayGame() {
        stopGame();
        initGame();
        startGame();
    }

    private void initGame() {
        ball.setPosition(iWidth >> 1, 0);
        vPlatforms = new Vector();
        tlGameTime = 0;
        iBallVelocity = 0;
        iWorldSpeed = 3;
        tWorldSpeedInc = nMillisToFrames(12000);
        tNewPlatform = nMillisToFrames(120);
        nPoints = 0;
        nLives = 3;
    }

    private int nMillisToFrames(int nMillis) {
        return nMillis / nFps;
    }

    public void startGame() {
        fIsPlaying = true;
        new Thread(this).start();
    }

    public void stopGame() {
        fIsPlaying = false;
    }

    public void run() {
        long tlTime;
        while(fIsPlaying) {
            // �������� �����
            tlTime = System.currentTimeMillis();
            // �������� ��������� ������
            getDirection();
            // �������� ��������� ����   
            update();
            // ������������
            draw();
            // �������� �����
            flushGraphics();
            // ���������
            tlTime += (tlDelay - System.currentTimeMillis());
            if(tlTime > 0) {
                try {
                    Thread.sleep(tlTime);
                } catch(InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    private void getDirection() {
        // �������� ��������� ������
        int iKeyStates = getKeyStates() | pointerState;

        // �������� ����������� ����
        iDirection = 0;

        // ���� ������ ����������� �������, �����
        if((iKeyStates&FIRE_PRESSED) > 0) pause();

        // ���� ������ ������� �����, ����������� ���� �����
        if((iKeyStates&LEFT_PRESSED) > 0) iDirection -= iAirControl;

        // ���� ������ ������� ������, ����������� ���� ������
        if((iKeyStates&RIGHT_PRESSED) > 0) iDirection += iAirControl;
    }

    public void pointerPressed(int x, int y) {
        pointerState = (x < (iWidth >> 1)) ? LEFT_PRESSED : RIGHT_PRESSED;
    }

    public void pointerDragged(int x, int y) {
        pointerPressed(x, y);
    }

    public void pointerReleased(int x, int y) {
        pointerState = 0;
    }

    private void pause() {
        // �����, ������������� ���� � ��������� ������� ����
        stopGame();
        main.pause();
    }

    private void update() {
        // ��������� ���������, ���� ������� ��������
        // �� ����������� ������������
        if(iBallVelocity <= iMaxVelocity)
            iBallVelocity += iBallAcceleration;
        // ���������, �� ������ �� ��� �� ������� ������
        if(ball.getX() < 0)
            ball.setPosition(0, ball.getY());
        if(ball.getX() + iBallDiameter > iWidth)
            ball.setPosition(iWidth - iBallDiameter, ball.getY());
        // �������� ������ ����
        ball.move(iDirection, iBallVelocity);
        fCollides = false;
        for(int iPlatform = 0; iPlatform < vPlatforms.size(); iPlatform++) {
            Platform tmp = (Platform)vPlatforms.elementAt(iPlatform);
            // ���� ���� �� ������ ������ - ������� �� ������
            if(tmp.getY() < -tmp.getHeight()) {
                vPlatforms.removeElementAt(iPlatform);
            } else if(!fCollides) {
                // ��������� ������������
                fCollides = ball.collidesWith(tmp, false);
                // �������� ��������� �����
                tmp.move(0, -iWorldSpeed);
                // ��� ��� ��������� (��� ����� �������������� ���������)
                if(!fCollides) fCollides = ball.collidesWith(tmp, false);
                if(fCollides) onCollide(tmp, iPlatform);
            } else tmp.move(0, -iWorldSpeed);
        }
        if(ball.getY() < 0 || ball.getY() + iBallDiameter > iHeight) {
            // ��� ��������� �� ������� ������
            onDie();
        }
        // ���������� ����� ���� �� ����� �� ���������
        if(!fCollides) nPoints++;
        if(tNewPlatform == 0) {
            // ������ ����� ������� ����� ���������
            // ���������� ����� ���������� ���������
            int tlNewPlatformMillis = 700 - (iWorldSpeed << 5);
            tlNewPlatformMillis +=
                Math.abs(rand.nextInt()) % (3500 - 3 * (iWorldSpeed << 7));

            int minimal = (iBallDiameter + 5) / (iWorldSpeed);
            if(tNewPlatform < minimal) tNewPlatform = minimal;

            tNewPlatform = nMillisToFrames(tlNewPlatformMillis);
            // ��������� ���������
            vPlatforms.addElement(createPlatform());
        } else {
            // ����� ��������� ������� �� �������
            tNewPlatform--;
        }
        if((tWorldSpeedInc == 0) && (iWorldSpeed < iMaxWorldSpeed)) {
            // ��������� ��������
            int tWorldSpeedIncMillis = 10000 +
                (Math.abs(rand.nextInt()) % 5000);
            tWorldSpeedInc = nMillisToFrames(tWorldSpeedIncMillis);
            iWorldSpeed++;
        } else {
            // ����� ��������� ������� �� �������
            tWorldSpeedInc--;
        }
        // ����������� ����� � ����
        tlGameTime += tlDelay;
    }
    
    void onCollide(Platform p, int index) {
		// �������� ����� ����
		iBallVelocity = 0;
		// ��������� ��� ��������� � ��������� ����������� ��������
		switch(p.getType()) {
			case 1:
				// ����� ���� �����
				onDie();
			break;
			case 2: 
				// ���� ���� �����
				if(nLives < nMaxLives) nLives++;
				p = new Platform(p.getX(), p.getY() + 10);
				vPlatforms.setElementAt(p, index);
			break;
			case 3:
				// ������ �����
				iBallVelocity = -15;
			break;
			case 4:
				// ���� �����
				nPoints += 100;
				p = new Platform(p.getX(), p.getY() + 10);
				vPlatforms.setElementAt(p, index);
			break;
		}
        // ��������� ��� �� ����������� ���� ���������
        ball.setPosition(ball.getX(), p.getY() - iBallDiameter);
    }

    // ����������, ��� ������ �������� �����
    private void onDie() {
        if(nLives == 1) {
            // ���� ��������
            fIsPlaying = false;
            main.gameover(nPoints);
            return;
        }
        // ��������� �����
        nLives--;
        // ���� ���������� ���������
        Platform tmp = null;
        for(int i = 0; i < vPlatforms.size(); i++) {
            tmp = (Platform)vPlatforms.elementAt(i);
            if(tmp.getY() > 20 && tmp.getType() == 0) break;
            else tmp = null;
        }
        
        // ���� ����� �� �������, ������ �����
        if(tmp == null) {
            tmp = new Platform(0, iWidth - 40 >> 1, iHeight - 10 >> 1);
            vPlatforms.addElement(tmp);
        }
        
        // ����� ��� �� ��� ���������
        ball.setPosition(tmp.getX() + (tmp.getWidth()-ball.getWidth() >> 1),
            tmp.getY() - ball.getHeight() - 2);
    }

    private Platform createPlatform() {
        int iXPlatform = 0, iYPlatform = iHeight - 10;
        // �������� ������������ ������� ������: ��� ������� ������ ������
        int nPixelsPositionRand = (iWidth << 1) / 3;
        // ���������� ���������� X ��� ���������
        do {
            iXPlatform = Math.abs(rand.nextInt()) % nPixelsPositionRand;
            iXPlatform += ball.getX() - (nPixelsPositionRand >> 1);
        } while((iXPlatform < 5) || (iXPlatform + 45 > iWidth));
        // ������� ����� ���������
        if(tlGameTime < 10000) return new Platform(iXPlatform, iYPlatform);
		int rnd = Math.abs(rand.nextInt()) % 10;
		switch(rnd) {
			case 0:
				// � ������
				return new Platform(2, iXPlatform, iYPlatform);
			case 1:
				// �������
				tNewPlatform = 15;
				return new Platform(1, iXPlatform, iYPlatform);
			case 2:
				// ������
				if(tlGameTime > 15000)
					return new Platform(3, iXPlatform, iYPlatform);
			break;
			case 3:
				// �� ���������
				return new Platform(4, iXPlatform, iYPlatform);
		}
		// �������
		return new Platform(iXPlatform, iYPlatform);
    }

    private void draw() {
        // ����������� ��������
        Graphics g = getGraphics();
        g.setColor(0xd6b498);
        g.fillRect(0, 0, iWidth, iHeight);
        // ���������� ���
        ball.paint(g);
        // ���������� ���������
        for(int i = 0; i < vPlatforms.size(); i++)
            ((Platform)vPlatforms.elementAt(i)).paint(g);
        // ���������� HUD
        fntDrawString("S" + Integer.toString(nPoints), 1, 1, g);
        String strT = "L" + Integer.toString(nLives);
        fntDrawString(strT, iWidth - fntStringWidth(strT) - 1, 1, g);
    }
}
