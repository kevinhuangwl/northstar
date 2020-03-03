package tech.xuanwu.northstar.core.domain;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.ErrorHint;
import tech.xuanwu.northstar.core.persistence.repo.AccountRepo;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.domain.ModifiableAccount;
import tech.xuanwu.northstar.engine.MarketEngine;
import tech.xuanwu.northstar.entity.OrderInfo;
import tech.xuanwu.northstar.exception.TradeException;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreEnum.OrderStatusEnum;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;

@Slf4j
public class SimulateAccount extends RealAccount implements IAccount, ModifiableAccount {
	
	private MarketEngine mkEngine;
	

	public SimulateAccount(GatewayApi gatewayApi, MarketEngine mkEngine, AccountRepo accountRepo) {
		this.name = gatewayApi.getGatewayName();
		this.gatewayApi = gatewayApi;
		this.mkEngine = mkEngine;
		this.accountRepo = accountRepo;
	}

	@Override
	public void submitOrder(SubmitOrderReqField submitOrderReq) throws TradeException {
		log.info("模拟账户下单");
		
		mkEngine.submitOrder(submitOrderReq);
		
		String originOrderId = submitOrderReq.getOriginOrderId();
		if(isTimeoutWaitingFor(()->{
			return cachedOrderMap.get(originOrderId)!=null;
		})) {
			throw new TradeException();
		}
	}

	@Override
	public void cancelOrder(CancelOrderReqField cancelOrderReq) throws TradeException {
		log.info("模拟账户撤单");
		
		mkEngine.cancelOrder(cancelOrderReq);
		
		String originOrderId = cancelOrderReq.getOriginOrderId();
		OrderInfo order = cachedOrderMap.get(originOrderId);
		if(order == null && order.getOrderStatus() != OrderStatusEnum.OS_AllTraded) {
			throw new TradeException();
		}
	}


	@Override
	public double depositMoney(double money) throws Exception {
		if(money < 0){
			throw new IllegalArgumentException(ErrorHint.NO_NEGATIVE_VALUE);
		}
		double balance = 0; 
		synchronized (this) {
			balance = this.accountInfo.getBalance() + money;
			this.accountInfo.setBalance(balance);			
		}
		onAccountChange();
		return balance;
	}

	@Override
	public double withdrawMoney(double money) throws Exception {
		if(money < 0){
			throw new IllegalArgumentException(ErrorHint.NO_NEGATIVE_VALUE);
		}
		if(this.accountInfo.getBalance() < money) {
			throw new IllegalArgumentException(ErrorHint.INSUFFICIENT_BALANCE);
		}
		double balance = 0;
		synchronized (this) {
			balance = this.accountInfo.getBalance() - money;
			this.accountInfo.setBalance(balance);
		}
		onAccountChange();
		return balance;
	}
	
	//账户信息变动时保存变更
	private void onAccountChange() {
		
	}
	
}
