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
package net;

import java.io.*;
import java.util.*;
import javax.microedition.io.*;
/**
 * @author Sanboll
 */
public class HTTPUtil {
    
    /**
     * „итает данные из HTTP-соединени€
     *
     * @param hc соединение
     * @param additionalProps параметры
     * @return считанные данные или <code>null</code>, если произошла ошибка.
     */
    static public byte[] get(HttpConnection hc, Hashtable additionalProps) {
        try {
            hc.setRequestMethod("GET");
            Enumeration en = additionalProps.keys();
            while(en.hasMoreElements()) {

                String key = en.nextElement().toString();
                String param = additionalProps.get(key).toString();
                hc.setRequestProperty(key, param);

                alt.Alt.addTraffic(param.length());
                alt.Alt.addTraffic(key.length());
            }
            InputStream is = hc.openInputStream();
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            for(;;) {
                int copy = is.read(buffer);
                if(copy < 0) break;
                else
                    out.write(buffer, 0, copy);
                alt.Alt.addTraffic(copy);
            }
            is.close();
            hc.close();
            return out.toByteArray();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * ¬ыполн€ет Post-запрос и читает ответ из HTTP-соединени€
     *
     * @param hc соединение
     * @param additionalProps параметры
     * @return ответ или <code>null</code>, если произошла ошибка.
     */
    static public byte[] post(HttpConnection hc, byte[] data,
         Hashtable additionalProps) {
        try {
            hc.setRequestMethod("POST");
            Enumeration en = additionalProps.keys();
            while(en.hasMoreElements()) {

                String key = en.nextElement().toString();
                String param = additionalProps.get(key).toString();
                hc.setRequestProperty(key, param);

                alt.Alt.addTraffic(key.length());
                alt.Alt.addTraffic(param.length());
            }
            OutputStream os = hc.openOutputStream();
            alt.Alt.addTraffic(data.length);
            os.write(data);
            os.close();
            InputStream is = hc.openInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[512];
            for(;;) {
                int copy = is.read(buffer);
                if(copy < 0) break;
                else
                    out.write(buffer, 0, copy);
                alt.Alt.addTraffic(copy);
            }
            is.close();
            hc.close();
            return out.toByteArray();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    static public HttpConnection open(String url) {
        alt.Alt.addTraffic(url.length());
        try {
            return (HttpConnection) Connector.open(url);
        } catch(Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

}
