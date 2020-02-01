package tech.xuanwu.northstar.core.engine;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.core.domain.ContractMarketData;
import tech.xuanwu.northstar.engine.RuntimeEngine;
import xyz.redtorch.pb.CoreField.TickField;

@Slf4j
@Component
public class RealTraderRuntimeEngine implements RuntimeEngine{
	
	//初始化时先预留N个合约的容量
	private ConcurrentHashMap<String, ContractMarketData> cmdMap = new ConcurrentHashMap<>(500);

	@Override
	public void updateTick(TickField tick) {
		String contractId = tick.getContract().getContractId();
		if(!cmdMap.containsKey(contractId)) {
			cmdMap.put(contractId, new ContractMarketData());
		}
		
		cmdMap.get(contractId).updateTick(tick);
	}

	@Override
	public boolean regAccount() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unregAccount() {
		// TODO Auto-generated method stub
		return false;
	}
	
	

}
