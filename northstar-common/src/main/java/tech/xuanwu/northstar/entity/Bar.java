package tech.xuanwu.northstar.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import xyz.redtorch.pb.CoreField.BarField;

@Getter
@Setter
public class Bar implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4712266690585974569L;
	
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
	//最新价
	protected double lastPrice;
	//交易量变化？
	protected int volumeChange;
	//成交量？？
	protected long volume;
	//成交金额
	protected double turnover;
	//持仓量？？
	protected double openInterest;
	//持仓量变化？？
	protected int openInterestChange;
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
	//收盘价
	protected double closePrice;
	
}
