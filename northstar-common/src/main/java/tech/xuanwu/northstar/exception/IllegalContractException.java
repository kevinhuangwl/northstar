package tech.xuanwu.northstar.exception;

public class IllegalContractException extends Exception{
	private static final long serialVersionUID = -773008190411214663L;

	
	public IllegalContractException(){}
	
	public IllegalContractException(String message) {
		super(message);
	}
	
	public IllegalContractException(String message, Throwable t) {
		super(message, t);
	}
}
