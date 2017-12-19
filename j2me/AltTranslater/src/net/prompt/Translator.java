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
package net.prompt;

import javax.microedition.io.*;
import java.util.*;
import java.io.*;
import net.*;
import alt.*;
/**
 * @author Sanboll
 */
public class Translator extends AbstractTranslator
{
    final static String[] langs = { "r","e","d","f","i","c" };

    static String lang = System.getProperty("microedition.language");

    final static String ACCOUNT_ID = "PMTAndroid";

    final static String CODE = "C3FC7A3D-FE52-4063-B11C-1565C627B7AD";

    public Translator(String text, int s, int d, Alt m) {
        super(text, s, d, m);
    }

    public String translate() throws Exception {
        Hashtable additionalProps = new Hashtable();
        additionalProps.put("Host", "www.translate.ru");
        HttpConnection hc = HTTPUtil.open(
            "http://www.translate.ru/services/Translator.asmx/Initialize?lang="+lang);
        if(hc == null)
            throw new TranslationFailedException();
        String s = new String(HTTPUtil.get(hc, additionalProps), "UTF-8");
        String reqid = SimpleParser.getValueFromXML(s, "reqId").trim();
        String error = SimpleParser.getValueFromXML(s, "errCode").trim();
        if(!error.equals("0")) {
            String dsk = SimpleParser.getValueFromXML(s, "ErrDesk").trim();
            throw new TranslationFailedException("Error code: " + error + ". Error description: " + dsk);
        }
        additionalProps.put("Content-Type", "application/x-www-form-urlencoded");
        additionalProps.put("PROMT-REQID", reqid);
        additionalProps.put("PROMT-ACCID", ACCOUNT_ID);
        String hash = md5(ACCOUNT_ID + reqid + CODE);
        String data = "dirCode=" + langs[sourceLanguageIndex] + langs[destLanguageIndex] +
            "&tmplCode=" + hash + "&sText=" +
            URICoder.encode(text) + "&format=text&lang=";
        if(lang != null)
            data += lang;
        else data += "ru";
        data += "&paramsXML=no";
        additionalProps.put("PROMT-CODE", hash);
        additionalProps.put("Content-Length", String.valueOf(data.length()));
        hc = HTTPUtil.open("http://www.translate.ru/services/Translator.asmx/Translate");
        if(hc == null)
            throw new TranslationFailedException();
        s = new String(HTTPUtil.post(hc, data.getBytes(), additionalProps), "UTF-8");
        String cdata = SimpleParser.getValueFromXML(s, "strResult");
        cdata = cdata.substring(cdata.indexOf("<![CDATA[") + 9);
        cdata = cdata.substring(0, cdata.length() - 3);
        return cdata;
    }

    public static String md5(String s) {
        return new MD5(s).asHex();
    }

}
