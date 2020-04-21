package tech.xuanwu.northstar.constant;

public interface ErrorHint {

	String NOT_NULL_PARAM = "入参不能为空";
	
	String NO_NEGATIVE_VALUE = "入参不能为负数";
	
	String INSUFFICIENT_BALANCE = "账户余额不足";
	
	String FAIL_SUBMIT_ORDER = "挂单失败";
	
	String FAIL_CANCEL_ORDER = "撤单失败";
	
	String UNKNOWN_ENUM_TYPE = "未知枚举类型";
	
	String FAIL_TO_SEND_MAIL = "邮件发送失败。";
	
	String EMPTY_PARAM = "参数不能为空";
}
