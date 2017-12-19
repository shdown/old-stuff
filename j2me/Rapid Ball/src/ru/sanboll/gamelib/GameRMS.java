package ru.sanboll.gamelib;
/**
 * ¬ классе определены функции сохранени€
 * числовых данных в долговременном хранилище.
 * ѕримечание: операции чтени€/записи хранилища
 * занимают около секунды времени даже на
 * современных модел€х. Ќе используйте их часто.
 */
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordEnumeration;
import java.io.*;

public class GameRMS {
    
    /**
     * —охранение числовой переменной в хранилище с указанным именем
     *
     * @param sRecordName им€ хранилища. ƒолжно состо€ть из ASCII-символов.
     * @param value cохран€емое значение
     */
    public static void save(String recordName, int value) {
        try {
            RecordStore.deleteRecordStore(recordName);
        } catch(Exception ex) {
        }
        byte[] data;
        RecordStore rs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeInt(value);
            dos.flush();
            dos.close();
            data = baos.toByteArray();
            baos.close();
            rs = RecordStore.openRecordStore(recordName, true);
            rs.addRecord(data, 0, data.length);
            
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                rs.closeRecordStore();
            } catch(Exception ex) {
            }
        }
    }
    
    /**
     * »звлечение числовой переменной из хранили ща с указанным именем.
     *
     * @param recordName им€ хранилища
     * @return сохранЄнное значение или 0, если оно отсутствует.
     */
    public static int read(String recordName) {
        byte[] data = null;
        int result = 0;
        try {
            RecordStore rs = RecordStore.openRecordStore(recordName, false);
            RecordEnumeration re = rs.enumerateRecords(null, null, false);
            data = re.nextRecord();
            re.destroy();
            rs.closeRecordStore();
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            DataInputStream dis = new DataInputStream(bais);
            result = dis.readInt();
            dis.close();
            bais.close();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
