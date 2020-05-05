package tech.xuanwu.northstar;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.ErrorHint;
import tech.xuanwu.northstar.constant.NoticeCode;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.NoticeInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.exception.TradeException;
import tech.xuanwu.northstar.gateway.GatewayApi;
import tech.xuanwu.northstar.gateway.SimulatedGateway;
import xyz.redtorch.pb.CoreEnum.CommonStatusEnum;
import xyz.redtorch.pb.CoreEnum.OffsetFlagEnum;
import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.CancelOrderReqField;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.GatewayField;
import xyz.redtorch.pb.CoreField.GatewaySettingField;
import xyz.redtorch.pb.CoreField.NoticeField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.PositionField;
import xyz.redtorch.pb.CoreField.SubmitOrderReqField;
import xyz.redtorch.pb.CoreField.TickField;
import xyz.redtorch.pb.CoreField.TradeField;

/**
 * 模拟网关接口实现，采用真实行情模拟成交
 * @author kevinhuangwl
 *
 */
@Slf4j
public class SimulatedGatewayImpl implements GatewayApi, SimulatedGateway{
	
	private GatewayApi realGatewayApi;
	
	private FastEventEngine feEngine;
	
	private boolean connected = false;
	
	/*账户信息*/
	private GwAccount account;
	
	/*挂单，<挂单ID，挂单>*/
	private ConcurrentHashMap<String, GwOrder> orderMap = new ConcurrentHashMap<>();
	/*成交记录*/
	private ConcurrentHashMap<String, TradeField> tradeMap = new ConcurrentHashMap<>();
	
	public SimulatedGatewayImpl(GatewayApi realGatewayApi, FastEventEngine feEngine, AccountInfo accountInfo, List<PositionInfo> positionInfoList) {
		log.info("启动模拟市场网关");
		this.account = accountInfo==null ? new GwAccount(realGatewayApi) : new GwAccount(accountInfo, positionInfoList);
		this.realGatewayApi = realGatewayApi;
		this.feEngine = feEngine;
		
	}
	
	@Override
	public boolean subscribe(ContractField contract) {
		return realGatewayApi.subscribe(contract);
	}

	@Override
	public boolean unsubscribe(ContractField contract) {
		return realGatewayApi.unsubscribe(contract);
	}
	
	@Override
	public String submitOrder(SubmitOrderReqField submitOrder) {
		if(submitOrder.getOffsetFlag() == OffsetFlagEnum.OF_Unkonwn) {
			log.warn("委托单开平仓状态异常：{}", ErrorHint.UNKNOWN_ENUM_TYPE);
			return "";
		}
		
		GwOrder order = new GwOrder(this);
		boolean validOrder = true;
		OrderField orderField = order.initFrom(submitOrder);
		AccountField acc = account.submitOrder(orderField);
		if(acc != null) {
			feEngine.emitAccount(acc);
			orderMap.put(order.getOriginOrderId(), order);
		}else {
			validOrder = false;
		}
		
		if(validOrder) {		
			log.info("模拟交易接口发单记录->{\n" //
					+ "InstrumentID:{},\n" //
					+ "LimitPrice:{},\n" //
					+ "VolumeTotalOriginal:{},\n" //
					+ "OrderPriceType:{},\n" //
					+ "Direction:{},\n" //
					+ "CombOffsetFlag:{},\n" //
					+ "CombHedgeFlag:{},\n" //
					+ "ContingentCondition:{},\n" //
					+ "ForceCloseReason:{},\n" //
					+ "MinVolume:{},\n" //
					+ "TimeCondition:{},\n" //
					+ "VolumeCondition:{},\n" //
					+ "StopPrice:{}}", //
					submitOrder.getContract().getSymbol(), //
					submitOrder.getPrice(), //
					submitOrder.getVolume(), //
					submitOrder.getOrderPriceType(), //
					submitOrder.getDirection(), //
					submitOrder.getOffsetFlag(), //
					submitOrder.getHedgeFlag(), //
					submitOrder.getContingentCondition(), //
					submitOrder.getForceCloseReason(), //
					submitOrder.getMinVolume(), //
					submitOrder.getTimeCondition(), //
					submitOrder.getVolumeCondition(), //
					submitOrder.getStopPrice());
			
			feEngine.emitOrder(order.orderConfirmed());
		}else {
			OrderField resultOrder = order.orderRejected();
			log.info("模拟订单被拒绝。原因：{}", resultOrder.getStatusMsg());
			feEngine.emitOrder(resultOrder);
		}
		
		return order.getOriginOrderId();
	}
	
