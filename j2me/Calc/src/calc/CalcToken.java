package calc;

public class CalcToken {
	public int id, type;
	
	public CalcToken(int id) {
		this.id = id;
		this.type = GodObject.typesById[id];
	}
}
