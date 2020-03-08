package tech.xuanwu.northstar.constant;

import java.util.HashMap;

public interface NoticeCode {

	static String GATEWAY_CONNECTED = "000";
	static String GATEWAY_READY = "001";
	
	static HashMap<String, RuntimeEvent> EVENT_MAP = new HashMap<String, RuntimeEvent>() {
		private static final long serialVersionUID = 3081838644051898338L;
		{
			put(GATEWAY_CONNECTED, RuntimeEvent.GATEWAY_CONNECTED);
			put(GATEWAY_READY, RuntimeEvent.GATEWAY_READY);
		}
	};
}
