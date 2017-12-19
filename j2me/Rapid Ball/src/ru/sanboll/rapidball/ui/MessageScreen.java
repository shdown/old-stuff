package ru.sanboll.rapidball.ui;
/** ����� ��� ������ ��������� � ��������� ����� */
import ru.sanboll.gamelib.Locale;
import ru.sanboll.rapidball.Main;

public class MessageScreen extends GameMenu {

	private static String[] createItems(String[] asText, int nEmptyStr) {
        String[] asTmp = new String[asText.length + nEmptyStr + 1];
        System.arraycopy(asText, 0, asTmp, 0, asText.length);
        for(int i = 0; i < nEmptyStr; i++)
            asTmp[asText.length + i] = new String();
        asTmp[asTmp.length - 1] = Locale.getString(13); // �����
        return asTmp;
    }

    public MessageScreen(String[] asText, int nEmptyStr, Main main) {
        super(createItems(asText, nEmptyStr), main);
        iSelectedIndex = asItems.length - 1;
    }
    
    /** �������������� ����� ������� �� ������� */
    public void keyPressed(int key) {
        if(getGameAction(key) == super.FIRE) {
            parent.messageSkipped(this);
        }
    }
}