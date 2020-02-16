package tech.xuanwu.northstar.core.service;

import java.util.List;

import xyz.redtorch.pb.CoreEnum.*;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.PositionField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;
import xyz.redtorch.pb.CoreField.TradeField;

/**
 * 交易服务 负责提供简单且统一的入参接口，然后统一封装 
 * 交易服务接口主要满足手工下单与程序下单提供相关的操作入口
 * 
 * @author kevinhuangwl
 *
 */
public interface TradeService {

	/**
	 * 提交委托
	 * 
	 * @param accountName     账户名称
	 * @param contractSymbol  交易合约
	 * @param price           下单价
	 * @param stopPrice       止损价
	 * @param volume             委托量
	 * @param priceType       价格类型
	 * @param direction       下单方向
	 * @param transactionType 开平方式
	 * @param hedgeType       组合投机标志
	 * @param expireType      委托单时效
	 * @param volType         成交量类型
	 * @param trigerType      触发类型
	 * @return 委托单ID
	 */
	String submitOrder(String accountName, String contractSymbol, double price, double stopPrice, int volume,
			OrderPriceTypeEnum priceType, DirectionEnum direction, OffsetFlagEnum transactionType,
			HedgeFlagEnum hedgeType, TimeConditionEnum expireType, VolumeConditionEnum volType,
			ContingentConditionEnum trigerType) throws Exception;

	/**
	 * 提交委托
	 * 
	 * @param accountName
	 * @param contractSymbol
	 * @param price
	 * @param volume
	 * @param direction
	 * @param transactionType
	 * @return
	 */
	String submitOrder(String accountName, String contractSymbol, double price, int volume, DirectionEnum direction,
			OffsetFlagEnum transactionType) throws Exception;

	/**
	 * 提交委托
	 * 
	 * @param accountName
	 * @param submitOrderReq
	 * @return
	 */
	String submitOrder(String accountName, SubmitOrderReqField submitOrderReq) throws Exception;

	/**
	 * 撤销委托
	 * @param accountName
	 * @param originOrderId
	 */
	void cancelOrder(String accountName, String originOrderId);
	
	/**
	 * 撤销委托
	 * @param accountName
	 * @param cancelOrderReq
	 * @return
	 */
	void cancelOrder(String accountName, CancelOrderReqField cancelOrderReq);
	
	/**
	 * 全部清仓
	 */
	void sellOutAllPosition(String accountName);

}
