package tech.xuanwu.northstar.core.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.MultiValueMap;

import lombok.Data;
import lombok.Getter;
import xyz.redtorch.pb.CoreEnum.CurrencyEnum;
import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.GatewayField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.PositionField;
import xyz.redtorch.pb.CoreField.TradeField;

/**
 * 账户对象，每个实际账户一个实例
 * 作为聚合根，协调多个策略之间的开平仓信号，以及在资金占用率过高时，禁止策略开仓
 * @author kevinhuangwl
 *
 */
public class Account {

	/*基本账户信息副本*/
	private AccountField account;
	
	/*持仓信息*/
	private Positions positions; 
	
	/*订单信息*/
	private Map<String, OrderField> ordersMap;
	
	/*成交信息*/
	private List<TradeField> transactionList;
	
	public Account(){}
	
	public Account(AccountField accountField){
		
	}
	
	/**
	 * 账户更新
	 * @param accountField
	 */
	public void updateAccount(AccountField accountField) {
		
	}
	
	private void setPropsFrom(AccountField accountField) {
		
	}
	
	private void saveChange() {
		
	}
	
	public boolean placeOrder() {
		return false;
	}
	
	public boolean cancelOrder() {
		return false;
	}
	
	public List<PositionField> getAllPosition() {
		return null;
	}
	
}
