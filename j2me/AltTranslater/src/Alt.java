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

import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;
/**
 * @version 1.0
 * @author Sanboll
 *
 * Main class
 */
public class Alt extends MIDlet implements CommandListener {

    /** Главное меню */
    List menu;

    /** Направление перевода */
    List from, to;

    /** Ввод текста */
    TextBox input;
    
    /** Состояние перевода */
    TextForm progress;

    /** Просмотр результата */
    TextForm result;

    /** Спосок переводов (история) */
    List history;
    
    /** Просмотр трафика */
    TextForm trafficForm;
    
    /** Форма "О программе" */
    TextForm about;
    
    /** Просмотр элемента из истории */
    TextForm element;

    /** Редактирование результата */
    TextBox resultEd;

    /** Настройки */
    Form options;

    /** Команды */
    Command back, ok, edit;

    /** Бегающий индиктор */
    RunningGauge progressGauge;

    /** Выбор переводчика */
    ChoiceGroup translater;

    /** Выбор языков */
    ChoiceGroup activeLangs;

    // Строки
    final static public String STATE_PREPARING = "Подготовка";
    final static public String STATE_CONNECTING = "Подключение";
    final static public String STATE_READ = "Передача данных";
    final static public String ERROR = "Ошибка";
    
    private DisplayStack stack;
    
    static public long traffic = 0;

    static public void addTraffic(int t) {
        traffic += t;
    }

    public Alt() {
        /* Init all UI elements */
        menu = new List("Alt Translator", List.IMPLICIT, new String[] {
            "Перевод", "Настройки", "Трафик", "История",
                "О программе", "Выход"
         }, null);
        menu.addCommand(List.SELECT_COMMAND);
        history = new List("История", List.IMPLICIT);
        history.addCommand(List.SELECT_COMMAND);
        history.addCommand(back = new Command("Назад", Command.BACK, 2));
        options = new Form("Настройки");
        translater = new ChoiceGroup("Переводчик", ChoiceGroup.EXCLUSIVE,
           new String[] {"Google", "Яндекс", "PROMT", "Bing", "ABBYY Lingvo"},
        null);
        ok = new Command("Готово", Command.ITEM, 0);
        translater.setSelectedIndex(Options.translater, true);
        options.addCommand(ok);
        activeLangs = new ChoiceGroup("Отображаетмые языки",
            ChoiceGroup.MULTIPLE, new String[] {
                "Русский", "Английский", "Немецкий", "Французский",
                "Итальянский", "Китайский"
            },
        null);
        options.append(translater);
        options.append(activeLangs);

        boolean arr[] = new boolean[activeLangs.size()];
        for(int i = 0; i < arr.length; i++) {
            arr[i] = (Options.activeLanguages & (1 << i)) != 0;
        }
        activeLangs.setSelectedFlags(arr);
        from = new List("Исходный язык", List.IMPLICIT);
        from.addCommand(List.SELECT_COMMAND);
        from.addCommand(back);
        from.setCommandListener(this);
        to = new List("Конечный язык", List.IMPLICIT);
        to.addCommand(ok);
        to.addCommand(back);
        to.setCommandListener(this);
        input = new TextBox("Текст", new String(), 1024, TextField.ANY);
        input.addCommand(ok);
        input.addCommand(back);
        trafficForm = new TextForm("Трафик");
        trafficForm.addCommand(back);
        about = new TextForm("О программе");
        about.setString("Alt Translator 1.0 by Sanboll.\nAlt Translator - "+
            "online-переводчик текстов, использующий сервисы Google "+
            "translate (translate.google.ru), Яндекс.Перевод (translate"+
            ".yandex.ru), PROMT (m.translate.ru), MS Translator "+
            "(microsofttranslator.com) и ABBYY Lingvo (pda.lingvo.ru)"+
            ".\nЗаходите на сайт автора sanboll54.narod2.ru");
        about.addCommand(back);
        result = new TextForm("Результат перевода");
        result.addCommand(ok);
        result.addCommand(edit = new Command("Ред.", Command.BACK, 0));
        resultEd = new TextBox("Результат", "", 1024, TextField.ANY);
        resultEd.addCommand(back);
        element = new TextForm("История");
        element.addCommand(back);
        progress = new TextForm("Состояние");
        progressGauge = new RunningGauge("", 10, 350);
        progress.append(progressGauge);
        Display d = Display.getDisplay(this);
        stack = new DisplayStack(d);
        stack.push(menu);
        menu.setCommandListener(this);
        input.setCommandListener(this);
        trafficForm.setCommandListener(this);
        options.setCommandListener(this);
        resultEd.setCommandListener(this);
        about.setCommandListener(this);
        history.setCommandListener(this);
        result.setCommandListener(this);
        element.setCommandListener(this);
    }

