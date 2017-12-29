package options;

import java.io.*;
import javax.microedition.rms.*;

public class OptionsManager {

	public final static int ANGLE_UNIT_RAD = 1;
	public final static int ANGLE_UNIT_DEG = 2;
	public static IntOption angleUnit = new IntOption(ANGLE_UNIT_RAD) {
		public void setValue(int value) {
			if(value != ANGLE_UNIT_RAD && value != ANGLE_UNIT_DEG)
				throw new IllegalArgumentException("unknown angleUnit " + value);
			this.value = value;
		}
	};
	public static IntOption plotterMaxHintChars = new IntOption(6) {
		public void setValue(int value) {
			if(value < 1)
				throw new IllegalArgumentException("plotterMaxHintChars < 1");
			if(value > 30)
				throw new IllegalArgumentException("plotterMaxHintChars > 30");
			this.value = value;
		}
	};
	public static BooleanOption autoInsertBraces = new BooleanOption(false);
	
	protected final static Option[] options = {
		angleUnit,
		plotterMaxHintChars,
		autoInsertBraces,
	};

/*****************************************************************************/	

	protected final static String RS_NAME = "options";
	
	public static void clear() throws OptionsException {
		try {
			RecordStore.deleteRecordStore(RS_NAME);
		} catch(RecordStoreNotFoundException unimportant) {
		} catch(RecordStoreException ex) {
			throw new OptionsException(ex.toString());
		}
	}
	
	public static void load() throws OptionsException {
		byte data[];
		
		RecordStore recordStore = null;
		try {
			recordStore = RecordStore.openRecordStore(RS_NAME, true);
			if(recordStore.getNumRecords() == 0)
				return;
			data = recordStore.getRecord(1);
		} catch(RecordStoreException ex) {
			throw new OptionsException(ex.toString());
		} finally {
			try {
				if(recordStore != null)
					recordStore.closeRecordStore();
			} catch(RecordStoreException unimportant) {
			}
		}
		
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		DataInputStream dis = new DataInputStream(bis);
		try {
			for(int i = 0; i < options.length; ++i) {
				options[i].read(dis);
			}
		} catch(IOException ex) {
			throw new OptionsException(ex.toString());
		} catch(IllegalArgumentException ex) {
			throw ex;
		} finally {
			try {
				bis.close();
				dis.close();
			} catch(IOException unimportant) {
			}
		}
	}
	
	public static void save() throws OptionsException {		
		byte data[];
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		
		try {
			for(int i = 0; i < options.length; ++i) {
				options[i].write(dos);
			}
			dos.flush();
			data = bos.toByteArray();
		} catch(IOException ex) {
			throw new OptionsException(ex.toString());
		} finally {
			try {
				bos.close();
				dos.close();
			} catch(IOException unimportant) {
			}
		}
		
		clear();
		RecordStore recordStore = null;
		try {
			recordStore = RecordStore.openRecordStore(RS_NAME, true);
			recordStore.addRecord(data, 0, data.length);
		} catch(RecordStoreException ex) {
			throw new OptionsException(ex.toString());
		} finally {
			try {
				if(recordStore != null)
					recordStore.closeRecordStore();
			} catch(RecordStoreException unimportant) {
			}
		}
	}
}