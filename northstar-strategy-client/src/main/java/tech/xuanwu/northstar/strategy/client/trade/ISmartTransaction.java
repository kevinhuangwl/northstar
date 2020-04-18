package tech.xuanwu.northstar.strategy.client.trade;

import tech.xuanwu.northstar.exception.TradeException;
import xyz.redtorch.pb.CoreField.ContractField;

public interface ISmartTransaction {

	void openAtMktPrice(ContractField c, int volume) throws TradeException;
	
	void open(ContractField c, int volume, double price) throws TradeException;
	
	void closeAtMktPrice(ContractField c, int volume) throws TradeException;
	
	void close(ContractField c, int volume, double price) throws TradeException;
}