	@Override
	public boolean cancelOrder(CancelOrderReqField cancelOrder) {
		String originOrderId = cancelOrder.getOriginOrderId();
		GwOrder order = orderMap.remove(originOrderId);
		if(order == null) {
			log.info("撤单失败。订单{}已成交", originOrderId);
			return false;
		}
		
		OrderField orderField = order.orderCancelled();
		feEngine.emitAccount(account.releaseOrder(orderField));
		feEngine.emitOrder(orderField);
		
		log.info("模拟撤单成功，合约：{}，订单号：{}", orderField.getContract().getUnifiedSymbol(), originOrderId);
		return true;
	}
	
	@Override
	public void emitTick(TickField tick) {
		feEngine.emitAccount(account.updateByTick(tick));
		
		for(PositionField p : account.getPositions()) {
			feEngine.emitPosition(p);
		}

		for(Entry<String, GwOrder> e : orderMap.entrySet()) {
			GwOrder order = e.getValue();
			ContractField c = order.getContract();
			if(!StringUtils.equals(c.getUnifiedSymbol(), tick.getUnifiedSymbol())) {
				//合约不匹配
				continue;
			}
			
			TradeField tradeField = order.tryDeal(tick);
			if(tradeField != null) {				
				log.info("模拟订单{}全部成交，合约：{}", order.getOriginOrderId(), tick.getUnifiedSymbol());
				OrderField tradedOrder = order.orderTraded();
				account.releaseOrder(tradedOrder);
				
				feEngine.emitTrade(tradeField);
				feEngine.emitOrder(tradedOrder);
				try {
					feEngine.emitAccount(account.tradeWith(tradeField));
				} catch (TradeException ex) {
					log.error("", ex);
				}
				
				tradeMap.put(tradeField.getTradeId(), tradeField);
				orderMap.remove(e.getKey());
			}
		}
	}
	
	//为了不影响正常行情，模拟网关的连接与断开不对行情网关产生作用
	@Override
	public void connect() {
		connected = true;
		
		feEngine.emitAccount(account.getAccount());
		for(PositionField p : account.getPositions()) {
			feEngine.emitPosition(p);
		}
		
		NoticeInfo noticeInfo = new NoticeInfo();
		noticeInfo.setEvent(NoticeCode.GATEWAY_READY);
		noticeInfo.setMessage("网关:" + getGatewayName() + "，网关ID:" + getGatewayId() + "，可以交易");
		noticeInfo.setData(getGatewayName());
		
		NoticeField.Builder noticeBuilder = NoticeField.newBuilder();
		noticeBuilder.setContent(new Gson().toJson(noticeInfo));
		noticeBuilder.setStatus(CommonStatusEnum.COMS_SUCCESS);
		noticeBuilder.setTimestamp(System.currentTimeMillis());
		feEngine.emitNotice(noticeBuilder.build());
	}

	@Override
	public void disconnect() {
		connected = false;
	}

	@Override
	public boolean isConnected() {
		return connected;
	}

	@Override
	public String getGatewayId() {
		return realGatewayApi.getGatewayId();
	}

	@Override
	public String getGatewayName() {
		return realGatewayApi.getGatewayName();
	}

	@Override
	public GatewayField getGateway() {
		return realGatewayApi.getGateway();
	}

	@Override
	public String getTradingDay() {
		return realGatewayApi.getTradingDay();
	}

	@Override
	public GatewaySettingField getGatewaySetting() {
		return realGatewayApi.getGatewaySetting();
	}

	@Override
	public boolean getAuthErrorFlag() {
		return realGatewayApi.getAuthErrorFlag();
	}

	@Override
	public void setAuthErrorFlag(boolean loginErrorFlag) {
		realGatewayApi.setAuthErrorFlag(loginErrorFlag);
	}

	@Override
	public long getLastConnectBeginTimestamp() {
		return realGatewayApi.getLastConnectBeginTimestamp();
	}

	@Override
	public FastEventEngine getEventEngine() {
		return feEngine;
	}

	@Override
	public void proceedDailySettlement() {
		// TODO Auto-generated method stub
		
	}

}
