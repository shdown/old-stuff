package options;

import ral.Real;

public class AngleUnitConverter {
	
	protected final static Real PI_180;
	static {
		PI_180 = new Real(Real.PI);
		PI_180.div(180);
	}

	public static void toRadians(Real angle) {
		switch(OptionsManager.angleUnit.getValue()) {
		case OptionsManager.ANGLE_UNIT_DEG:
			angle.mul(PI_180);
			break;
		case OptionsManager.ANGLE_UNIT_RAD:
			break;
		default:
			throw new IllegalArgumentException("Unknown angle unit");
		}
	}
	
	public static void fromRadians(Real angle) {
		switch(OptionsManager.angleUnit.getValue()) {
		case OptionsManager.ANGLE_UNIT_DEG:
			angle.div(PI_180);
			break;
		case OptionsManager.ANGLE_UNIT_RAD:
			break;
		default:
			throw new IllegalArgumentException("Unknown angle unit");
		}		
	}
}