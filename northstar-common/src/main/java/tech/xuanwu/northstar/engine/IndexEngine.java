package tech.xuanwu.northstar.engine;

import xyz.redtorch.pb.CoreField.TickField;

/**
 * 指数引擎
 * 负责合约的指数行情管理
 * @author kevinhuangwl
 *
 */

public interface IndexEngine {
	
	void onGatewayReady(String gatewayId) throws Exception;

	boolean addIndexContract(String gatewayId, String symbol) throws Exception;
	
	boolean removeIndexContract(String gatewayId, String symbol) throws Exception;
	
	void updateTick(TickField tick);
}
