package calc;

import ral.Real;

public class CalcValueToken extends CalcToken {
	public Real value;
	
	public CalcValueToken(Real value) {
		super(GodObject.ID_VALUE);
		this.value = value;
	}
}
