package tech.xuanwu.northstar.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Tick implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 261629605193686115L;

	//数据源ID（格式：合约名称@交易所名称@品种类型@网关ID， 例：rb2005@SHFE@FUTURES@CTP-SimNow724）
	protected String dataSourceId;
	//交易日（格式：yyyyMMdd）
	protected String tradingDay;
	//数据所属日期（格式：yyyyMMdd）
	protected String actionDay;
	//数据所属时间（格式：HHmmssSSS）
	protected String actionTime;
	//数据所属时间戳
	protected long actionTimestamp;
	//开盘价
	protected double openPrice;
	//最高价
	protected double highPrice;
	//最低价
	protected double lowPrice;
	//涨停价
	protected double upperLimit;
	//跌停价
	protected double lowerLimit;
	//买一价
	protected double bidPrice;
	//卖一价
	protected double askPrice;
	//买一量
	protected int bidVolume;
	//卖一量
	protected int askVolume;
	//均价？？
	protected double avgPrice;
	//最新价
	protected double lastPrice;
	//每个TICK的交易量变化
	protected int volumeChange;
	//成交量
	protected long volume;
	//成交金额
	protected double turnover;
	//当前持仓量
	protected double openInterest;
	//当天持仓量变化？？
	protected int openInterestChange;
	//昨持仓
	protected double preOpenInterest;
	//昨收盘价
	protected double preClosePrice;
	//结算价
	protected double settlePrice;
	//昨结算价
	protected double preSettlePrice;
	
}
