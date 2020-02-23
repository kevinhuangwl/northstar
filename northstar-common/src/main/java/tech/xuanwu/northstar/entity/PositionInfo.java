package tech.xuanwu.northstar.entity;

import java.io.Serializable;

import lombok.Data;
import xyz.redtorch.pb.CoreEnum.PositionDirectionEnum;
import xyz.redtorch.pb.CoreField.PositionField;

@Data
public class PositionInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -790933052545700894L;
	
	String positionId;
	String accountId;  // 账户ID
	PositionDirectionEnum positionDirection;  // 持仓方向
	int position;  // 持仓量
	int frozen;  // 冻结数量
	int ydPosition;  // 昨持仓
	int ydFrozen;  // 冻结数量
	int tdPosition;  // 今持仓
	int tdFrozen;  // 冻结数量
	double lastPrice;  // 计算盈亏使用的行情最后价格
	double price;  // 持仓均价
	double priceDiff;  // 持仓价格差
	double openPrice;  // 开仓均价
	double openPriceDiff;  // 开仓价格差
	double positionProfit;  // 持仓盈亏
	double positionProfitRatio;  // 持仓盈亏率
	double openPositionProfit;  // 开仓盈亏
	double openPositionProfitRatio;  // 开仓盈亏率
	double useMargin;  // 占用的保证金
	double exchangeMargin;  // 交易所的保证金
	double contractValue;  // 最新合约价值
    String contractSymbol;  // 合约
	String gatewayId;  // 网关ID
	
	public static PositionInfo convertFrom(PositionField pf) {
		PositionInfo info = new PositionInfo();
		info.positionId = pf.getPositionId();
		info.accountId = pf.getAccountId();
		info.contractSymbol = pf.getContract().getSymbol();
		info.contractValue = pf.getContractValue();
		info.exchangeMargin = pf.getExchangeMargin();
		info.frozen = pf.getFrozen();
		info.gatewayId = pf.getGatewayId();
		info.lastPrice = pf.getLastPrice();
		info.openPositionProfit = pf.getOpenPositionProfit();
		info.openPositionProfitRatio = pf.getOpenPositionProfitRatio();
		info.openPrice = pf.getOpenPrice();
		info.openPriceDiff = pf.getOpenPriceDiff();
		info.position = pf.getPosition();
		info.positionDirection = pf.getPositionDirection();
		info.positionProfit = pf.getPositionProfit();
		info.positionProfitRatio = pf.getPositionProfitRatio();
		info.price = pf.getPrice();
		info.priceDiff = pf.getPriceDiff();
		info.tdFrozen = pf.getTdFrozen();
		info.tdPosition = pf.getTdPosition();
		info.useMargin = pf.getUseMargin();
		info.ydFrozen = pf.getYdFrozen();
		info.ydPosition = pf.getYdPosition();
		return info;
	}
}
