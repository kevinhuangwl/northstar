package tech.xuanwu.northstar;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.ErrorHint;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.exception.AccountException;
import tech.xuanwu.northstar.exception.TradeException;
import xyz.redtorch.pb.CoreEnum.CurrencyEnum;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.HedgeFlagEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreEnum.PositionDirectionEnum;
import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.PositionField;
import xyz.redtorch.pb.CoreField.TickField;
import xyz.redtorch.pb.CoreField.TradeField;

/**
 * 账户对象领域模型，专门用于账户实时计算
 * @author kevinhuangwl
 *
 */
@Slf4j
public class GwAccount {
	
	//账户默认初始金额10万
	private final int DEFAULT_INIT_BALANCE = 100000;
	
	private final double DEFAULT_FEE_TICK = 1;
	
	private volatile AccountInfo accountInfo;
	
	private ConcurrentHashMap<String, GwPosition> positionMap = new ConcurrentHashMap<>();
	
	private ConcurrentHashMap<String, OrderField> orderMap = new ConcurrentHashMap<>();
	
	public GwAccount(String gatewayId, String gatewayName) {
		log.info("创建模拟账户{}", gatewayName);
		accountInfo = new AccountInfo();
		accountInfo.setAccountId(gatewayId);
		accountInfo.setGatewayId(gatewayId);
		accountInfo.setName(gatewayName);
		accountInfo.setCurrency(CurrencyEnum.CNY);
		accountInfo.setHolder("user");
		
		log.info("初始入金：{}, 可用金额：{}", depositMoney(DEFAULT_INIT_BALANCE));
	}
	
	public GwAccount(AccountInfo accountInfo, List<PositionInfo> positionInfoList) {
		//避免外部对象逃逸，因此重新构造对象副本
		this.accountInfo = AccountInfo.convertFrom(accountInfo.convertTo());
		for(PositionInfo p : positionInfoList) {
			positionMap.put(p.getPositionId(), new GwPosition(p));
		}
	}
	
	/**
	 * 获取账户信息
	 * @return
	 */
	public AccountField getAccount() {
		return accountInfo.convertTo();
	}
	
	/**
	 * 获取持仓列表
	 * @return
	 */
	public List<PositionField> getPositions() {
		List<PositionField> result = new ArrayList<>();
		for(GwPosition p : positionMap.values()) {
			result.add(p.getPosition());
		}
		return result;
	}
	
	/**
	 * 行情刷新
	 * @param tick
	 */
	public AccountField updateByTick(TickField tick) {
		
		
		return accountInfo.convertTo();
	}
	
	private void refresh() {
		synchronized (accountInfo) {
			//当前权益 = 期初权益 + 当天平仓盈亏  + 持仓盈亏 - 手续费 + 入金金额 - 出金金额 
			accountInfo.setBalance(accountInfo.getPreBalance() + accountInfo.getCloseProfit() + accountInfo.getPositionProfit() 
			- accountInfo.getCommission() + accountInfo.getDeposit() - accountInfo.getWithdraw());
			
			//可用资金 = 当前权益 - 持仓保证金 - 委托单保证金
			accountInfo.setAvailable(accountInfo.getBalance() - accountInfo.getMargin());
		}
	}
	
	/**
	 * 挂单
	 * @return
	 */
	public AccountField submitOrder(OrderField order) throws TradeException{
		boolean isOpening = order.getOffsetFlag() == OffsetFlagEnum.OF_Open;
		boolean validOrder = isOpening ? validOpeningOrder(order) : validClosingOrder(order);
		if(validOrder) {
			throw new TradeException();
		}
		
		if(isOpening) {			
			ContractField c = order.getContract();
			//冻结资金
			synchronized (accountInfo) {
				double marginRatio = order.getDirection() == DirectionEnum.D_Buy ? c.getLongMarginRatio() : c.getShortMarginRatio();
				double frozenMoney = order.getPrice() * order.getTotalVolume() * c.getMultiplier() * marginRatio;
				accountInfo.setMargin(accountInfo.getMargin() + frozenMoney);
				refresh();
			}
		}
		orderMap.put(order.getOrderId(), order);
		return accountInfo.convertTo();
	}
	
