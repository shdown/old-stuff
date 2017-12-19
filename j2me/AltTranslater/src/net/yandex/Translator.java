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
package net.yandex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import java.util.Hashtable;
import net.*;
import alt.Alt;
/**
 * @author Sanboll
 */
public class Translator extends AbstractTranslator {

    private static String[] langs = { "ru", "en", "fr", "de", "it", "ch" };

    public Translator(String t, int s, int d, Alt m) {
        super(t, s, d, m);
    }

    public String translate() throws Exception {
        String query = "http://translate.yandex.ru/tr.json/translate?lang=" + 
            langs[sourceLanguageIndex] + "-" + langs[destLanguageIndex] +
                "&srv=tr-text&id=adb7aca1-0-0&text=" +
                    URICoder.encode(text);

        midlet.setState(midlet.STATE_CONNECTING);
        
        HttpConnection hc = HTTPUtil.open(query);
        if(hc == null)
            throw new TranslationFailedException();

        midlet.setState(midlet.STATE_READ);

        Hashtable params = new Hashtable();
        
        return SimpleParser.parseJSON(new String(HTTPUtil.get(hc, params),
            "UTF-8"), "\n", "\n");
    }
}