    public void insertToHistory(String s) {
        history.insert(0, s, null);
    }

    public void startApp() {
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean b) {
    }

    private short getActiveLanguages() throws Exception {
        short v = 0;
        boolean arr[] = new boolean[activeLangs.size()];
        if(activeLangs.getSelectedFlags(arr) < 2) throw new Exception();
        for(int i = 0; i < arr.length; i++)
            if(arr[i]) v |= (short)(1 << i);
        return v;
    }

    public void commandAction(Command c, Displayable d) {
        if(c == List.SELECT_COMMAND) {
            if(d == menu) {
                switch(menu.getSelectedIndex()) {
                    case 0:
                        prepareDirectionLists();
                        stack.push(from);
                    break;
                    case 1:
                        stack.push(options);
                    break;
                    case 2:
                        showTraffic();
                    break;
                    case 3:
                        stack.push(history);
                    break;
                    case 4:
                        stack.push(about);
                    break;
                    case 5:
                        notifyDestroyed();
                    break;
                }
            } else if(d == from) {
                String s = from.getString(from.getSelectedIndex());
                for(int i = 0; i < to.size(); i++) {
                    if(to.getString(i).equals(s)) {
                        to.delete(i);
                        break;
                    }
                }
                if(to.size() == 1)
                    stack.push(input);
                else
                    stack.push(to);
            } else if(d == to) {
                stack.push(input);
            } else if(d == history) {
                if(history.size() == 0) return;
                element.setString(history.getString(
                    history.getSelectedIndex()));
                stack.push(element);
            }
        } else if(d == resultEd) {
            resultEd.setString(new String());
            /* show main menu */
            stack.home();
        } else if(c == back) {
            /* return to previous displayable */
            stack.back();
            if(stack.current() == from)
                prepareDirectionLists();
        } else if(c == ok) {
            if(d == options) {
                /* save options */
                try {
                    Options.changeOptions(
                        (byte)translater.getSelectedIndex(),
                        getActiveLanguages());
                        /* and show main menu */
                        stack.back();
                } catch(Exception ex) {
                    Display.getDisplay(this).setCurrent(new Alert(
                        "Alt Translater", "Должно быть выбрано как минимум "+
                            "два языка!", null, null), options);
                }
            } else if(d == result) {
                /* show main menu */
                stack.home();
            } else if(d == input) {
                /* translate */
                String text = input.getString();
                if(text.length() == 0)
                    return;
                input.setString(new String());
                int f = getLangCode(from.getString(from.getSelectedIndex())),
                    t = getLangCode(to.getString(to.getSelectedIndex()));
                TranslatorFactory.launchTranslator(this, f, t, text);
                stack.push(progress);
                progressGauge.start();
            }
        } else if(c == edit) {
            resultEd.setString(result.getString());
            stack.push(resultEd);
            result.setString(new String());
        }
    }

    private int getLangCode(String langName) {
        for(int i = 0; i < activeLangs.size(); i++) {
            if(langName.equals(activeLangs.getString(i))) return i;
        }
        return -1;
    }

    private void prepareDirectionLists() {
        while(from.size() != 0) from.delete(0);
        while(to.size() != 0) to.delete(0);
        for(int i = 0; i < activeLangs.size(); i++) {
            if((Options.activeLanguages&(1<<i))!=0) {
                from.append(activeLangs.getString(i), null);
                to.append(activeLangs.getString(i), null);
            }
        }
    }

    private void showTraffic() {
        stack.push(trafficForm);
        String s = traffic < 1024 ? traffic + " Б" :
            (traffic>>10) + " Кб " + (traffic&1023) + " Б";
        trafficForm.setString(s);
    }

    public void setState(String s) {
        progress.setString(s);
    }

    public void showResult(String s) {
        progressGauge.stop();
        result.setString(s);
        stack.push(result);
    }

}
