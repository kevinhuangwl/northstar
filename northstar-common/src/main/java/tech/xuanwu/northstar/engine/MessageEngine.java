package tech.xuanwu.northstar.engine;

import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.TickField;
import xyz.redtorch.pb.CoreField.TradeField;

/**
 * 消息引擎
 * 负责内部组件通信事件（包括网页端事件回调，以及策略端事件回调）
 * @author kevinhuangwl
 *
 */
public interface MessageEngine {

	void emitTick(TickField tick);

	void emitAccount(AccountField account);
	
	void emitOrder(OrderField order);
	
	void emitTransaction(TradeField trade);
	
}
