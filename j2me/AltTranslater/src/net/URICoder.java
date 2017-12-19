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
public class URICoder
{
    private final static int BUFFER_SIZE = 100;
    private static String unReserved = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.!~*\'() ";
    private static char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    private static byte[] hexValues = new byte['g'];
    private static boolean[] isReserved = new boolean[127];
    private static char[] charBuffer = new char[BUFFER_SIZE];
    private static boolean bufferLocked = false;
    static
    {
        for(int index = 0; index < isReserved.length; index ++){isReserved[index] = true;}
        for(int index = 0; index < unReserved.length(); index ++){isReserved[unReserved.charAt(index)] = false;}

        for(int index = 0; index < hexValues.length; ){hexValues[index ++] = -1;}
        for(int index = '0'; index <= '9'; index ++){hexValues[index] = (byte)(index - '0');}
        for(int index = 'A'; index <= 'F'; index ++){hexValues[index] = (byte)(index + 10 - 'A');}
        for(int index = 'a'; index <= 'f'; index ++){hexValues[index] = (byte)(index + 10 - 'a');}
    }

    public static String encode(String sequence) {
        int slength = sequence.length();
        int length = 0;
        for(int index = 0; index < slength; ) {
            int symbol = sequence.charAt(index ++);
            if ((symbol >= 0x0000) && (symbol <= 0x007F)) {
                if(isReserved[symbol]){length += 3;}
                else {length ++;}
            }
            else if (symbol >= 0x0800){length += 9;}
            else {length += 6;}
        }
        StringBuffer result = new StringBuffer(length);
        char[] array;
        int aindex = 0;
        synchronized(charBuffer) {
            array = bufferLocked ? new char[BUFFER_SIZE] : charBuffer;
            bufferLocked = array == charBuffer;
        }
        for(int index = 0; index < slength; index ++) {
            int symbol = sequence.charAt(index);
            if ((symbol >= 0x0000) && (symbol <= 0x007F)) {
                if(isReserved[symbol]) {
                    array[aindex ++] = '%';
                    array[aindex ++] = hexDigits[symbol >> 4];
                    array[aindex ++] = hexDigits[symbol & 0xf];
                }
                else {array[aindex ++] = symbol == ' ' ? '+' : (char)symbol;}
            }
            else if (symbol >= 0x0800) {
                array[aindex ++] = '%';
                array[aindex ++] = hexDigits[(0xe0 | ((symbol >> 12) & 0x0f)) >> 4];
                array[aindex ++] = hexDigits[(0xe0 | ((symbol >> 12) & 0x0f)) & 0xf];
                array[aindex ++] = '%';
                array[aindex ++] = hexDigits[(0x80 | ((symbol >> 6) & 0x3f)) >> 4];
                array[aindex ++] = hexDigits[(0x80 | ((symbol >> 6) & 0x3f)) & 0xf];
                array[aindex ++] = '%';
                array[aindex ++] = hexDigits[(0x80 | (symbol & 0x3f)) >> 4];
                array[aindex ++] = hexDigits[(0x80 | (symbol & 0x3f)) & 0xf];
            } else {
                array[aindex ++] = '%';
                array[aindex ++] = hexDigits[(0xc0 | ((symbol >> 6) & 0x1f)) >> 4];
                array[aindex ++] = hexDigits[(0xc0 | ((symbol >> 6) & 0x1f)) & 0xf];
                array[aindex ++] = '%';
                array[aindex ++] = hexDigits[(0x80 | (symbol & 0x3f)) >> 4];
                array[aindex ++] = hexDigits[(0x80 | (symbol & 0x3f)) & 0xf];
            }
            if(aindex > BUFFER_SIZE - 10) {
                result.append(array, 0, aindex);
                aindex = 0;
            }
        }
        result.append(array, 0, aindex);
        if(array == charBuffer){bufferLocked = false;}
        return result.toString();
    }
}
