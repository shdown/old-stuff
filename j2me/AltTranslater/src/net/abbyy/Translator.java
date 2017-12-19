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
package net.abbyy;

import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import net.*;
import alt.Alt;
/**
 * @author Sanboll
 */
public class Translator extends AbstractTranslator implements TagFilter {

    static String[] langs = {
        "Russian",
        "English",
        "German",
        "French",
        "Italian",
        "Chinese"//не работает
    };

    String pair;

/*
    static String[] pairs = {
        "Russian-English",
        "Russian-French",
        "German-Russian",
        "Russian-Italian"
    };
*/
    public Translator(String word, int s, int d, Alt m) {
        super(word, s, d, m);
        pair = langs[s] + '-' + langs[d];
    }

    public String translate() throws Exception {
        midlet.setState(midlet.STATE_CONNECTING);
        String url = "http://pda.lingvo.ru/Result.aspx?Word=";
        url += URICoder.encode(text);
        HttpConnection hc = HTTPUtil.open(url);
        if(hc == null)
            throw new TranslationFailedException();
        Hashtable params = new Hashtable();
        params.put("Host", "pda.lingvo.ru");
        params.put("User-Agent", "Opera/9.80");
        params.put("Content-Type", "application/x-www-form-urlencoded");
        params.put("Accept", "text/html");
        params.put("Connection", "Keep-Alive");
        params.put("set-cookie", "Pair="+pair);
        return SimpleParser.parseXML(new String(HTTPUtil.get(hc, params),
            "UTF-8"), "\n", this);
    }

    public boolean accept(String t, String w) {
        w = w.trim();
        t = t.trim().toLowerCase();
        if(w.equals("\n") || w.equals("\u2022") || w.equals("-") ||
            w.indexOf("Словари") >= 0 ||
            w.toLowerCase().indexOf("lingvo") >= 0)
            return false;
        return t.equals("span");
    }
}