package tech.xuanwu.northstar.core.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.common.Conditional;
import tech.xuanwu.northstar.constant.ErrorHint;
import tech.xuanwu.northstar.core.persistence.repo.AccountRepo;
import tech.xuanwu.northstar.core.persistence.repo.PositionRepo;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.OrderInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.entity.TransactionInfo;
import tech.xuanwu.northstar.exception.TradeException;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreEnum.ConnectStatusEnum;
import xyz.redtorch.pb.CoreEnum.OrderStatusEnum;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.GatewayField;
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
	
	private ConnectStatusEnum status = ConnectStatusEnum.CS_Disconnected;
	
	
	/*账户ID*/
	private String accountId;
	
	@Getter
	@NotNull
	private String gatewayId;
	
	protected String lastOrderTradeDay = "";
	
	public Account(GatewayApi gatewayApi, AccountRepo accountRepo, PositionRepo positionRepo){
		this.accountId = gatewayApi.getGatewaySetting().getCtpApiSetting().getUserId() + "@" + gatewayApi.getGatewayId();
		this.gatewayId = gatewayApi.getGatewayId();
		this.gatewayApi = gatewayApi;
		this.accountRepo = accountRepo;
		this.positionRepo = positionRepo;
	}

	@Override
	public void submitOrder(SubmitOrderReqField submitOrderReq) throws TradeException {
		log.info("账户-【{}】委托下单，{}", accountId, submitOrderReq);
		gatewayApi.submitOrder(submitOrderReq);
		
		String originOrderId = submitOrderReq.getOriginOrderId();
		if(isTimeoutWaitingFor(()->{
			return cachedOrderMap.get(originOrderId)!=null;
		})) {
			throw new TradeException();
		}
	}

	@Override
	public void cancelOrder(CancelOrderReqField cancelOrderReq) throws TradeException {
		log.info("账户-【{}】委托撤单，{}", accountId, cancelOrderReq);
		gatewayApi.cancelOrder(cancelOrderReq);
		
		String originOrderId = cancelOrderReq.getOriginOrderId();
		OrderInfo order = cachedOrderMap.get(originOrderId);
		if(order == null && order.getOrderStatus() != OrderStatusEnum.OS_AllTraded) {
			throw new TradeException();
		}
	}

	@Override
	public void updatePosition(PositionInfo position) {
		synchronized (positionMap) {
			if(position.getPosition() == 0) {
				positionMap.remove(position.getPositionId());
				positionRepo.removeById(position);
				return;
			}
			PositionInfo lastPosition = positionMap.get(position.getPositionId());
			positionMap.put(position.getPositionId(), position);
			if(lastPosition != null && lastPosition.getPosition() == position.getPosition()) {				
				//当持仓不变时，不更新数据库
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
		cachedOrderMap.put(order.getOriginOrderId(), order);
		
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
	public List<OrderInfo> getOrderInfoList(LocalDate fromDate, LocalDate toDate) {
		//FIXME 先做简单实现
		synchronized (orderMap) {
			List<OrderInfo> resultList = new ArrayList<>(orderMap.size());
			resultList.addAll(orderMap.values());
			return resultList;
		}
	}

	@Override
	public List<TransactionInfo> getTransactionInfoList(LocalDate fromDate, LocalDate toDate) {
		//FIXME 先做简单实现
		synchronized (transactionMap) {
			List<TransactionInfo> resultList = new ArrayList<>(transactionMap.size());
			resultList.addAll(transactionMap.values());
			return resultList;
		}
	}

	@Override
	public void connectGateway() {
		if(gatewayApi.isConnected()) {
			return;
		}
		gatewayApi.connect();
		status = ConnectStatusEnum.CS_Connecting;
	}

	@Override
	public void disconnectGateway() {
		if(!gatewayApi.isConnected()) {
			return;
		}
		gatewayApi.disconnect();
		status = ConnectStatusEnum.CS_Disconnected;
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
	
	protected boolean isTimeoutWaitingFor(Conditional c) {
		int retry = 100;
		while(retry-->0) {
			if(c.expect()) {
				return false;
			}
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		return true;
	}

	@Override
	public GatewayField getGateway() {
		return gatewayApi.getGateway();
	}

	@Override
	public ConnectStatusEnum connectStatus() {
		return status;
	}

	@Override
	public void onConnected() {
		status = ConnectStatusEnum.CS_Connected;
	}

	@Override
	public String getAccountId() {
		return accountId;
	}

}
