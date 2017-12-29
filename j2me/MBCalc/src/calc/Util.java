package calc;

public class Util {

	public static boolean isOperandEnding(int id) {
		switch(GodObject.typesById[id]) {
		case GodObject.TYPE_CONSTANT:
		case GodObject.TYPE_DIGIT_PART:
		case GodObject.TYPE_VARIABLE:
		case GodObject.TYPE_CLOSING_BRACE:
		case GodObject.TYPE_VALUE:
			return true;
		case GodObject.TYPE_OP:
			// "x!" is an operand, but "x+" is not.
			return GodObject.operatorAritiesById[id] == 1 &&
			       GodObject.operatorPrecsById[id] == GodObject.OP_PREC_LEFT;
		default:
			return false;
		}
	}
	
	public static boolean isFunction(int id) {
		return GodObject.typesById[id] == GodObject.TYPE_OP &&
		       GodObject.operatorAritiesById[id] == 1 &&
		       GodObject.operatorPrecsById[id] == GodObject.OP_PREC_RIGHT;
	}
}