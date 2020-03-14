package tech.xuanwu.northstar.core.engine.rt.event;

import java.util.EventObject;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tech.xuanwu.northstar.constant.RuntimeEvent;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.OrderInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.entity.TransactionInfo;
import tech.xuanwu.northstar.exception.NoSuchAccountException;
import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.PositionField;
import xyz.redtorch.pb.CoreField.TradeField;

@Component
public class AccountEventHandler implements RuntimeEngine.Listener, InitializingBean{
	
	
	@Autowired
	private RuntimeEngine rtEngine;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		rtEngine.addEventHandler(RuntimeEvent.FEEDBACK_ACCOUNT, this);
		rtEngine.addEventHandler(RuntimeEvent.FEEDBACK_POSITION, this);
		rtEngine.addEventHandler(RuntimeEvent.FEEDBACK_ORDER, this);
		rtEngine.addEventHandler(RuntimeEvent.FEEDBACK_TRADE, this);
	}

	@Override
	public void onEvent(EventObject e) throws NoSuchAccountException {
		Object obj = e.getSource();
		
		if(obj instanceof AccountField) {
			AccountField account = (AccountField) obj;
			String gatewayId = account.getGatewayId();
			rtEngine.getAccount(gatewayId).updateAccount(AccountInfo.convertFrom(account));
			
		}else if (obj instanceof PositionField) {
			PositionField position = (PositionField) obj;
			String gatewayId = position.getGatewayId();
			rtEngine.getAccount(gatewayId).updatePosition(PositionInfo.convertFrom(position));
			
		}else if (obj instanceof OrderField) {
			OrderField order = (OrderField) obj;
			String gatewayId = order.getGatewayId();
			rtEngine.getAccount(gatewayId).updateOrder(OrderInfo.convertFrom(order));
			
		}else if (obj instanceof TradeField) {
			TradeField trade = (TradeField) obj;
			String gatewayId = trade.getGatewayId();
			rtEngine.getAccount(gatewayId).updateTransaction(TransactionInfo.convertFrom(trade));
			
		}
	}

}
