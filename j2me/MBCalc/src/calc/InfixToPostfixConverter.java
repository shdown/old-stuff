package calc;

import java.util.Vector;
import java.util.Stack;

public class InfixToPostfixConverter {
	
	public static Vector convert(Vector infixExpression) throws InvalidExpressionException {
		Vector out = new Vector();
		Stack stack = new Stack();
		int last_id = -1;

		for(int i = 0; i < infixExpression.size(); ++i) {
			CalcToken token = (CalcToken)infixExpression.elementAt(i);
			int type = GodObject.typesById[token.id];
			switch(type) {
			case GodObject.TYPE_VALUE:
			case GodObject.TYPE_VARIABLE:
				out.addElement(token);
				break;
			case GodObject.TYPE_OP:
				int priority = GodObject.operatorPrioritiesById[token.id];
				int prec = GodObject.operatorPrecsById[token.id];
				int arity = GodObject.operatorAritiesById[token.id];
				if(arity == 1) {
					if(last_id != -1 && Util.isOperandEnding(last_id)) {
						// it's must be a postfix operator
						if(prec != GodObject.OP_PREC_LEFT)
							throw new InvalidExpressionException("non-postfix unary operator after operand");
					} else {
						// it's must be a prefix operator
						if(prec != GodObject.OP_PREC_RIGHT)
							throw new InvalidExpressionException("non-prefix unary operator after non-operand");
					}
				}
				while(!stack.isEmpty()) {
					CalcToken top = (CalcToken)stack.peek();
					int topType = GodObject.typesById[top.id];
					int topPriority = GodObject.operatorPrioritiesById[top.id];
					if(topType == GodObject.TYPE_OP &&
					   ((prec == GodObject.OP_PREC_RIGHT && priority < topPriority) ||
						(prec == GodObject.OP_PREC_LEFT && priority <= topPriority)))
					{
						out.addElement(stack.pop());
					} else {
						break;
					}
				}
				stack.push(token);
				break;
			case GodObject.TYPE_OPENING_BRACE:
				stack.push(token);
				break;
			case GodObject.TYPE_CLOSING_BRACE:
				while(true) {
					if(stack.isEmpty()) {
						throw new InvalidExpressionException("unmatched braces or something");
					}
					CalcToken top = (CalcToken)stack.pop();
					if(GodObject.typesById[top.id] == GodObject.TYPE_OPENING_BRACE) {
						break;
					}
					out.addElement(top);
				}
				break;
			default:
				throw new UnknownTokenException("unknown token: ID " + token.id);
			}
			last_id = token.id;
		}
		while(!stack.isEmpty()) {
			CalcToken token = (CalcToken)stack.pop();
			if(GodObject.typesById[token.id] != GodObject.TYPE_OP) {
				throw new InvalidExpressionException("unmatched braces");
			}
			out.addElement(token);
		}
		return out;
	}
}