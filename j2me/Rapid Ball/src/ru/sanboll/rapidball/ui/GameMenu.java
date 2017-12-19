package ru.sanboll.rapidball.ui;
/** Игровое меню */
import javax.microedition.lcdui.*;
import ru.sanboll.rapidball.Main;

public class GameMenu extends Canvas {

    protected Main parent;

    protected String asItems[];

    protected int iSelectedIndex;

    protected int iWidth, iHeight, iTextYPos;

    protected static int iImageOffset, iTextOffset;

    protected static Font font = Font.getFont(
        Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL
    );

    protected static Image ball;

    protected static int iItemHeight;

    static {
        try {
            ball = Image.createImage("/png/ball.png");
            iTextOffset = ball.getHeight() - font.getHeight() >> 1;
            if(iTextOffset < 0) {
                iImageOffset = -iTextOffset;
                iTextOffset = 0;
            }
            iItemHeight = Math.max(font.getHeight(), ball.getHeight());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public GameMenu(String[] asItems, Main parent) {
		this.parent = parent;
        this.asItems = asItems;
        setFullScreenMode(true);
        iWidth = getWidth();
        iHeight = getHeight();
        iTextYPos = (iHeight - iItemHeight * asItems.length) >> 1;
        iSelectedIndex = 0;
    }

    public void paint(Graphics g) {
        g.setColor(0xd6b498);
        g.fillRect(0, 0, iWidth, iHeight);
        g.setFont(font);
        g.setColor(0);
        int y = iTextYPos, x;
        for(int i = 0; i < asItems.length; i++) {
            g.drawString(asItems[i],
                x = (iWidth - font.stringWidth(asItems[i]) >> 1),
                    y + iTextOffset, 0);
            if(i == iSelectedIndex) {
                g.drawImage(ball,
                    x - ball.getWidth() - 1,
                        y + iImageOffset, 0);
                g.drawImage(ball,
                    x + font.stringWidth(asItems[i]) + 1,
                        y + iImageOffset, 0);
            }
            y += iItemHeight;
        }
    }

    public int getSelectedIndex() {
        return iSelectedIndex;
    }

    public void setSelectedIndex(int iSelectedIndex) {
        this.iSelectedIndex = iSelectedIndex;
        repaint();
    }

    public void keyPressed(int k) {
        switch(k=getGameAction(k)) {
            case UP:
                if(iSelectedIndex > 0)
                    iSelectedIndex--;
            break;
            case DOWN:
                if(iSelectedIndex != (asItems.length - 1))
                    iSelectedIndex++;
            break;
            case FIRE:
                parent.menuItemSelected(this);
            break;
        }

        repaint();
    }
}