package tech.xuanwu.northstar.core.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.ErrorHint;
import tech.xuanwu.northstar.core.persistence.repo.AccountRepo;
import tech.xuanwu.northstar.core.persistence.repo.ContractRepo;
import tech.xuanwu.northstar.core.persistence.repo.PositionRepo;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.entity.AccountConnectionInfo;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.entity.GatewayInfo;
import tech.xuanwu.northstar.entity.OrderInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.entity.TransactionInfo;
import tech.xuanwu.northstar.exception.TradeException;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreEnum.GatewayTypeEnum;
import xyz.redtorch.pb.CoreEnum.OrderStatusEnum;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;

/**
 * 账户对象，每个实际账户一个实例
 * 作为聚合根，协调多个策略之间的开平仓信号，以及在资金占用率过高时，禁止策略开仓
 * @author kevinhuangwl
 *
 */
@Slf4j
public class Account implements IAccount{
	//数据库更新间隔，避免频繁更新数据库
	private static final int DB_UPDATE_INTERVAL = 30000;
	private long lastUpdateTime = 0;
	
	@NotNull
	protected AccountRepo accountRepo;
	
	@NotNull
	protected PositionRepo positionRepo;
	
	@NotNull
	private ContractRepo contractRepo;
	
	/*账户对应的网关接口，一对一关系*/
	@NotNull
	protected GatewayApi gatewayApi;
	
	/*基本账户信息副本*/
	protected volatile AccountInfo accountInfo;
	
	/*订单信息*/
	protected Map<String, OrderInfo> orderMap = new HashMap<>();
	
	/*持仓信息*/
	protected Map<String, PositionInfo> positionMap = new HashMap<>();
	
	/*成交信息*/
	protected Map<String, TransactionInfo> transactionMap = new HashMap<>();
	
	/**/
	protected ConcurrentHashMap<String, OrderInfo> cachedOrderMap = new ConcurrentHashMap<>();
	
	
	private AccountConnectionInfo connectionInfo;
	
	/*账户ID*/
	private String accountId;
	
	protected String lastOrderTradeDay = "";
	
	public Account(GatewayApi gatewayApi, AccountRepo accountRepo, PositionRepo positionRepo, ContractRepo contractRepo){
		this.accountId = gatewayApi.getGatewaySetting().getCtpApiSetting().getUserId() + "@" + gatewayApi.getGatewayId();
		this.gatewayApi = gatewayApi;
		this.accountRepo = accountRepo;
		this.contractRepo = contractRepo;
		this.positionRepo = positionRepo;
		this.connectionInfo = new AccountConnectionInfo(this.accountId, GatewayInfo.convertFrom(gatewayApi.getGateway()));
	}

	
	@Override
	public void submitOrder(SubmitOrderReqField submitOrderReq) throws TradeException {
		log.info("账户-【{}】委托下单，{}", accountId, submitOrderReq);
		if(!gatewayApi.isConnected()) {
			throw new TradeException(accountId, ErrorHint.ACCOUNT_DISCONNECT);
		}
		gatewayApi.submitOrder(submitOrderReq);
	}

	@Override
	public void cancelOrder(CancelOrderReqField cancelOrderReq) throws TradeException {
		log.info("账户-【{}】委托撤单，{}", accountId, cancelOrderReq);
		
		String orderId = cancelOrderReq.getOrderId();
		OrderInfo order = cachedOrderMap.get(orderId);
		if(order == null || order.getOrderStatus() == OrderStatusEnum.OS_AllTraded) {
			throw new TradeException(accountId, ErrorHint.FAIL_CANCEL_ORDER);
		}
		gatewayApi.cancelOrder(cancelOrderReq);
	}

	@Override
	public void updatePosition(PositionInfo position) {
		synchronized (positionMap) {
			PositionInfo lastPosition = positionMap.put(position.getPositionId(), position);
			if(lastPosition != null && lastPosition.getPosition() == position.getPosition()) {				
				//当持仓不变时，不更新数据库
				return;
			}
			if(position.getPosition() == 0) {
				if(positionMap.containsKey(position.getPositionId())) {					
					positionMap.remove(position.getPositionId());
					positionRepo.removeById(position);
				}
				return;
			}
			positionRepo.upsertById(position);
		}
	}
	
	@Override
	public List<PositionInfo> getPositionInfoList() {
		synchronized (positionMap) {		
			List<PositionInfo> resultList = new ArrayList<>(positionMap.size());
			for(PositionInfo p : positionMap.values()) {
				//过滤持仓量等于零的持仓
				if(p.getPosition() > 0) {					
					resultList.add(p);
				}
			}
			return resultList;
		}
	}

