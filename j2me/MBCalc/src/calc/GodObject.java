/* GENERATED AUTOMATICALLY. DO NOT EDIT. */
package calc;

import ral.Real;
import options.AngleUnitConverter;

public class GodObject {
    public final static int
        ID_INPUT_BRACES = 0,
        ID_VALUE = 1,
        ID_UNARY_MINUS = 2,
        ID_MINUS = 3,
        ID_ZERO = 4,
        ID_ONE = 5,
        ID_TWO = 6,
        ID_THREE = 7,
        ID_FOUR = 8,
        ID_FIVE = 9,
        ID_SIX = 10,
        ID_SEVEN = 11,
        ID_EIGHT = 12,
        ID_NINE = 13,
        ID_PERIOD = 14,
        ID_MINUS_OF_UNKNOWN_ARITY = 15,
        ID_E = 16,
        ID_PI = 17,
        ID_EPS = 18,
        ID_OPENING_BRACE = 19,
        ID_CLOSING_BRACE = 20,
        ID_PLUS = 21,
        ID_MODULUS = 22,
        ID_MUL = 23,
        ID_DIV = 24,
        ID_FACT = 25,
        ID_POW = 26,
        ID_SQR = 27,
        ID_SQRT = 28,
        ID_SIN = 29,
        ID_ASIN = 30,
        ID_LN = 31,
        ID_COS = 32,
        ID_ACOS = 33,
        ID_EXP = 34,
        ID_TAN = 35,
        ID_ATAN = 36,
        ID_COT = 37,
        ID_ACOT = 38,
        ID_FLOOR = 39,
        ID_ABS = 40,
        ID_SGN = 41,
        ID_ROUND = 42,
        ID_AND = 43,
        ID_CEIL = 44,
        ID_OR = 45,
        ID_ANS = 46,
        ID_FRAC = 47,
        ID_XOR = 48,
        ID_X = 49,

        TYPE_OPENING_BRACE = -1,
        TYPE_DIGIT_PART = -2,
        TYPE_INPUT_SPECIFIC = -3,
        TYPE_VARIABLE = -4,
        TYPE_CONSTANT = -5,
        TYPE_CLOSING_BRACE = -6,
        TYPE_MINUS_OF_UNKNOWN_ARITY = -7,
        TYPE_OP = -8,
        TYPE_VALUE = -9,

        OP_PREC_RIGHT = 1,
        OP_PREC_LEFT = 2;

    public final static int typesById[] = {
        TYPE_INPUT_SPECIFIC,
        TYPE_VALUE,
        TYPE_OP,
        TYPE_OP,
        TYPE_DIGIT_PART,
        TYPE_DIGIT_PART,
        TYPE_DIGIT_PART,
        TYPE_DIGIT_PART,
        TYPE_DIGIT_PART,
        TYPE_DIGIT_PART,
        TYPE_DIGIT_PART,
        TYPE_DIGIT_PART,
        TYPE_DIGIT_PART,
        TYPE_DIGIT_PART,
        TYPE_DIGIT_PART,
        TYPE_MINUS_OF_UNKNOWN_ARITY,
        TYPE_CONSTANT,
        TYPE_CONSTANT,
        TYPE_CONSTANT,
        TYPE_OPENING_BRACE,
        TYPE_CLOSING_BRACE,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_VARIABLE,
        TYPE_OP,
        TYPE_OP,
        TYPE_VARIABLE,
    };
    public final static int operatorPrioritiesById[] = {
        -1 /* not an operator */,
        -1 /* not an operator */,
        89,
        10,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        10,
        20,
        20,
        20,
        42,
        30,
        89,
        90,
        90,
        90,
        90,
        90,
        90,
        90,
        90,
        90,
        90,
        90,
        90,
        90,
        90,
        90,
        9,
        90,
        7,
        -1 /* not an operator */,
        90,
        8,
        -1 /* not an operator */,
    };
    public final static int operatorAritiesById[] = {
        -1 /* not an operator */,
        -1 /* not an operator */,
        1,
        2,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        2,
        2,
        2,
        2,
        1,
        2,
        1,
        1,
        1,
        1,
        1,
        1,
        1,
        1,
        1,
        1,
        1,
        1,
        1,
        1,
        1,
        1,
        2,
        1,
        2,
        -1 /* not an operator */,
        1,
        2,
        -1 /* not an operator */,
    };
    public final static int operatorPrecsById[] = {
        -1 /* not an operator */,
        -1 /* not an operator */,
        OP_PREC_RIGHT,
        OP_PREC_LEFT,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        OP_PREC_RIGHT,
        OP_PREC_LEFT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        -1 /* not an operator */,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        -1 /* not an operator */,
    };

    public static Real getConstantById(int id) {
        switch(id) {
        case ID_E:
            return Real.E;
        case ID_EPS:
            return Real.MIN;
        case ID_PI:
            return Real.PI;
        default:
            throw new UnknownTokenException("unknown constant: ID " + id);
        }
    }

    public static void execOperatorById(int id, Real a, Real b) {
        switch(id) {
        case ID_POW:
            a.pow(b);
            break;
        case ID_MUL:
            a.mul(b);
            break;
        case ID_FLOOR:
            a.floor();
            break;
        case ID_ACOT:
            a.recip(); a.atan(); AngleUnitConverter.fromRadians(a);
            break;
        case ID_ACOS:
            a.acos(); AngleUnitConverter.fromRadians(a);
            break;
        case ID_AND:
            a.and(b);
            break;
        case ID_SGN:
            if(a.isFinite() && !a.isZero()) a.assign(a.isNegative() ? -1 : 1);
            break;
        case ID_ABS:
            a.abs();
            break;
        case ID_UNARY_MINUS:
            a.neg();
            break;
        case ID_EXP:
            a.exp();
            break;
        case ID_COS:
            AngleUnitConverter.toRadians(a); a.cos();
            break;
        case ID_COT:
            AngleUnitConverter.toRadians(a); a.tan(); a.recip();
            break;
        case ID_FACT:
            a.fact();
            break;
        case ID_ATAN:
            a.atan(); AngleUnitConverter.fromRadians(a);
            break;
        case ID_SQR:
            a.sqr();
            break;
        case ID_CEIL:
            a.ceil();
            break;
        case ID_ROUND:
            a.round();
            break;
        case ID_DIV:
            a.div(b);
            break;
        case ID_SQRT:
            a.sqrt();
            break;
        case ID_LN:
            a.ln();
            break;
        case ID_MODULUS:
            a.mod(b);
            break;
        case ID_FRAC:
            a.frac();
            break;
        case ID_MINUS:
            a.sub(b);
            break;
        case ID_OR:
            a.or(b);
            break;
        case ID_TAN:
            AngleUnitConverter.toRadians(a); a.tan();
            break;
        case ID_SIN:
            AngleUnitConverter.toRadians(a); a.sin();
            break;
        case ID_XOR:
            a.xor(b);
            break;
        case ID_PLUS:
            a.add(b);
            break;
        case ID_ASIN:
            a.asin(); AngleUnitConverter.fromRadians(a);
            break;
        default:
            throw new UnknownTokenException("unknown operator: ID " + id);
        }
    }
}
