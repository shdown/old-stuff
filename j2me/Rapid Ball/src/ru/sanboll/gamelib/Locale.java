package ru.sanboll.gamelib;
/**
  * ����� ���������� ������� ����������� (��������) ����������.
  * ���� �� �������� ������ ������ � ����� /lang � ������� �
  * ��������� UTF-8. ��� ����� - lang.??, ��� ?? - ������������
  * ������/�������, � ������� ��������� �������. ���� ������ �����
  * �� �������, ����� �������� ���� /lang/lang.xx.
  */
import java.io.*;

public class Locale {

    private static String text[];

    static {
        if(!readText(System.getProperty("microedition.language"))) {
            readText("xx");
        }
    }

    private static boolean readText(String lang) {
        InputStream is;
        text = new String[20];
        try {
            is = Class.forName("java.lang.Class").
                getResourceAsStream("/lang/lang." + lang);
            byte str[] = new byte[is.available()];
            is.read(str, 0, str.length);
            int s = 0, i = 0, e;
			boolean winStBr = false; // ������� �� ����� ������ � ����� windows
            try {
                while(true) {
                    e = s;
                    while(str[e++] != 0x0D);
					winStBr = str[e] == 0x0A;
                    if(winStBr) e--;
                    text[i++] = new String(str, s, e - s, "UTF-8");
                    s = winStBr ? e + 2 : e;
                }
            } catch(Exception ex2) {
            }
        } catch(Exception ex) {
            return false;
        }

        return true;
    }
    
    /**
     * ���������� ������ � ��������� �������.
     *
     * @param id ����� ������
     * @return ������
     */
    public static String getString(int id) {
        return text[id - 1];
    }
}