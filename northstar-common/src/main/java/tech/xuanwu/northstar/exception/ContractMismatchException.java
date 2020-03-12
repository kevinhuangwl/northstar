package tech.xuanwu.northstar.exception;

public class ContractMismatchException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5148898793995759706L;

	public ContractMismatchException(String expect, String actual) {
		super("合约对象不匹配。期望合约【" + expect + "】，实际合约【" + actual + "】");
	}

}
