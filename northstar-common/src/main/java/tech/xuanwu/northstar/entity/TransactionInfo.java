package tech.xuanwu.northstar.entity;

import java.io.Serializable;

import lombok.Data;
import xyz.redtorch.pb.CoreEnum.DirectionEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreField.TradeField;

@Data
public class TransactionInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2720492643581889360L;
	
	String tradeId;  // 成交ID,通常是<网关ID@定单编号@方向@成交编号>，加入方向是因为部分交易所发生违规自成交后,成交ID相同
	String adapterTradeId;  // 适配器层成交ID
	String adapterOrderId;  // 适配器层定单ID
	String originOrderId;  // 原始定单ID
	String orderId;  // 定单ID,通常是<网关ID@定单ID>
	String orderLocalId; // 本地报单编号
	String brokerOrderSeq; //经纪公司报单编号
	String orderSysId; // 报单编号
	String settlementId; // 结算编号
	String sequenceNo; // 序列号
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
		info.tradeId = tf.getTradeId();
		info.adapterTradeId = tf.getAdapterTradeId();
		info.adapterOrderId = tf.getAdapterOrderId();
		info.originOrderId = tf.getOriginOrderId();
		info.orderId = tf.getOrderId();
		info.orderLocalId = tf.getOrderLocalId();
		info.brokerOrderSeq = tf.getBrokerOrderSeq();
		info.orderSysId = tf.getOrderSysId();
		info.settlementId = tf.getSettlementId();
		info.sequenceNo = tf.getSequenceNo();
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
		return info;
		
	}
}
