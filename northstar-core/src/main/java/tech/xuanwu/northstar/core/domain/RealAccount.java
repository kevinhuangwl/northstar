package tech.xuanwu.northstar.core.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.ErrorHint;
import tech.xuanwu.northstar.core.dao.AccountDao;
import tech.xuanwu.northstar.domain.IAccount;
import tech.xuanwu.northstar.domain.IStrategy;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.OrderInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.entity.TransactionInfo;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreEnum.OrderStatusEnum;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;

/**
 * 账户对象，每个实际账户一个实例
 * 作为聚合根，协调多个策略之间的开平仓信号，以及在资金占用率过高时，禁止策略开仓
 * @author kevinhuangwl
 *
 */
@Slf4j
public class RealAccount implements IAccount{
	
	@Autowired
	AccountDao accDao;
	
	/*账户对应的网关接口，一对一关系*/
	private GatewayApi gatewayApi;
	
	/*基本账户信息副本*/
	volatile AccountInfo accountInfo;
	
	/*策略信息*/
	protected Map<String, IStrategy> strategyMap = new HashMap<>();
	
	/*订单信息*/
	protected Map<String, OrderInfo> orderMap = new HashMap<>();
	
	/*持仓信息*/
	protected Map<String, PositionInfo> positionMap = new HashMap<>();
	
	/*成交信息*/
	protected Map<String, TransactionInfo> transactionMap = new HashMap<>();
	
	/*账户名称*/
	@Getter
	protected String name;
	
	protected String lastOrderTradeDay = "";
	
	public RealAccount(){}
	
	public RealAccount(GatewayApi gatewayApi){
		this.name = gatewayApi.getGatewayName();
		this.gatewayApi = gatewayApi;
	}

	@Override
	public void submitOrder(SubmitOrderReqField submitOrderReq) {
		log.info("账户-【{}】委托下单，{}", name, submitOrderReq);
		gatewayApi.submitOrder(submitOrderReq);
	}

	@Override
	public void cancelOrder(CancelOrderReqField cancelOrderReq) {
		log.info("账户-【{}】委托撤单，{}", name, cancelOrderReq);
		gatewayApi.cancelOrder(cancelOrderReq);
	}

	@Override
	public void updatePosition(PositionInfo position) {
		synchronized (positionMap) {
			//持仓量等于零的持仓不记录
			if(position.getPosition() > 0) {				
				positionMap.put(position.getPositionId(), position);
			}
		}
	}

	@Override
	public List<PositionInfo> getPositionInfoList() {
		synchronized (positionMap) {		
			List<PositionInfo> resultList = new ArrayList<>(positionMap.size());
			resultList.addAll(positionMap.values());
			return resultList;
		}
	}

	@Override
	public void updateOrder(OrderInfo order) {
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
		if(this.accountInfo == null) {
			this.accountInfo = account;
		}
		//若账户信息有变，则保存记录
		if(!this.accountInfo.equals(account)) {
			accDao.insert(account);
		}
	}

	@Override
	public List<IStrategy> getStrategyList() {
		synchronized (strategyMap) {
			List<IStrategy> resultList = new ArrayList<>(strategyMap.size());
			resultList.addAll(strategyMap.values());
			return resultList;
		}
	}

	@Override
	public void regStrategy(String strategyName) {
		IStrategy strategy = new Strategy(strategyName);
		synchronized (strategyMap) {
			strategyMap.put(strategyName, strategy);
		}
		
	}

	@Override
	public void unregStrategy(String strategyName) {
		synchronized (strategyMap) {
			strategyMap.remove(strategyName);
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
		//FIXME 先做简单实现
		throw new IllegalStateException("本方法未实现");
	}

	@Override
	public void disconnectGateway() {
		if(!gatewayApi.isConnected()) {
			return;
		}
		gatewayApi.disconnect();
	}

	@Override
	public double getBalance() {
		return accountInfo.getBalance();
	}

	@Override
	public double getMargin() {
		return accountInfo.getMargin();
	}
	
}