	@Override
	public void updateOrder(OrderInfo order) {
		cachedOrderMap.put(order.getOrderId(), order);
		
		synchronized (orderMap) {
			String orderId = order.getOrderId();
			OrderStatusEnum status = order.getOrderStatus();
			String sequenceNo = order.getSequenceNo();
			
			OrderInfo lastOrder = orderMap.get(orderId);
			if(lastOrder == null) {
				orderMap.put(orderId, order);
				return;
			}
			
			if(StringUtils.isBlank(lastOrder.getOrderSysId())) {
				orderMap.put(orderId, order);
				return;
			}
			
			if(Integer.valueOf(sequenceNo) > Integer.valueOf(lastOrder.getSequenceNo())) {
				orderMap.put(orderId, order);
				return;
			}
			
			if(status != OrderStatusEnum.OS_Unknown) {
				orderMap.put(orderId, order);
			}
		}
	}

	@Override
	public void updateTransaction(TransactionInfo transaction) {
		synchronized (transactionMap) {
			transactionMap.put(transaction.getTradeId(), transaction);
		}
	}

	@Override
	public AccountInfo getAccountInfo() {
		return accountInfo;
	}

	@Override
	public void updateAccount(AccountInfo account) {
		if(account == null) {
			throw new IllegalArgumentException(ErrorHint.NOT_NULL_PARAM);
		}
		
		account.setTradingDay(gatewayApi.getTradingDay());
		
		//若账户信息有变，则保存记录
		if(!account.equals(this.accountInfo)) {
			try {
				this.accountInfo = account;
				if(System.currentTimeMillis() - lastUpdateTime > DB_UPDATE_INTERVAL) {					
					accountRepo.upsertByDate(account);
					lastUpdateTime = System.currentTimeMillis();
				}
			} catch (Exception e) {
				log.error("插入账户信息异常", e);
			}
		}
	}

	@Override
	public void sellOutAllPosition() {
		//FIXME 先做简单实现
		throw new IllegalStateException("本方法未实现");
	}

	@Override
	public List<OrderInfo> getOrderInfoList() {
		synchronized (orderMap) {
			List<OrderInfo> resultList = new ArrayList<>(orderMap.size());
			resultList.addAll(orderMap.values());
			return resultList;
		}
	}

	@Override
	public List<TransactionInfo> getTransactionInfoList() {
		synchronized (transactionMap) {
			List<TransactionInfo> resultList = new ArrayList<>(transactionMap.size());
			resultList.addAll(transactionMap.values());
			return resultList;
		}
	}

	@Override
	public void connectGateway() {
		if(gatewayApi.isConnected()) {
			log.info("账户已连线，不重复操作");
			return;
		}
		gatewayApi.connect();
		connectionInfo.onConnecting();
	}

	@Override
	public void disconnectGateway() {
		if(!gatewayApi.isConnected()) {
			log.info("账户已断开，不重复操作");
			return;
		}
		gatewayApi.disconnect();
		connectionInfo.onDisconnected();
	}

	/**
	 * 每天收盘结算操作
	 */
	protected void proceedDailySettlement() {
		try {
			accountRepo.upsertByDate(this.accountInfo);
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	@Override
	public void onConnected() {
		connectionInfo.onConnected();
		
		if(gatewayApi.getGateway().getGatewayType() == GatewayTypeEnum.GTE_Trade) {
			return;
		}
		
//		log.info("=====【{}】开始自动续订合约=====", gatewayApi.getGatewayId());
//		List<ContractInfo> contractList;
//		try {
//			//自动续订期货合约
//			contractList = contractRepo.getAllAvailableFutureContracts(gatewayApi.getGatewayId());
//		} catch (Exception ex) {
//			log.error("", ex);
//			throw new RuntimeException(ex);
//		}
//		for(ContractInfo c : contractList) {
//			ContractField contract = c.convertTo();
//			if(contract != null) {
//				gatewayApi.subscribe(contract);
//				log.info("订阅网关【{}】的合约【{}】", c.getGatewayId(), c.getSymbol());
//			}else {
//				log.warn("合约【{}】已过期", c.getSymbol());
//				contractRepo.delete(c.getGatewayId(),c.getSymbol());				
//			}
//		}		
//		
//		log.info("=====自动续订合约完成=====");
		
	}

	@Override
	public String getAccountId() {
		return accountId;
	}


	@Override
	public AccountConnectionInfo getAccountConnectionInfo() {
		return connectionInfo;
	}


	@Override
	public GatewayInfo getGatewayInfo() {
		return connectionInfo.getGatewayInfo();
	}

}
