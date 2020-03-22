package tech.xuanwu.northstar;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.CommonConstant;
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
	
	private ConcurrentHashMap<String, GwPosition> longPositionMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, GwPosition> shortPositionMap = new ConcurrentHashMap<>();
	
	private ConcurrentHashMap<String, OrderField> orderMap = new ConcurrentHashMap<>();
	
	private ConcurrentHashMap<String, TickField> tickMap = new ConcurrentHashMap<>();
	
	public GwAccount(String gatewayId, String gatewayName) {
		log.info("创建模拟账户{}", gatewayName);
		accountInfo = new AccountInfo();
		accountInfo.setAccountId(gatewayId);
		accountInfo.setGatewayId(gatewayId);
		accountInfo.setCode(CommonConstant.SIM_TAG);
		accountInfo.setName(gatewayName);
		accountInfo.setCurrency(CurrencyEnum.CNY);
		accountInfo.setHolder("user");
		
		log.info("初始入金：{}, 可用金额：{}", depositMoney(DEFAULT_INIT_BALANCE));
	}
	
	public GwAccount(AccountInfo accountInfo, List<PositionInfo> positionInfoList) {
		//避免外部对象逃逸，因此重新构造对象副本
		this.accountInfo = AccountInfo.convertFrom(accountInfo.convertTo());
		for(PositionInfo p : positionInfoList) {
			if(p.getPositionDirection() == PositionDirectionEnum.PD_Long || p.getPositionDirection() == PositionDirectionEnum.PD_Net && p.getPosition()>0) {
				longPositionMap.put(p.getContract().getUnifiedSymbol(), new GwPosition(p));				
			}else if(p.getPositionDirection() == PositionDirectionEnum.PD_Short || p.getPositionDirection() == PositionDirectionEnum.PD_Net && p.getPosition()<0) {
				shortPositionMap.put(p.getContract().getUnifiedSymbol(), new GwPosition(p));
			}
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
		for(GwPosition p : longPositionMap.values()) {
			result.add(p.getPosition());
		}
		for(GwPosition p : shortPositionMap.values()) {
			result.add(p.getPosition());
		}
		return result;
	}
	
	/**
	 * 行情刷新
	 * @param tick
	 */
	public AccountField updateByTick(TickField tick) {
		String unifiedSymbol = tick.getUnifiedSymbol();
		GwPosition lp = longPositionMap.get(unifiedSymbol);
		double deltaProfit = 0;
		if(lp != null) {
			PositionField p0 = lp.getPosition();
			PositionField p = lp.updateByTick(tick);
			deltaProfit += p.getPositionProfit() - p0.getPositionProfit();
		}
		GwPosition sp = shortPositionMap.get(unifiedSymbol);
		if(sp != null) {
			PositionField p0 = sp.updateByTick(tick);
			PositionField p = sp.updateByTick(tick);
			deltaProfit += p.getPositionProfit() - p0.getPositionProfit();
		}
		tickMap.put(tick.getUnifiedSymbol(), tick);
		synchronized (accountInfo) {			
			accountInfo.setPositionProfit(accountInfo.getPositionProfit() + deltaProfit);
			refresh();
		}
		
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
		if(!validOrder) {
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
		}else {
			//冻结仓位
			String unifiedSymbol = order.getContract().getUnifiedSymbol();
			GwPosition p = order.getDirection() == DirectionEnum.D_Buy ? shortPositionMap.get(unifiedSymbol) : longPositionMap.get(unifiedSymbol);
			p.frozenPosition(order);
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
		boolean isOpening = order.getOffsetFlag() == OffsetFlagEnum.OF_Open;
		orderMap.remove(order.getOrderId());
		ContractField c = order.getContract();
		synchronized (accountInfo) {
			double marginRatio = order.getDirection() == DirectionEnum.D_Buy ? c.getLongMarginRatio() : c.getShortMarginRatio();
			double frozenMoney = order.getPrice() * order.getTotalVolume() * c.getMultiplier() * marginRatio;
			if(isOpening) {				
				//解冻资金
				accountInfo.setMargin(accountInfo.getMargin() - frozenMoney);
			}else {
				//解冻仓位
				String unifiedSymbol = order.getContract().getUnifiedSymbol();
				GwPosition p = order.getDirection() == DirectionEnum.D_Buy ? shortPositionMap.get(unifiedSymbol) : longPositionMap.get(unifiedSymbol);
				p.unfrozenPosition(order);
			}
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
		String unifiedSymbol = contract.getUnifiedSymbol();
		GwPosition p = order.getDirection() == DirectionEnum.D_Buy ? shortPositionMap.get(unifiedSymbol) : longPositionMap.get(unifiedSymbol);
		if(p == null) {
			return false;
		}
		PositionField pf = p.getPosition();
		if(order.getOffsetFlag() == OffsetFlagEnum.OF_CloseToday) {
			return pf.getTdPosition() - pf.getTdFrozen() >= order.getTotalVolume();
		}else if(order.getOffsetFlag() == OffsetFlagEnum.OF_CloseYesterday) {
			return pf.getYdPosition() - pf.getYdFrozen() >= order.getTotalVolume();
		}
		return pf.getPosition() - pf.getFrozen() >= order.getTotalVolume();
		
	}
	
	/**
	 * 成交
	 * @param order
	 * @return
	 */
	public AccountField tradeWith(TradeField trade) throws TradeException {
		//成交后构建合约持仓
		String unifiedSymbol = trade.getContract().getUnifiedSymbol();
		
		boolean isOpen = trade.getOffsetFlag() == OffsetFlagEnum.OF_Open;
		boolean isClose = trade.getOffsetFlag() != OffsetFlagEnum.OF_Unkonwn && !isOpen;
		boolean affectLongPosition = trade.getDirection()==DirectionEnum.D_Buy && isOpen || trade.getDirection()==DirectionEnum.D_Sell && isClose;
		boolean affectShortPosition = trade.getDirection()==DirectionEnum.D_Sell && isOpen || trade.getDirection()==DirectionEnum.D_Buy && isClose;
		
		
		if(affectLongPosition && affectShortPosition || !affectLongPosition && !affectShortPosition) {
			throw new TradeException(accountInfo.getName(), "状态异常");
		}
		
		GwPosition gp = affectLongPosition ? longPositionMap.get(unifiedSymbol) : shortPositionMap.get(unifiedSymbol);
		if(gp == null && isOpen) {
			boolean isLongPosition = trade.getDirection()==DirectionEnum.D_Buy;
			String positionId = unifiedSymbol + "@" + trade.getDirection().getValueDescriptor().getName() + "@" + accountInfo.getAccountId();
			PositionInfo pi = new PositionInfo();
			pi.setPositionId(positionId);
			pi.setAccountId(trade.getAccountId());
			pi.setPositionDirection(isLongPosition ? PositionDirectionEnum.PD_Long : PositionDirectionEnum.PD_Short);
			pi.setContract(ContractInfo.convertFrom(trade.getContract()));
			pi.setGatewayId(accountInfo.getGatewayId());
			gp = new GwPosition(pi);
			
			if(isLongPosition) {				
				longPositionMap.put(unifiedSymbol, gp);
			}else {
				shortPositionMap.put(unifiedSymbol, gp);
			}
		}
		
		ContractField c = trade.getContract();
		double commission = c.getPriceTick() * trade.getContract().getMultiplier() * DEFAULT_FEE_TICK * trade.getVolume();
		
		if(isOpen) {
			gp.addPosition(trade);
			openingDeal(commission, gp.getUseMargin());
			log.info("合约【{}】开仓委托成交。扣减保证金：{}，扣减手续费：{}", c.getContractId(), gp.getUseMargin(), commission);
		}else {
			
			PositionField positionField = gp.getPosition();
			double marginBefore = gp.getUseMargin();
			//平仓的方向与持仓相反，所以使用的保证金比例也是相反的
			double dir = positionField.getPositionDirection() == PositionDirectionEnum.PD_Long ? 1 : -1;
			double closeProfit = dir * (trade.getPrice() - positionField.getPrice()) * c.getMultiplier() * trade.getVolume();
			
			gp.reducePosition(trade);
			
			double marginDelta = gp.getUseMargin() - marginBefore;
			closingDeal(commission, marginDelta, closeProfit);
			log.info("合约【{}】平仓委托成交。释放保证金：{}，扣减手续费：{}，平仓盈亏：{}", c.getContractId(), Math.abs(marginDelta), commission, closeProfit);
		}
		
		return accountInfo.convertTo();
	}
	
	//开仓成交
	private void openingDeal(double commission, double useMargin) {
		synchronized (accountInfo) {
			accountInfo.setMargin(accountInfo.getMargin() + useMargin);
			accountInfo.setCommission(accountInfo.getCommission() + commission);
			refresh();
		}
	}
	//平仓成交
	private void closingDeal(double commission, double marginDelta, double closeProfit) {
		synchronized (accountInfo) {
			accountInfo.setCloseProfit(accountInfo.getCloseProfit() + closeProfit);
			accountInfo.setMargin(accountInfo.getMargin() + marginDelta);
			accountInfo.setCommission(accountInfo.getCommission() + commission);
			refresh();
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
		//撤销全部挂单
		for(Entry<String, OrderField> e : orderMap.entrySet()) {
			OrderField order = e.getValue();
			releaseOrder(order);
		}
		
		double settleMargin = 0;
		List<GwPosition> totalPosition = new ArrayList<>(longPositionMap.size() + shortPositionMap.size());
		totalPosition.addAll(longPositionMap.values());
		totalPosition.addAll(shortPositionMap.values());
		for(GwPosition p : totalPosition) {
			String unifiedSymbol = p.getPosition().getContract().getUnifiedSymbol();
			TickField tick = tickMap.get(unifiedSymbol);
			PositionField positionField = p.proceedDailySettlement(tick.getSettlePrice());
			settleMargin += positionField.getUseMargin();
		}
		
		synchronized (accountInfo) {
			//重新计算保证金
			accountInfo.setMargin(settleMargin);
			
			//刷新可用资金
			accountInfo.setAvailable(accountInfo.getBalance() - accountInfo.getMargin());
			
			//转化权益
			accountInfo.setPreBalance(accountInfo.getBalance() - accountInfo.getPositionProfit());
			accountInfo.setTradingDay(LocalDate.parse(accountInfo.getTradingDay()).plusDays(1).format(CommonConstant.D_FORMAT_INT_FORMATTER));
			
			//当天计算项复位
			accountInfo.setDeposit(0);
			accountInfo.setWithdraw(0);
			accountInfo.setCommission(0);
			accountInfo.setCloseProfit(0);
		}
		
		return accountInfo.convertTo();
	}
}
