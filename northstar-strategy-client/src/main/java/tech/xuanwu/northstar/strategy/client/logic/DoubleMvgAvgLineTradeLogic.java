package tech.xuanwu.northstar.strategy.client.logic;

import tech.xuanwu.northstar.strategy.client.dataref.DataRef;
import tech.xuanwu.northstar.strategy.client.indicators.MovingAverageIndicator;

/**
 * 双均线交易策略
 * 
 * 多头信号：快线上穿慢线
 * 空头信号：快线下穿慢线
 * @author kevinhuangwl
 *
 */
public class DoubleMvgAvgLineTradeLogic implements TradeLogic{

	private MovingAverageIndicator fastLine;
	private MovingAverageIndicator slowLine;
	
	public DoubleMvgAvgLineTradeLogic(DataRef dataRef, DataRef.PriceType priceType, int fastLineRef, int slowLineRef) {
		fastLine = new MovingAverageIndicator(dataRef, priceType, fastLineRef);
		slowLine = new MovingAverageIndicator(dataRef, priceType, slowLineRef);
	}
	
	@Override
	public Signal getCurrentSignal() {
		return null;
	}

}
