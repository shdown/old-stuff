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
/**
 * @author Sanboll
 */
public class TranslatorFactory {
    public static void launchTranslator(Alt midlet, int sourceLang,
                                        int destLang, String text) {
        switch(Options.translater) {
            case 0:
                new net.google.Translator(text, sourceLang, destLang, midlet);
            break;
            case 1:
                new net.yandex.Translator(text, sourceLang, destLang, midlet);
            break;
            case 2:
                new net.prompt.Translator(text, sourceLang, destLang, midlet);
            break;
            case 3:
                new net.bing.Translator(text, sourceLang, destLang, midlet);
            break;
            case 4:
                new net.abbyy.Translator(text, sourceLang, destLang, midlet);
            break;
        }
    }
}