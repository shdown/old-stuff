package app;

public class KeyClassifier {
/*****************************************************************************/
	public final static int
		K_UNKNOWN = 0,
		K_NUMERIC = 1,
		K_JOY_RIGHT = 2,
		K_JOY_LEFT = 3,
		K_JOY_DOWN = 4,
		K_JOY_UP = 5,
		K_JOY_FIRE = 6,
		K_MENU_RIGHT = 7,
		K_MENU_LEFT = 8;

	public static int classifyKey(int keyCode) {
		if(('0' <= keyCode && keyCode <= '9') || keyCode == '*' || keyCode == '#')
			return K_NUMERIC;
		switch(keyCode) {
		case -7:
			return K_MENU_RIGHT;
		case -6:
			return K_MENU_LEFT;
		case -5:
			return K_JOY_FIRE;
		case -4:
			return K_JOY_RIGHT;
		case -3:
			return K_JOY_LEFT;
		case -2:
			return K_JOY_DOWN;
		case -1:
			return K_JOY_UP;
		default:
			return K_UNKNOWN;
		}
	}
}