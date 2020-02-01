package tech.xuanwu.northstar.engine;

import xyz.redtorch.pb.CoreField.TickField;

/**
 * 运行时引擎
 * 负责提供领域对象的运行时环境
 * @author kevinhuangwl
 *
 */
public interface RuntimeEngine {

	boolean regAccount();
	
	boolean unregAccount();
	
	void updateTick(TickField tick);
}
