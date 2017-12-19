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
package net.bing;

import java.util.*;
import javax.microedition.io.HttpConnection;
import net.*;
import alt.Alt;
/**
 * @author Sanboll
 */
public class Translator extends AbstractTranslator {

    private static String[] langs = { "ru", "en", "de", "fr", "it", "zh-CHS"};

    private static String appid = "037C394ED1EA70440C3B5E07FA0A6A837DCE47A9";

    public Translator(String text, int s, int d, Alt m) {
        super(text, s, d, m);
    }

    public String translate() throws Exception {
        String query = "http://api.microsofttranslator.com/V2/Ajax.svc/Translate?";
        query += "appId=";
        query += appid;
        query += "&from=";
        query += langs[sourceLanguageIndex];
        query += "&to=";
        query += langs[destLanguageIndex];
        query += "&text=";
        query += URICoder.encode(text);
        midlet.setState(midlet.STATE_CONNECTING);
        
        HttpConnection hc = HTTPUtil.open(query);
        if(hc == null)
            throw new TranslationFailedException();
        midlet.setState(midlet.STATE_READ);

        return SimpleParser.parseJSON(new String(HTTPUtil.get(hc, new Hashtable()), "UTF-8"), "\n", "\n");
    }
}
