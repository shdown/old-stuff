package ru.sanboll.rapidball.game;
/** Игровой экран */
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import java.util.*;
import ru.sanboll.rapidball.Main;

public class GameScreen extends GameCanvas implements Runnable {
    
    /** Ширина и высота экрана */
    private int iWidth, iHeight;
    
    /** Игра продолжается или нет */
    private boolean fIsPlaying;
    
    /** Объект мяча */
    private static Sprite ball;

    /** Диаметр мяча */
    private static int iBallDiameter;
    
    /** Скорость мяча вниз */
    private int iBallVelocity;

    /** Масимальная скорость мяча */
    private static int iMaxVelocity = 9;

    /** Ускорение мяча вниз */
    private static int iBallAcceleration = 1;
    
    /** Платформы, по которым он прыгает */
    private Vector vPlatforms;
    
    /** Баллы */
    private int nPoints;

    /** Жизни */
    private int nLives;
    
    /** Максимальное количество жизней */
    private static int nMaxLives = 10;
    
    /** Скорость игры */
    private int iWorldSpeed;
    
    /** Максимальная скорость игры */
    private static int iMaxWorldSpeed = 7;
    
    /** Каунтер увеличения скорости игры */
    private int tWorldSpeedInc;
    
    /** Генератор случайных чисел */
    private static Random rand = new Random(System.currentTimeMillis());
    
    /** Каунтер появление новой платформы */
    private int tNewPlatform;

    /** Управляемость мячом в воздухе */
    private static int iAirControl = 5;
    
    /** Задержка игрового цикла в мс */
    private static final long tlDelay = 24;

    private static int nFps = (int)(1000 / tlDelay);
    
    /** Время, проведённое в игре */
    private long tlGameTime;
    
    /** Напрвление движения мяча */
    private int iDirection;

    /** Ссылка на главный класс */
    private Main main;
    
    /** Изображения цифр и знаков */
    public static Image[] imgLetters;
    
    /** Касается ли мяч платформы */
    boolean fCollides;
    
    /** Строка с символами шрифта */
    public final static String strLetters = "SL1234567890";

    /** Состояние указателя (для сенсорных телефонов) */
    private int pointerState = 0;

    public GameScreen(Main main) {
        // автоматически обновлять состояние клавиш
        super(true);
        // ссылка на главный класс
        this.main = main;
        // полноэкранный режим
        setFullScreenMode(true);
        // получаем ширину и высоту экрана
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
            // засекаем время
            tlTime = System.currentTimeMillis();
            // получить состояние кнопок
            getDirection();
            // обновить состояние игры   
            update();
            // перерисовать
            draw();
            // обновить экран
            flushGraphics();
            // подождать
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
        // получить состояние клавиш
        int iKeyStates = getKeyStates() | pointerState;

        // обнуляем направление мяча
        iDirection = 0;

        // если нажата центральная клавиша, пауза
        if((iKeyStates&FIRE_PRESSED) > 0) pause();

        // если нажата клавиша влево, направление мяча влево
        if((iKeyStates&LEFT_PRESSED) > 0) iDirection -= iAirControl;

        // если нажата клавиша вправо, направление мяча вправо
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
        // пауза, останавливаем игру и оповещаем главный касс
        stopGame();
        main.pause();
    }

    private void update() {
        // применить ускорение, если текущая скорость
        // не превосходит максимальную
        if(iBallVelocity <= iMaxVelocity)
            iBallVelocity += iBallAcceleration;
        // проверить, не вылеел ли мяч за пределы экрана
        if(ball.getX() < 0)
            ball.setPosition(0, ball.getY());
        if(ball.getX() + iBallDiameter > iWidth)
            ball.setPosition(iWidth - iBallDiameter, ball.getY());
        // сдвинуть спрайт мяча
        ball.move(iDirection, iBallVelocity);
        fCollides = false;
        for(int iPlatform = 0; iPlatform < vPlatforms.size(); iPlatform++) {
            Platform tmp = (Platform)vPlatforms.elementAt(iPlatform);
            // если ушла за предел экрана - удаляем из памяти
            if(tmp.getY() < -tmp.getHeight()) {
                vPlatforms.removeElementAt(iPlatform);
            } else if(!fCollides) {
                // проверить столкновение
                fCollides = ball.collidesWith(tmp, false);
                // сдвинуть платформу вверх
                tmp.move(0, -iWorldSpeed);
                // еще раз проверить (уже новое местоположение платформы)
                if(!fCollides) fCollides = ball.collidesWith(tmp, false);
                if(fCollides) onCollide(tmp, iPlatform);
            } else tmp.move(0, -iWorldSpeed);
        }
        if(ball.getY() < 0 || ball.getY() + iBallDiameter > iHeight) {
            // мяч выкатился за пределы экрана
            onDie();
        }
        // прибавляем баллы если не лежит на платформе
        if(!fCollides) nPoints++;
        if(tNewPlatform == 0) {
            // пришло время создать новую платформу
            // определяем время следующего появления
            int tlNewPlatformMillis = 700 - (iWorldSpeed << 5);
            tlNewPlatformMillis +=
                Math.abs(rand.nextInt()) % (3500 - 3 * (iWorldSpeed << 7));

            int minimal = (iBallDiameter + 5) / (iWorldSpeed);
            if(tNewPlatform < minimal) tNewPlatform = minimal;

            tNewPlatform = nMillisToFrames(tlNewPlatformMillis);
            // добавляем платформу
            vPlatforms.addElement(createPlatform());
        } else {
            // иначе уменьшаем счетчик на единицу
            tNewPlatform--;
        }
        if((tWorldSpeedInc == 0) && (iWorldSpeed < iMaxWorldSpeed)) {
            // увеличить скорость
            int tWorldSpeedIncMillis = 10000 +
                (Math.abs(rand.nextInt()) % 5000);
            tWorldSpeedInc = nMillisToFrames(tWorldSpeedIncMillis);
            iWorldSpeed++;
        } else {
            // иначе уменьшаем счетчик на единицу
            tWorldSpeedInc--;
        }
        // увеличиваем время в игре
        tlGameTime += tlDelay;
    }
    
