package ru.sanboll.gamelib;
/**
 * � ������ ���������� �������� ������� ��������������� �����.
 */
import javax.microedition.media.*;
import java.io.*;

public class GameSound {
	
	/** ������ MIME-����� */
	private static final String mimeTypes[][] = {
		new String[] {"audio/midi", "audio/mid", "audio/x-midi", "audio/x-mid"},
		new String[] {"audio/amr", "audio/x-amr"},
		new String[] {"audio/wav", "audio/wave", "audio/x-wav", "audio/x-wave"},
		new String[] {"audio/mpeg", "audio/mp3", "audio/x-mp3", "audio/mpeg3"}
	};

    private Player player = null;
	
	private String audioMimeType = null;
    
    /** ����������� */
	public GameSound() {
	}
	
	private int defineMIME(String ext) {
		if(ext.equals("amr")) {
			return 1;
		} else if(ext.equals("wav")) {
			return 2;
		} else if(ext.equals("mp3")) {
			return 4;
		} else {
			return 0;
		}
	}
    
    /**
     * ��������� ���� �� �����. ������ ������������
	 * ������������ �� ���������� �����.
     * ���������� �������: mid (midi), wav, amr, mp3.
     *
     * @param path ���� � ��������� �����
     */
	public void loadSound(String path) {
		player = null;
		path = path.toLowerCase();
		String ext = path.substring(0, path.length() - 3);
        InputStream is = getClass().getResourceAsStream(path);
        String[] mimes = mimeTypes[defineMIME(ext)];
        try {
            for(int i = 0; i < mimes.length; i++) {
                try {
                    player = Manager.createPlayer(is, mimes[i]);
					audioMimeType = mimes[i];
                    break;
                } catch(Exception notSupportedMimeType) {
                }
            }

            player.realize();
        } catch(Exception ex) {
			player = null;
        }
    }
    
    /**
     * ����������, ����� �� �������������� �� ���� ����������
     *
     * @param cyclic ����� �� �������������� �� ���� ����������
     */
    public void setCyclic(boolean cyclic) {
        player.setLoopCount(cyclic ? -1 : 1);
    }
		
	/**
	 * ����������, ��� �� �������� ����
	 *
	 * @return true, ���� ���� ��������
	 */
	public boolean isSoundLoaded() {
		return player != null;
	}
	
	/**
	 * ���������� MIME-��� ���������������� �����
	 *
	 * @return MIME-��� (��������, "audio/amr")
	 */
	public String getMimeType() {
		return audioMimeType;
	}
    
    /** ������������� ���� */
    public void playSound() {
        try {
            player.start();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /** (���)������������� ���� */
    public void stopSound() {
        try {
            player.stop();
        } catch(Exception ex) {
        }
    }
    
    /** ����������� ���� � ������ */
    public void moveToBegin() {
        try {
            player.setMediaTime(0L);
        } catch(Exception ex) {
        }
    }
    
    /** ������������� ����, ��������� ���� � ����������� ������� */
    public void closeSound() {
        stopSound();
        try {
            player.close();
        } catch(Exception ex) {
        }
        player = null;
    }
}