package tech.xuanwu.northstar.core.engine;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.constant.CommonConstant;
import tech.xuanwu.northstar.core.domain.IndexContract;
import tech.xuanwu.northstar.core.persistence.repo.ContractRepo;
import tech.xuanwu.northstar.core.persistence.repo.IndexContractRepo;
import tech.xuanwu.northstar.engine.FastEventEngine;
import tech.xuanwu.northstar.engine.IndexEngine;
import tech.xuanwu.northstar.entity.ContractInfo;
import tech.xuanwu.northstar.gateway.GatewayApi;
import xyz.redtorch.pb.CoreField.ContractField;
import xyz.redtorch.pb.CoreField.TickField;

@Slf4j
@Component
public class NorthstarIndexEngine implements IndexEngine{
	
	private ConcurrentHashMap<String, IndexContract> idxContractMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, IndexContract> contractToIndexContractMap = new ConcurrentHashMap<>();
	
	@Autowired
	private IndexContractRepo idxContractRepo;
	
	@Autowired
	private ContractRepo contractRepo;
	
	@Autowired
	private FastEventEngine feEngine;
	
	@Autowired
	private ApplicationContext ctx;

	@Override
	public boolean addIndexContract(String gatewayId, String symbol) throws Exception {
		ContractField contract = generateIndexContractAndPrepare(gatewayId, symbol);
		ContractInfo contractInfo = ContractInfo.convertFrom(contract);
		contractInfo.setSubscribed(true);
		return idxContractRepo.insertIfAbsent(contractInfo);
	}
	
	private ContractField generateIndexContractAndPrepare(String gatewayId, String symbol) throws Exception {
		log.info("网关【{}】增加订阅指数合约【{}】", gatewayId, symbol);
		List<ContractInfo> contractList = contractRepo.getSeriesContractsByExample(gatewayId, symbol);
		IndexContract idxContract = new IndexContract(symbol, contractList, (indexTick)->{
			feEngine.emitTick(indexTick);
		});
		//记录合约
		ContractField contract = idxContract.getContract();
		idxContractMap.put(String.format("%s@%s", symbol, gatewayId), idxContract);

		//向网关订阅
		GatewayApi mktGateway = (GatewayApi) ctx.getBean(CommonConstant.CTP_MKT_GATEWAY);
		for(ContractInfo c : contractList) {
			mktGateway.subscribe(c.convertTo());
			contractToIndexContractMap.put(c.getUnifiedSymbol(), idxContract);
		}
		return contract;
	}

	@Override
	public boolean removeIndexContract(String gatewayId, String symbol) throws Exception {
		log.info("网关【{}】移除订阅指数合约【{}】", gatewayId, symbol);
		List<ContractInfo> contractList = contractRepo.getSeriesContractsByExample(gatewayId, symbol);
		idxContractMap.remove(String.format("%s@%s", symbol, gatewayId));
		for(ContractInfo c : contractList) {
			contractToIndexContractMap.remove(c.getUnifiedSymbol());
		}
		return idxContractRepo.delete(gatewayId, symbol);
	}

	@Override
	public void updateTick(TickField tick) {
		IndexContract idxContract = contractToIndexContractMap.get(tick.getUnifiedSymbol());
		if(idxContract == null) {
			return;
		}
		idxContract.updateByTick(tick);
	}

	@Override
	public void onGatewayReady(String gatewayId) throws Exception {
		List<ContractInfo> idxContractList = idxContractRepo.getAllSubscribedContracts(gatewayId);
		if(idxContractList.size()==0) {
			return;
		}
		log.info("续订网关【{}】的指数合约", gatewayId);
		for(ContractInfo idxContract : idxContractList) {
			generateIndexContractAndPrepare(idxContract.getGatewayId(), idxContract.getSymbol());
		}
		
	}

}
