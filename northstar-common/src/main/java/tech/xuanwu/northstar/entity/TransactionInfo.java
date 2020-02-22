package tech.xuanwu.northstar.entity;

import java.io.Serializable;

import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreField.TradeField;

public class TransactionInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2720492643581889360L;
	
	String accountId;  // 账户ID
	DirectionEnum direction;  // 方向
	OffsetFlagEnum offsetFlag;  // 开平
	double price;  // 价格
	int volume;  // 数量
	String tradingDay;  // 交易日
	String tradeDate;  // 成交日期
	String tradeTime;  // 成交时间(HHmmssSSS)
	long tradeTimestamp;  // 成交时间戳
    String contractSymbol;  // 合约
	String gatewayId;  // 网关ID
	
	public static TransactionInfo convertFrom(TradeField tf) {
		TransactionInfo info = new TransactionInfo();
		info.accountId = tf.getAccountId();
		info.contractSymbol = tf.getContract().getSymbol();
		info.direction = tf.getDirection();
		info.gatewayId = tf.getGatewayId();
		info.offsetFlag = tf.getOffsetFlag();
		info.price = tf.getPrice();
		info.tradeDate = tf.getTradeDate();
		info.tradeTime = tf.getTradeTime();
		info.tradeTimestamp = tf.getTradeTimestamp();
		info.tradingDay = tf.getTradingDay();
		info.volume = tf.getVolume();
		return null;
		
	}
}
