package calc;

import ral.Real;
import java.util.Vector;

public class DigitPartsMerger {
	protected static char digitPartIdToChar(int id) {
		switch(id) {
		case GodObject.ID_ZERO:
			return '0';
		case GodObject.ID_ONE:
			return '1';
		case GodObject.ID_TWO:
			return '2';
		case GodObject.ID_THREE:
			return '3';
		case GodObject.ID_FOUR:
			return '4';
		case GodObject.ID_FIVE:
			return '5';
		case GodObject.ID_SIX:
			return '6';
		case GodObject.ID_SEVEN:
			return '7';
		case GodObject.ID_EIGHT:
			return '8';
		case GodObject.ID_NINE:
			return '9';
		case GodObject.ID_PERIOD:
			return '.';
		default:
			throw new UnknownTokenException("unknown digit part: ID " + id);
		}
	}
	
	protected static boolean multiplicationSignDropped(int idA, int idB) {
		if(!Util.isOperandEnding(idA))
			return false;
		return idB == GodObject.ID_CLOSING_BRACE ||
		       GodObject.typesById[idB] == GodObject.TYPE_VARIABLE ||
			   Util.isFunction(idB);
	}
	
	public static Vector merge(int[] ids) {
		Vector out = new Vector();
		StringBuffer numberBuf = new StringBuffer();
		int last_type = 0, last_id = -1;
		for(int i = 0; i < ids.length; ++i) {
			int id = ids[i], type = GodObject.typesById[id];
			if(type != GodObject.TYPE_DIGIT_PART && last_type == GodObject.TYPE_DIGIT_PART) {
				out.addElement(new CalcValueToken(new Real(numberBuf.toString())));
				numberBuf.setLength(0);
			}
			if(last_id != -1 && multiplicationSignDropped(last_id, id)) {
				out.addElement(new CalcToken(GodObject.ID_MUL));
				last_id = GodObject.ID_MUL;
				last_type = GodObject.typesById[last_id];
			}
			switch(type) {
			case GodObject.TYPE_DIGIT_PART:
				numberBuf.append(digitPartIdToChar(id));
				break;
			case GodObject.TYPE_MINUS_OF_UNKNOWN_ARITY:
				if(last_id != -1 && Util.isOperandEnding(last_id)) {
					// it's binary
					out.addElement(new CalcToken(GodObject.ID_MINUS));
				} else {
					// it's unary
					out.addElement(new CalcToken(GodObject.ID_UNARY_MINUS));
				}
				break;
			case GodObject.TYPE_CONSTANT:
				out.addElement(new CalcValueToken(new Real(GodObject.getConstantById(id))));
				break;
			default:
				out.addElement(new CalcToken(id));
				break;
			}
			last_type = type;
			last_id = id;
		}
		if(last_type == GodObject.TYPE_DIGIT_PART) {
			out.addElement(new CalcValueToken(new Real(numberBuf.toString())));
		}
		return out;
	}
}