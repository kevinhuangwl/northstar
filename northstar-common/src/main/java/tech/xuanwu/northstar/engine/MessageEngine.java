package tech.xuanwu.northstar.engine;

import xyz.redtorch.pb.CoreField.TickField;

/**
 * 消息引擎
 * 负责内部组件通信事件（包括网页端事件回调，以及策略端事件回调）
 * @author kevinhuangwl
 *
 */
public interface MessageEngine {

	void emitTick(TickField tick);

}
