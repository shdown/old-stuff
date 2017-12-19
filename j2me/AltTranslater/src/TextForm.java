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
package alt;

import javax.microedition.lcdui.*;
/**
 * @author Sanboll
 */
public class TextForm extends Form {
    private StringItem str;

    public TextForm(String title) {
        super(title);
        str = new StringItem(new String(), new String());
        append(str);
    }

    public void setString(String s) {
        str.setText(s);
    }

    public String getString() {
        return str.getText();
    }
}