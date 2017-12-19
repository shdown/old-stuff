package ru.sanboll.gamelib;
/**
 * � ������ ���������� ������� ����������
 * �������� ������ � �������������� ���������.
 * ����������: �������� ������/������ ���������
 * �������� ����� ������� ������� ���� ��
 * ����������� �������. �� ����������� �� �����.
 */
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordEnumeration;
import java.io.*;

public class GameRMS {
    
    /**
     * ���������� �������� ���������� � ��������� � ��������� ������
     *
     * @param sRecordName ��� ���������. ������ �������� �� ASCII-��������.
     * @param value c���������� ��������
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
     * ���������� �������� ���������� �� ������� �� � ��������� ������.
     *
     * @param recordName ��� ���������
     * @return ���������� �������� ��� 0, ���� ��� �����������.
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