	/**
	 * 释放挂单（成交或者撤单）
	 * @param order
	 * @return
	 */
	public AccountField releaseOrder(OrderField order) {
		orderMap.remove(order.getOrderId());
		ContractField c = order.getContract();
		synchronized (accountInfo) {
			double marginRatio = order.getDirection() == DirectionEnum.D_Buy ? c.getLongMarginRatio() : c.getShortMarginRatio();
			double frozenMoney = order.getPrice() * order.getTotalVolume() * c.getMultiplier() * marginRatio;
			accountInfo.setMargin(accountInfo.getMargin() - frozenMoney);
			refresh();
		}
		return accountInfo.convertTo();
	}
	
	//校验合法开仓
	private boolean validOpeningOrder(OrderField order) {
		ContractField contract = order.getContract();
		double marginRatio = order.getDirection() == DirectionEnum.D_Buy ? contract.getLongMarginRatio() : contract.getShortMarginRatio();
		double frozenMoney = order.getPrice() * order.getTotalVolume() * contract.getMultiplier() * marginRatio;
		double transactionFee =  order.getTotalVolume() * contract.getPriceTick() * contract.getMultiplier() * DEFAULT_FEE_TICK;
		
		return accountInfo.getAvailable() >= frozenMoney + transactionFee;
	}
	
	//校验合法平仓
	private boolean validClosingOrder(OrderField order) {
		ContractField contract = order.getContract();
		for(GwPosition p : positionMap.values()) {
			PositionField pf = p.getPosition();
			if(!StringUtils.equals(contract.getContractId(), pf.getContract().getContractId())) {
				//合约不一致
				continue;
			}
			
			//方向要匹配
			if(pf.getPositionDirection()==PositionDirectionEnum.PD_Long && order.getDirection()==DirectionEnum.D_Sell 
					|| pf.getPositionDirection()==PositionDirectionEnum.PD_Short && order.getDirection()==DirectionEnum.D_Buy ) {
				
				if(order.getOffsetFlag() == OffsetFlagEnum.OF_Open) {
					return false;
				}else if(order.getOffsetFlag() == OffsetFlagEnum.OF_CloseToday) {
					return pf.getTdPosition() >= order.getTotalVolume();
				}else if(order.getOffsetFlag() == OffsetFlagEnum.OF_CloseYesterday) {
					return pf.getYdPosition() >= order.getTotalVolume();
				}
				return pf.getPosition() >= order.getTotalVolume();
			}
		}
		return false;
	}
	
	/**
	 * 成交
	 * @param order
	 * @return
	 */
	public AccountField tradeWith(TradeField trade) {
		//成交后构建合约持仓
		String unifiedSymbol = trade.getContract().getUnifiedSymbol();
		DirectionEnum direction = trade.getDirection();
		HedgeFlagEnum hedgeFlag = trade.getHedgeFlag();
		String accountId = accountInfo.getAccountId();
		String positionId = unifiedSymbol + "@" + direction.getValueDescriptor().getName() + "@" + hedgeFlag.getValueDescriptor().getName() + "@" + accountId;
		
		boolean isLongPosition = trade.getDirection()==DirectionEnum.D_Buy;
		boolean isOpen = trade.getOffsetFlag() == OffsetFlagEnum.OF_Open;
		GwPosition gp;
		if(positionMap.containsKey(positionId)) {
			gp = positionMap.get(positionId);
		}else {
			PositionInfo pi = new PositionInfo();
			pi.setPositionId(positionId);
			pi.setAccountId(trade.getAccountId());
			pi.setPositionDirection(isLongPosition ? PositionDirectionEnum.PD_Long : PositionDirectionEnum.PD_Short);
			pi.setContract(ContractInfo.convertFrom(trade.getContract()));
			pi.setGatewayId(accountInfo.getGatewayId());
			gp = new GwPosition(pi);
		}
		
		if(isOpen) {
			gp.addPosition(trade);
			openingDeal(trade);
		}else {
			gp.reducePosition(trade);
			closingDeal(gp.getPosition(), trade);
		}
		
		positionMap.put(positionId, gp);
		return accountInfo.convertTo();
	}
	
