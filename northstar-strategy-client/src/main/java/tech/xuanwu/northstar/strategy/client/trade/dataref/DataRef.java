package tech.xuanwu.northstar.strategy.client.trade.dataref;

import java.util.List;

import tech.xuanwu.northstar.strategy.client.trade.indicator.Indicator;

public interface DataRef<T> {

	/**
	 * 更新数据
	 * @param tick
	 */
	void updateData(T data);
	
	
	/**
	 * 获取数据
	 * @return
	 */
	List<T> getDataRef();
	
	/**
	 * 向数据源加入指标，监听数据变化
	 * @param indicator
	 */
	void addIndicator(Indicator indicator);
	
	
	enum PriceType{
		/**
		 * 最高价
		 */
		HIGH,
		/**
		 * 最低价
		 */
		LOW,
		/**
		 * 开盘价
		 */
		OPEN,
		/**
		 * 收盘价
		 */
		CLOSE;
	}
}
