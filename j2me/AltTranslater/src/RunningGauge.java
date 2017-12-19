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
import javax.microedition.lcdui.Gauge;

public class RunningGauge extends Gauge implements Runnable {

    private boolean run = false;

    private long delay;

    Thread thread;

    public RunningGauge(String label, int size, int _delay) {
        super(label, false, size, 0);
        delay = _delay;
        if(getMaxValue() < 1 || delay < 1)
            throw new IllegalArgumentException();
    }

    final public void start() {
        setValue(0);
        run = true;
        (thread = new Thread(this)).start();
    }

    final public void stop() {
        run = false;
    }

    final public boolean isRunning() {
        return run;
    }

    public void run() {
        int direction = 1;
        try {
            while(run) {
                if(getValue() == getMaxValue())
                    direction = -1;
                else if(getValue() == 0)
                    direction = 1;
                setValue(getValue() + direction);
                thread.sleep(delay);
            }
        } catch(InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
