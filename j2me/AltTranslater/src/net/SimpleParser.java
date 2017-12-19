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
/**
 * @author Sanboll
 */
public class SimpleParser {

    public static boolean isXMLBreakChar(char ch) {
        return " \n\t\r>".indexOf(ch) >= 0;
    }

    public static String parseXML(String xml, String delimitor,
                                  TagFilter filter) {
        String source = new String(), tmp = new String(), tag = new String();
        int enclosure = 0;
        boolean str = false;
        for(int i = 0; i < xml.length(); i++) {
            char ch = xml.charAt(i);
            if(ch == '"') {
                    str = !str;
            } else if(!str) {
                if(ch == '<') {
                    enclosure++;
                        if(tmp.length() != 0 && tag.length() != 0)
                            if(filter.accept(tag, tmp)) {
                                source += tmp;
                                source += delimitor;
                            }
                    tmp = new String();
                    tag = new String();
                    while(!isXMLBreakChar(ch = xml.charAt(++i))) tag += ch;
                    if(tag.startsWith("/")) tag = tag.substring(1);
                    i--;
                } else if(ch == '>') enclosure--;
                else if(enclosure == 0) tmp += ch;
            }
        }
        return source;
    }

    public static String getValueFromXML(String xml, String vtag) {
        int i = xml.indexOf("<" + vtag + ">");
        if(i < 0) return null;
        i += 2 + vtag.length();
        xml = xml.substring(i);    
        i = xml.indexOf("</" + vtag + ">");
        if(i < 0) return null;
        return xml.substring(0, i);
    }

    public static String parseJSON (String json, String objectDelimitor,
                                    String nameValueDelimitor) {
        String source = new String();
        boolean str = false;
        char ch;
        for(int i = 0; i < json.length(); i++) {
            if((ch = json.charAt(i)) == '"') str = !str;
            else if(str) source += ch;
            else if(ch == ':') source += nameValueDelimitor;
            else if(ch == ',') source += objectDelimitor;
        }
        return source;
    }
}
