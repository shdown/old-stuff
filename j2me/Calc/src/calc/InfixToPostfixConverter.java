package calc;

import java.util.Vector;
import java.util.Stack;

public class InfixToPostfixConverter {
	
	public static Vector convert(Vector infixExpression) throws InvalidExpressionException {
		Vector out = new Vector();
		Stack stack = new Stack();
		
		int last_type = 0;
		
		for(int i = 0; i < infixExpression.size(); ++i) {
			CalcToken token = (CalcToken)infixExpression.elementAt(i);
			switch(token.type) {
				case GodObject.TYPE_VALUE:
				case GodObject.TYPE_VARIABLE:
					out.addElement(token);
					break;
				
				case GodObject.TYPE_OP:
					int priority = GodObject.operatorPrioritiesById[token.id];
					int prec = GodObject.operatorPrecsById[token.id];
					int arity = GodObject.operatorAritiesById[token.id];
					if(arity == 1) {
						switch(last_type) {
						case 0:
						case GodObject.TYPE_OPENING_BRACE:
						case GodObject.TYPE_PRE_MINUS_OF_UNKNOWN_ARITY:
						case GodObject.TYPE_OP:
							// it's must be a prefix (left-sided) operator
							if(prec != GodObject.OP_PREC_LEFT)
								throw new InvalidExpressionException("non-prefix unary operator after non-operand");
							break;
						default:
							// it's must be a postfix (right-sided) operator
							if(prec != GodObject.OP_PREC_RIGHT)
								throw new InvalidExpressionException("non-postfix unary operator after operand");
							break;
						}
					}
					while(!stack.isEmpty()) {
						CalcToken top = (CalcToken)stack.peek();
						int topPriority = GodObject.operatorPrioritiesById[top.id];
						if(top.type == GodObject.TYPE_OP &&
						   arity == GodObject.operatorAritiesById[top.id] &&
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
						if(top.type == GodObject.TYPE_OPENING_BRACE) {
							break;
						}
						out.addElement(top);
					}
					break;
				
				default:
					throw new UnknownTokenException("unknown token: ID " + token.id);
			}
			last_type = token.type;
		}
		
		while(!stack.isEmpty()) {
			CalcToken token = (CalcToken)stack.pop();
			if(token.type != GodObject.TYPE_OP) {
				throw new InvalidExpressionException("unmatched braces");
			}
			out.addElement(token);
		}
		
		return out;
	}
}
