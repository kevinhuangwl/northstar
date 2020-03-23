package tech.xuanwu.northstar.core.domain;

import tech.xuanwu.northstar.core.persistence.repo.AccountRepo;
import tech.xuanwu.northstar.core.persistence.repo.PositionRepo;
import tech.xuanwu.northstar.domain.IStrategy;
import tech.xuanwu.northstar.gateway.GatewayApi;

/**
 * 策略类，本质上也是一个账户，有自己的持仓、订单记录、成交记录
 * @author kevinhuangwl
 *
 */
public final class Strategy extends Account implements IStrategy{

	public Strategy(GatewayApi gatewayApi, AccountRepo accountRepo, PositionRepo positionRepo) {
		super(gatewayApi, accountRepo, positionRepo);
		// TODO Auto-generated constructor stub
	}


}
