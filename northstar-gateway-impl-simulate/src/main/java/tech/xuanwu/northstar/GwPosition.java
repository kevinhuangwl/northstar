package tech.xuanwu.northstar;

import org.apache.commons.codec.binary.StringUtils;

import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.exception.ContractMismatchException;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreEnum.PositionDirectionEnum;
import xyz.redtorch.pb.CoreField.PositionField;
import xyz.redtorch.pb.CoreField.TickField;
import xyz.redtorch.pb.CoreField.TradeField;

/**
 * 持仓对象领域模型，专门用于持仓实时计算
 * 本类仅限于模拟网关包内使用
 * 同步锁设计为轻量级锁，因为模拟网关理论上只会不断接收tick事件线程的更新，偶尔会接收到挂单与撤单的接口线程调用
 * @author kevinhuangwl
 *
 */
class GwPosition {
	
	private PositionInfo p;
	
	private double marginRatio;
	
	private ContractInfo contract;
	
	public GwPosition(PositionInfo p) {
		this.p = PositionInfo.convertFrom(p.convertTo());
		this.contract = p.getContract();
		this.marginRatio = p.getPositionDirection() == PositionDirectionEnum.PD_Long ? contract.getLongMarginRatio() : contract.getShortMarginRatio();
	}
	
	/**
	 * 加仓
	 * @param tradeField
	 */
	public PositionField addPosition(TradeField tradeField) {
		String expectSymbol = p.getContract().getUnifiedSymbol();
		String actualSymbol = tradeField.getContract().getUnifiedSymbol();
		if(!StringUtils.equals(expectSymbol, actualSymbol)) {
			throw new ContractMismatchException(expectSymbol, actualSymbol);
		}
		
		synchronized (p) {			
			int nPosition = tradeField.getVolume();
			int oPosition = p.getPosition();
			int tdPosition = p.getTdPosition();

			// 计算成本
			double cost = p.getPrice() * p.getPosition() * contract.getMultiplier();
			double nCost = tradeField.getPrice() * tradeField.getVolume() * contract.getMultiplier();
			double openCost = p.getOpenPrice() * p.getPosition() * contract.getMultiplier();
			
			p.setTdPosition(tdPosition + nPosition);
			p.setPosition(oPosition + nPosition);
			p.setPrice((cost + nCost) / (p.getPosition() * contract.getMultiplier()));
			p.setOpenPrice((openCost + nCost) / (p.getPosition() * contract.getMultiplier()));
			p.setContractValue(tradeField.getPrice() * p.getPosition() * contract.getMultiplier());
			p.setExchangeMargin(p.getContractValue() * marginRatio);
			p.setUseMargin(p.getExchangeMargin());
		}
		
		return p.convertTo();
	}
	
	/**
	 * 减仓
	 * @param tradeField
	 */
	public PositionField reducePosition(TradeField tradeField) {
		String expectSymbol = p.getContract().getUnifiedSymbol();
		String actualSymbol = tradeField.getContract().getUnifiedSymbol();
		if(!StringUtils.equals(expectSymbol, actualSymbol)) {
			throw new ContractMismatchException(expectSymbol, actualSymbol);
		}
		
		synchronized (p) {			
			int nPosition = tradeField.getVolume();

			if(tradeField.getOffsetFlag()==OffsetFlagEnum.OF_CloseToday) {
				p.setTdPosition(p.getTdPosition() - nPosition);	
				p.setPosition(p.getPosition() - nPosition);
			}else if(tradeField.getOffsetFlag()==OffsetFlagEnum.OF_CloseYesterday) {
				p.setYdPosition(p.getYdPosition() - nPosition);
				p.setPosition(p.getPosition() - nPosition);
			}else {
				p.setPosition(p.getPosition() - nPosition);
				p.setTdPosition(p.getTdPosition() - Math.min(p.getTdPosition(), nPosition));
				p.setYdPosition(p.getPosition() - p.getTdPosition());
			}
		}
		
		return p.convertTo();
	}
	
	/**
	 * 行情更新
	 * @param tick
	 */
	public PositionField updateByTick(TickField tick) {
		String expectSymbol = p.getContract().getUnifiedSymbol();
		String actualSymbol = tick.getUnifiedSymbol();
		if(!StringUtils.equals(expectSymbol, actualSymbol)) {
			throw new ContractMismatchException(expectSymbol, actualSymbol);
		}
		
		synchronized (p) {
			p.setLastPrice(tick.getLastPrice());
			p.setPriceDiff(p.getPositionDirection()==PositionDirectionEnum.PD_Long ? (p.getLastPrice()-p.getPrice()) : (p.getPrice()-p.getLastPrice()));
			p.setOpenPriceDiff(p.getPositionDirection()==PositionDirectionEnum.PD_Long ? (p.getLastPrice()-p.getOpenPrice()) : (p.getOpenPrice()-p.getLastPrice()));
			p.setPositionProfit(p.getPosition() * p.getPriceDiff() * contract.getMultiplier());
			p.setPositionProfitRatio(p.getPositionProfit() / p.getUseMargin());
			p.setOpenPositionProfit(p.getPosition() * p.getOpenPriceDiff() * contract.getMultiplier());
			p.setOpenPositionProfitRatio(p.getOpenPositionProfit() / p.getUseMargin());
			
			p.setContractValue(tick.getLastPrice() * p.getPosition() * contract.getMultiplier());
		}
		return p.convertTo();
	}
	
	/**
	 * 日结算
	 */
	public PositionField proceedDailySettlement() {
		synchronized (p) {
			p.setYdPosition(p.getYdPosition() + p.getTdPosition());
		}
		return p.convertTo();
	}
	
	
	/**
	 * 获取持仓信息
	 * @return
	 */
	public PositionField getPosition() {
		return p.convertTo();
	}
	
}
