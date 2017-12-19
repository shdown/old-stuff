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

import alt.Alt;
/**
 * @author Sanboll
 */
public abstract class AbstractTranslator implements Runnable {
    protected int sourceLanguageIndex, destLanguageIndex;
    protected String text;
    protected Alt midlet;
    
    public AbstractTranslator(String text, int sourceLanguageIndex,
                              int destLanguageIndex, Alt midlet) {
        this.text = text;
        this.sourceLanguageIndex = sourceLanguageIndex;
        this.destLanguageIndex = destLanguageIndex;
        this.midlet = midlet;
        midlet.setState(midlet.STATE_PREPARING);
        new Thread(this).start();
    }

    public void run() {
        String ans = null;
        try {
            ans = translate();
            midlet.showResult(ans);
            midlet.insertToHistory(text + ": " + ans);
            ans = null;
        } catch (Throwable th) {
            th.printStackTrace();
            midlet.showResult(midlet.ERROR + ": " + th);
        }
        text = ans = null;
    }

    abstract public String translate() throws Exception;
}
