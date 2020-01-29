package tech.xuanwu.northstar.core.domain;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import xyz.redtorch.pb.CoreEnum.CurrencyEnum;
import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.GatewayField;
import xyz.redtorch.pb.CoreField.PositionField;

/**
 * 账户对象
 * @author kevinhuangwl
 *
 */
@Getter
public class Account {

	private String accountId;  // ID，通常是 <账户代码@币种@网关>
	private String code;  // 代码
	private String name;  // 名称
	private String holder;  // 持有人
	private CurrencyEnum currency;  // 币种
	private double preBalance;  // 昨日权益
	private double balance;  // 权益
	private double available;  // 可用资金
	private double commission;  // 手续费
	private double margin;  // 保证金占用
	private double closeProfit;  // 平仓盈亏
	private double positionProfit;  // 持仓盈亏
	private double deposit;  // 入金
	private double withdraw;  // 出金
	private GatewayField gateway;  // 网关
	private AccountField accountField;
	
	public Account(){}
	
	public Account(AccountField accountField){
		
	}
	
	
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
