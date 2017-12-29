package calc;

import ral.Real;

public interface VariableProducer {
	abstract public Real getVariableValue(int id) throws MissingVariableException;
}