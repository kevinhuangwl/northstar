package tech.xuanwu.northstar.domain;

import org.apache.commons.codec.binary.StringUtils;

import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.exception.ContractMismatchException;
import xyz.redtorch.pb.CoreEnum.PositionDirectionEnum;
import xyz.redtorch.pb.CoreField.PositionField;
import xyz.redtorch.pb.CoreField.TickField;
import xyz.redtorch.pb.CoreField.TradeField;

/**
 * 持仓对象领域模型
 * @author kevinhuangwl
 *
 */
public class GwPosition {
	
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
	public GwPosition addPosition(TradeField tradeField) {
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
			
			p.setPosition(oPosition + nPosition);
			p.setTdPosition(tdPosition + nPosition);
			p.setPrice((cost + nCost) / (p.getPosition() * contract.getMultiplier()));
			p.setOpenPrice((openCost + nCost) / (p.getPosition() * contract.getMultiplier()));
			
			p.setExchangeMargin(p.getExchangeMargin() + p.getPosition() * p.getPrice() * contract.getMultiplier() * marginRatio);
			p.setUseMargin(p.getExchangeMargin());
		}
		
		return this;
	}
	
	/**
	 * 减仓
	 * @param tradeField
	 */
	public GwPosition reducePosition(TradeField tradeField) {
		String expectSymbol = p.getContract().getUnifiedSymbol();
		String actualSymbol = tradeField.getContract().getUnifiedSymbol();
		if(!StringUtils.equals(expectSymbol, actualSymbol)) {
			throw new ContractMismatchException(expectSymbol, actualSymbol);
		}
		
		synchronized (p) {			
			int nPosition = tradeField.getVolume();
			int oPosition = p.getPosition();
			int tdPosition = p.getTdPosition();

			p.setPosition(oPosition - nPosition);
			p.setTdPosition(tdPosition - nPosition);
			
			p.setExchangeMargin(p.getExchangeMargin() + p.getPosition() * p.getPrice() * contract.getMultiplier() * marginRatio);
			p.setUseMargin(p.getExchangeMargin());
		}
		
		
		return this;
	}
	
	/**
	 * 行情更新
	 * @param tick
	 */
	public void updateByTick(TickField tick) {
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
			
			p.setContractValue(p.getContractValue() + tick.getLastPrice() * p.getPosition() * contract.getMultiplier());
			p.setExchangeMargin(p.getExchangeMargin() + p.getPosition() * p.getPrice() * contract.getMultiplier() * marginRatio);
			p.setUseMargin(p.getExchangeMargin());
		}
	}
	
	/**
	 * 日结算
	 */
	public void proceedDailySettlement() {
		synchronized (p) {
			
		}
	}
	
	/**
	 * 获取持仓信息
	 * @return
	 */
	public PositionInfo getPositionInfo() {
		//防止对象被外部修改
		return PositionInfo.convertFrom(p.convertTo());
	}
	
	/**
	 * 获取持仓信息
	 * @return
	 */
	public PositionField getPositionField() {
		return p.convertTo();
	}
	
}
