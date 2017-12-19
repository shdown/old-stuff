package ru.sanboll.rapidball.game;
/** Класс платформы */
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.*;

public class Platform extends Sprite {

    /** Тип платформы */
    private int type;

    /** Массив с изображениями платформ */
    private static Image[] platformImages;

    static {
        try {
            platformImages = new Image[5];
            for(int i = 0; i < platformImages.length; i++)
                platformImages[i] = Image.createImage("/png/p" + i + ".png");
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public Platform(int type, int x, int y) {
        super(platformImages[type]);
        this.type = type;
        setPosition(x, y);
    }

    public Platform(int x, int y) {
        this(0, x, y);
    }

    public int getType() {
        return type;
    }
}