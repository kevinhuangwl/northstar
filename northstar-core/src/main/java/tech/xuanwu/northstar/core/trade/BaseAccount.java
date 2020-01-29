package tech.xuanwu.northstar.core.trade;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseAccount implements AccountOperation{

	//账户余额
	protected double balance;
	
	
}
