package tech.xuanwu.northstar.core.domain;

import tech.xuanwu.northstar.domain.IStrategy;

/**
 * 策略类，本质上也是一个账户，有自己的持仓、订单记录、成交记录
 * @author kevinhuangwl
 *
 */
public final class Strategy extends RealAccount implements IStrategy{

	public Strategy(String name) {
		this.name = name;
	}
}
