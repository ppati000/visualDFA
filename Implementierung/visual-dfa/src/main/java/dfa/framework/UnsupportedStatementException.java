package dfa.framework;

import soot.Unit;

public class UnsupportedStatementException extends DFAException {

	private static final long serialVersionUID = 1L;
	
	private final String stmtName;
	private final Unit unit;
	
	public UnsupportedStatementException(String stmtName, Unit unit) {
		this.stmtName = stmtName;
		this.unit = unit;
	}
	
	public String getStatementName() {
		return stmtName;
	}
	
	public Unit getUnit() {
		return unit;
	}
	
	public String getMessage() {
		StringBuilder sb = new StringBuilder("Unsupported statement: ");
		sb.append(getStatementName()).append(" [").append(unit).append("]");
		return sb.toString();
	}
	
}
