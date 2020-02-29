package tech.xuanwu.northstar.core.domain;

import java.util.EventObject;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.ErrorHint;
import tech.xuanwu.northstar.constant.RuntimeEvent;
import tech.xuanwu.northstar.core.persistence.repo.AccountRepo;
import tech.xuanwu.northstar.domain.ModifiableAccount;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.exception.NoSuchEventHandlerException;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;

@Slf4j
public class SimulateAccount extends RealAccount implements IAccount, ModifiableAccount {
	
	private RuntimeEngine rtEngine;
	

	public SimulateAccount(GatewayApi gatewayApi, RuntimeEngine rtEngine, AccountRepo accountRepo) {
		this.name = gatewayApi.getGatewayName();
		this.gatewayApi = gatewayApi;
		this.rtEngine = rtEngine;
		this.accountRepo = accountRepo;
	}

	@Override
	public void submitOrder(SubmitOrderReqField submitOrderReq) {
		log.info("模拟账户下单");
		
		try {
			rtEngine.emitEvent(RuntimeEvent.SUBMIT_ORDER_SIMULATE, new EventObject(submitOrderReq));
		} catch (NoSuchEventHandlerException e) {
			log.error("", e);
		}
	}

	@Override
	public void cancelOrder(CancelOrderReqField cancelOrderReq) {
		log.info("模拟账户撤单");
		
		try {
			rtEngine.emitEvent(RuntimeEvent.CANCEL_ORDER_SIMULATE, new EventObject(cancelOrderReq));
		} catch (NoSuchEventHandlerException e) {
			log.error("", e);
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