	//开仓成交
	private void openingDeal(TradeField tradeField) {
		ContractField c = tradeField.getContract();
		synchronized (accountInfo) {
			double marginRatio = tradeField.getDirection() == DirectionEnum.D_Buy ? c.getLongMarginRatio() : c.getShortMarginRatio();
			double commission = c.getPriceTick() * tradeField.getContract().getMultiplier() * DEFAULT_FEE_TICK * tradeField.getVolume();
			double margin = tradeField.getPrice() * tradeField.getVolume() * c.getMultiplier() * marginRatio;
			accountInfo.setAvailable(accountInfo.getAvailable() - margin - commission);
			accountInfo.setMargin(accountInfo.getMargin() + margin);
			accountInfo.setCommission(accountInfo.getCommission() + commission);
			log.info("合约【{}】开仓委托成交。扣减保证金：{}，扣减手续费：{}", c.getContractId(), margin, commission);
		}
	}
	//平仓成交
	private void closingDeal(PositionField positionField, TradeField tradeField) {
		ContractField c = tradeField.getContract();
		synchronized (accountInfo) {
			//平仓的方向与持仓相反，所以使用的保证金比例也是相反的
			double marginRatio = tradeField.getDirection() == DirectionEnum.D_Buy ? c.getShortMarginRatio() : c.getLongMarginRatio();
			double commission = c.getPriceTick() * c.getMultiplier() * DEFAULT_FEE_TICK * tradeField.getVolume();
			double margin = tradeField.getPrice() * tradeField.getVolume() * c.getMultiplier() * marginRatio;
			double priceDiff = positionField.getPositionDirection() == PositionDirectionEnum.PD_Long 
					? (tradeField.getPrice() - positionField.getPrice()) : (positionField.getPrice() - tradeField.getPrice());
			double closeProfit = priceDiff * c.getMultiplier() * tradeField.getVolume();
			accountInfo.setCloseProfit(accountInfo.getCloseProfit() + closeProfit);
			accountInfo.setAvailable(accountInfo.getAvailable() + margin + closeProfit - commission);
			accountInfo.setMargin(accountInfo.getMargin() - margin);
			accountInfo.setCommission(accountInfo.getCommission() + commission);
			log.info("合约【{}】平仓委托成交。释放保证金：{}，扣减手续费：{}，平仓盈亏：{}", c.getContractId(), margin, commission, closeProfit);
		}
	}
	
	/**
	 * 入金
	 * @param money
	 * @return	返回可用金额
	 */
	public AccountField depositMoney(double money) {
		if(money < 0) {
			throw new IllegalArgumentException("入金金额不能为负数");
		}
		synchronized (accountInfo) {			
			accountInfo.setDeposit(accountInfo.getDeposit() + money);
			refresh();
		}
		return accountInfo.convertTo();
	}
	
	/**
	 * 出金
	 * @param money
	 * @return	返回可用金额
	 * @throws AccountException 
	 */
	public AccountField withdrawMoney(double money) throws AccountException {
		if(money < 0) {
			throw new IllegalArgumentException("出金金额不能为负数");
		}
		if(accountInfo.getAvailable() < money) {
			throw new AccountException(accountInfo.getName(), ErrorHint.INSUFFICIENT_BALANCE);
		}
		synchronized (accountInfo) {			
			accountInfo.setWithdraw(accountInfo.getWithdraw() + money);
			refresh();
		}
		return accountInfo.convertTo();
	}
	
	/**
	 * 进行日结算
	 */
	public AccountField proceedDailySettlement() {
		
		return accountInfo.convertTo();
	}
}
