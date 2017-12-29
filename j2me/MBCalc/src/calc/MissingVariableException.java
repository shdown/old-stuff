package calc;

public class MissingVariableException extends Exception {
	public int id;
	
	public MissingVariableException(int id, String message) {
		super(message);
		this.id = id;
	}
}