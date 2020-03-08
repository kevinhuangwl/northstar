package tech.xuanwu.northstar.core.persistence.repo;

import java.util.List;

import tech.xuanwu.northstar.entity.GatewayInfo;

public interface GatewayRepo {

	/**
	 * 添加网关信息
	 * @param gateway
	 * @return
	 */
	boolean upsertById(GatewayInfo gateway);
	
	/**
	 * 根据网关ID查询网关信息
	 * @param gatewayId
	 * @return
	 */
	GatewayInfo findGatewayById(String gatewayId);
	
	/**
	 * 获取全部网关信息
	 * @return
	 */
	List<GatewayInfo> getAllGateways();
}