    void onCollide(Platform p, int index) {
		// скорость равна нулю
		iBallVelocity = 0;
		// проверить тип платформы и совершить необходимые действия
		switch(p.getType()) {
			case 1:
				// минус одна жизнь
				onDie();
			break;
			case 2: 
				// плюс одна жизнь
				if(nLives < nMaxLives) nLives++;
				p = new Platform(p.getX(), p.getY() + 10);
				vPlatforms.setElementAt(p, index);
			break;
			case 3:
				// прыжок вверх
				iBallVelocity = -15;
			break;
			case 4:
				// плюс баллы
				nPoints += 100;
				p = new Platform(p.getX(), p.getY() + 10);
				vPlatforms.setElementAt(p, index);
			break;
		}
        // поместить мяч на поверхность этой платформы
        ball.setPosition(ball.getX(), p.getY() - iBallDiameter);
    }

    // вызывается, как только потеряна жизнь
    private void onDie() {
        if(nLives == 1) {
            // игра окончена
            fIsPlaying = false;
            main.gameover(nPoints);
            return;
        }
        // уменьшить жизни
        nLives--;
        // ищем подходящую платформу
        Platform tmp = null;
        for(int i = 0; i < vPlatforms.size(); i++) {
            tmp = (Platform)vPlatforms.elementAt(i);
            if(tmp.getY() > 20 && tmp.getType() == 0) break;
            else tmp = null;
        }
        
        // если такой не нашлось, создаём новую
        if(tmp == null) {
            tmp = new Platform(0, iWidth - 40 >> 1, iHeight - 10 >> 1);
            vPlatforms.addElement(tmp);
        }
        
        // кладём мяч на эту платформу
        ball.setPosition(tmp.getX() + (tmp.getWidth()-ball.getWidth() >> 1),
            tmp.getY() - ball.getHeight() - 2);
    }

    private Platform createPlatform() {
        int iXPlatform = 0, iYPlatform = iHeight - 10;
        // смещение относительно позиции шарика: две третьих ширины экрана
        int nPixelsPositionRand = (iWidth << 1) / 3;
        // определяет координату X для платформы
        do {
            iXPlatform = Math.abs(rand.nextInt()) % nPixelsPositionRand;
            iXPlatform += ball.getX() - (nPixelsPositionRand >> 1);
        } while((iXPlatform < 5) || (iXPlatform + 45 > iWidth));
        // создать новую платформу
        if(tlGameTime < 10000) return new Platform(iXPlatform, iYPlatform);
		int rnd = Math.abs(rand.nextInt()) % 10;
		switch(rnd) {
			case 0:
				// с жизнью
				return new Platform(2, iXPlatform, iYPlatform);
			case 1:
				// красная
				tNewPlatform = 15;
				return new Platform(1, iXPlatform, iYPlatform);
			case 2:
				// желтая
				if(tlGameTime > 15000)
					return new Platform(3, iXPlatform, iYPlatform);
			break;
			case 3:
				// со звёздочкой
				return new Platform(4, iXPlatform, iYPlatform);
		}
		// обычная
		return new Platform(iXPlatform, iYPlatform);
    }

    private void draw() {
        // графический контекст
        Graphics g = getGraphics();
        g.setColor(0xd6b498);
        g.fillRect(0, 0, iWidth, iHeight);
        // нарисовать мяч
        ball.paint(g);
        // нарисовать платформы
        for(int i = 0; i < vPlatforms.size(); i++)
            ((Platform)vPlatforms.elementAt(i)).paint(g);
        // нарисовать HUD
        fntDrawString("S" + Integer.toString(nPoints), 1, 1, g);
        String strT = "L" + Integer.toString(nLives);
        fntDrawString(strT, iWidth - fntStringWidth(strT) - 1, 1, g);
    }
}
