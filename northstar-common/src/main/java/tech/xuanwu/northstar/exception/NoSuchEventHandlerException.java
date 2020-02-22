package tech.xuanwu.northstar.exception;

import tech.xuanwu.northstar.constant.RuntimeEvent;

public class NoSuchEventHandlerException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -478950872351476667L;

	public NoSuchEventHandlerException(){}
	
	public NoSuchEventHandlerException(RuntimeEvent event) {
		super("不存在事件【" + event + "】的处理器");
	}
}
