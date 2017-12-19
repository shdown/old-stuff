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
				return 0;
		}
	}
	
	public static Vector merge(int[] ids) throws UnknownTokenException {
		Vector out = new Vector();
		String digit = new String();
		int last_type = 0;

		for(int i = 0; i < ids.length; ++i) {
			int type = GodObject.typesById[ids[i]];

			if(type != GodObject.TYPE_PRE_DIGIT_PART && last_type == GodObject.TYPE_PRE_DIGIT_PART) {
				out.addElement(new CalcValueToken(new Real(digit))); // NumberFormatException?
				digit = new String();
			}			

			switch(type) {

			case GodObject.TYPE_PRE_DIGIT_PART:
				digit += digitPartIdToChar(ids[i]);
				break;

			case GodObject.TYPE_PRE_MINUS_OF_UNKNOWN_ARITY:
				switch(last_type) {
				case 0:
				case GodObject.TYPE_OPENING_BRACE:
				case GodObject.TYPE_PRE_MINUS_OF_UNKNOWN_ARITY:
				case GodObject.TYPE_OP:
					// it's unary
					out.addElement(new CalcToken(GodObject.ID_UNARY_MINUS));
					break;
				default:
					// it's binary
					out.addElement(new CalcToken(GodObject.ID_MINUS));
					break;
				}
				break;

			case GodObject.TYPE_PRE_CONSTANT:
				out.addElement(new CalcValueToken(new Real(GodObject.getConstantById(ids[i]))));
				break;

			default:
				out.addElement(new CalcToken(ids[i]));
				break;
			}
			
			last_type = type;
		}
		
		if(last_type == GodObject.TYPE_PRE_DIGIT_PART) {
			out.addElement(new CalcValueToken(new Real(digit))); // NumberFormatException?
		}
		
		return out;
	}
}