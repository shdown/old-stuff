/* GENERATED AUTOMATICALLY. DO NOT EDIT. */
package calc;

import ral.Real;

public class GodObject {
    public final static int
        ID_VALUE = 0,
        ID_ZERO = 1,
        ID_ONE = 2,
        ID_TWO = 3,
        ID_THREE = 4,
        ID_FOUR = 5,
        ID_FIVE = 6,
        ID_SIX = 7,
        ID_SEVEN = 8,
        ID_EIGHT = 9,
        ID_NINE = 10,
        ID_PERIOD = 11,
        ID_MINUS_OF_UNKNOWN_ARITY = 12,
        ID_UNARY_MINUS = 13,
        ID_PLUS = 14,
        ID_MINUS = 15,
        ID_MODULUS = 16,
        ID_MUL = 17,
        ID_DIV = 18,
        ID_FACT = 19,
        ID_OPENING_BRACE = 20,
        ID_CLOSING_BRACE = 21,
        ID_INPUT_BRACES = 22,
        ID_POW = 23,
        ID_SQR = 24,
        ID_SQRT = 25,
        ID_SIN = 26,
        ID_ASIN = 27,
        ID_LN = 28,
        ID_COS = 29,
        ID_ACOS = 30,
        ID_EXP = 31,
        ID_TAN = 32,
        ID_ATAN = 33,
        ID_E = 34,
        ID_COT = 35,
        ID_ACOT = 36,
        ID_PI = 37,
        ID_FLOOR = 38,
        ID_ABS = 39,
        ID_SGN = 40,
        ID_ROUND = 41,
        ID_AND = 42,
        ID_EPS = 43,
        ID_CEIL = 44,
        ID_OR = 45,
        ID_ANS = 46,
        ID_FRAC = 47,
        ID_XOR = 48,
        ID_X = 49,

        TYPE_PRE_CONSTANT = -1,
        TYPE_PRE_DIGIT_PART = -2,
        TYPE_OPENING_BRACE = -3,
        TYPE_PRE_MINUS_OF_UNKNOWN_ARITY = -4,
        TYPE_INPUT_SPECIFIC = -5,
        TYPE_VARIABLE = -6,
        TYPE_CLOSING_BRACE = -7,
        TYPE_OP = -8,
        TYPE_VALUE = -9,

        OP_PREC_RIGHT = 1,
        OP_PREC_LEFT = 2;

    public final static int typesById[] = {
        TYPE_VALUE,
        TYPE_PRE_DIGIT_PART,
        TYPE_PRE_DIGIT_PART,
        TYPE_PRE_DIGIT_PART,
        TYPE_PRE_DIGIT_PART,
        TYPE_PRE_DIGIT_PART,
        TYPE_PRE_DIGIT_PART,
        TYPE_PRE_DIGIT_PART,
        TYPE_PRE_DIGIT_PART,
        TYPE_PRE_DIGIT_PART,
        TYPE_PRE_DIGIT_PART,
        TYPE_PRE_DIGIT_PART,
        TYPE_PRE_MINUS_OF_UNKNOWN_ARITY,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OPENING_BRACE,
        TYPE_CLOSING_BRACE,
        TYPE_INPUT_SPECIFIC,
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
        TYPE_PRE_CONSTANT,
        TYPE_OP,
        TYPE_OP,
        TYPE_PRE_CONSTANT,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_OP,
        TYPE_PRE_CONSTANT,
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
        9,
        1,
        1,
        1,
        2,
        2,
        9,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        3,
        8,
        9,
        9,
        9,
        9,
        9,
        9,
        9,
        9,
        9,
        -1 /* not an operator */,
        9,
        9,
        -1 /* not an operator */,
        9,
        9,
        9,
        9,
        1,
        -1 /* not an operator */,
        9,
        1,
        -1 /* not an operator */,
        9,
        1,
        -1 /* not an operator */,
    };
    public final static int operatorAritiesById[] = {
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
        1,
        2,
        2,
        2,
        2,
        2,
        1,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
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
        -1 /* not an operator */,
        1,
        1,
        -1 /* not an operator */,
        1,
        1,
        1,
        1,
        2,
        -1 /* not an operator */,
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
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        OP_PREC_RIGHT,
        -1 /* not an operator */,
        -1 /* not an operator */,
        -1 /* not an operator */,
        OP_PREC_LEFT,
        OP_PREC_RIGHT,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        -1 /* not an operator */,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        -1 /* not an operator */,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        -1 /* not an operator */,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
        -1 /* not an operator */,
        OP_PREC_LEFT,
        OP_PREC_LEFT,
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
            a.recip(); a.atan();
            break;
        case ID_ACOS:
            a.acos();
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
            a.cos();
            break;
        case ID_COT:
            a.tan(); a.recip();
            break;
        case ID_FACT:
            a.fact();
            break;
        case ID_ATAN:
            a.atan();
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
            a.tan();
            break;
        case ID_SIN:
            a.sin();
            break;
        case ID_XOR:
            a.xor(b);
            break;
        case ID_PLUS:
            a.add(b);
            break;
        case ID_ASIN:
            a.asin();
            break;
        default:
            throw new UnknownTokenException("unknown operator: ID " + id);
        }
    }
}
