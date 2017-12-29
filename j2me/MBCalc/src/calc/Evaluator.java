package calc;

import ral.Real;
import java.util.Vector;
import java.util.Stack;

public class Evaluator {
	public static Real evaluate(Vector postfixExpression, VariableProducer vp, boolean modifyValues)
		throws InvalidExpressionException, MissingVariableException
	{
		Stack stack = new Stack();
		for(int i = 0; i < postfixExpression.size(); ++i) {
			CalcToken token = (CalcToken)postfixExpression.elementAt(i);
			switch(GodObject.typesById[token.id]) {
			case GodObject.TYPE_VARIABLE:
				stack.push(new Real(vp.getVariableValue(token.id)));
				break;
			case GodObject.TYPE_VALUE:
				if(modifyValues) {
					stack.push(((CalcValueToken)token).value);
				} else {
					stack.push(new Real(((CalcValueToken)token).value));
				}
				break;
			case GodObject.TYPE_OP:
				if(stack.isEmpty()) {
					throw new InvalidExpressionException("no operands left for operator");
				}
				Real a = (Real)stack.pop(), b = null;
				switch(GodObject.operatorAritiesById[token.id]) {
				case 1:
					break;
				case 2:
					if(stack.isEmpty()) {
						throw new InvalidExpressionException("no operands left for operator");
					}
					b = a;
					a = (Real)stack.pop();
					break;
				default:
					throw new UnknownTokenException("operator arity neither 1 nor 2: ID " + token.id);
				}
				GodObject.execOperatorById(token.id, a, b);
				stack.push(a);
				break;
			default:
				throw new UnknownTokenException("unknown token: ID " + token.id);
			}
		}
		if(stack.isEmpty()) {
			throw new InvalidExpressionException("empty expression?");
		}
		Real result = (Real)stack.pop();
		if(!stack.isEmpty()) {
			throw new InvalidExpressionException("stack is not empty at the end");
		}
		return result;
	}
}