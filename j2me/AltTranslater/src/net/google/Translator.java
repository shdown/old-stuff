/** Alt Translater - online translater for mobile devices
 *  Copyright (C) 2011 Sanboll
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.google;

import java.util.Hashtable;
import javax.microedition.io.*;
import java.io.*;
import net.*;
import alt.Alt;
/**
 * @author Sanboll
 */
public class Translator extends AbstractTranslator {

    private static String[] langs = { "ru", "en", "de", "fr", "it", "zh-CN" };

    public Translator(String t, int s, int d, Alt a) {
        super(t, s, d, a);
    }

    public String translate() throws Exception {
        String qq = new String(text.getBytes("UTF-8"));
        String query = "http://translate.google.ru/translate_a/t";
        midlet.setState(midlet.STATE_CONNECTING);
        
        HttpConnection hc = HTTPUtil.open(query);
        if(hc == null)
            throw new TranslationFailedException();
    
        Hashtable params = new Hashtable();
        params.put("Host", "translate.google.com.ua");
        params.put("User-Agent", "Opera/9.64");
        params.put("Referer", "translate.google.com.ua");
        params.put("Content-Type", "application/x-www-form-urlencoded");
        params.put("Accept", "*/*");
        params.put("Proxy-Connection", "close");
        
        midlet.setState(midlet.STATE_READ);

        byte[] data = ("sl=" + langs[sourceLanguageIndex] + "&tl=" + langs[destLanguageIndex] + "&client=t&text=" + qq).getBytes();

        if(data == null)
            throw new TranslationFailedException();
        String ans = new String(HTTPUtil.post(hc, data, params), "UTF-8");
        if(destLanguageIndex == 5)
            ans = chinese(ans);
        return parseanswer(ans);
    }

    public static String chinese(String txt) {
        StringBuffer s = new StringBuffer(txt);
        txt = null;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == 'n') s.deleteCharAt(i);
        }
        return s.toString();
    }

    private static int index;
    private static boolean end;

    public static String parseanswer(String txt) {
        index = 0;
        end = false;
        StringBuffer buffer = new StringBuffer();
        while (txt.charAt(index) != '\"') {
            index++;
        }
        index++;
        buffer.append(getBrackets(txt));
        
        while (hasNext(txt)) {
            buffer.append(' ');
            buffer.append(getBrackets(txt));
        }
        checkEnd(txt);
        while (!end) {
            if (txt.charAt(++index) == ',') break;
            checkEnd(txt);
        }
        index++;
        int brackets = 0;
        char tmp;
        checkEnd(txt);
        int num = 0;
        while (index < txt.length() && !end) {
            tmp = txt.charAt(index);
            switch (tmp) {
                case '[': ++brackets; break;
                case ']': --brackets; break;
                case '\"':
                    if (brackets == 2) {
                        index ++;
                        buffer.append("\n");
                        buffer.append(getBrackets(txt));
                        buffer.append(":\n");
                        num = 0;
                    } else if (brackets == 3) {
                        num ++;
                        index ++;
                        buffer.append(num).append(". ");
                        buffer.append(getBrackets(txt));
                        buffer.append("\n");
                    }
                    break;
            }
            index ++;
            checkEnd(txt);
        }
        return buffer.toString();
    }

    private static void checkEnd(String txt) {
        if((index < txt.length()) && (txt.charAt(index) == ','))
            if((index + 1 < txt.length()) && (txt.charAt(index + 1) == ','))
                end = true;
    }

    private static boolean hasNext(String txt) {
        while(index < txt.length()) {
            if(++index < txt.length() && ( txt.charAt(index) == ']')) {
                if(index + 1 < txt.length() && (txt.charAt(index + 1) == ']'))
                     return false;
                else if(++index < txt.length() &&
                    (txt.charAt(index) == ',')) {
                    if(++index < txt.length() &&
                        (txt.charAt(index) == '[') ){
                        if(++index < txt.length() &&
                            (txt.charAt(index) == '\"')) {
                                index ++;
                                return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private static String getBrackets(String txt) {
        StringBuffer buffer = new StringBuffer();
        char symbol;
        while(index < txt.length() && (symbol = txt.charAt(index)) != '\"') {
            if(symbol == '\\') {
                if(++index < txt.length()) {
                    switch(symbol = txt.charAt(index)) {
                        case 'n':
                            buffer.append("\n");
                            break;
                        case '\\':
                            buffer.append("\\");
                            break;
                        case 'r':
                            buffer.append("\r");
                            break;
                        case 't':
                            buffer.append("\t");
                            break;
                        case '\"':
                            buffer.append("\"");
                            break;
                        case 'b':
                            buffer.append("\b");
                            break;
                        default:
                            buffer.append((char)symbol);
                    }
                } else {
                    buffer.append(symbol);
                }
            } else {
                buffer.append((char)symbol);
            }
            index ++;
        }

        return buffer.toString();
    }
}
