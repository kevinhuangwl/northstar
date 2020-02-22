package tech.xuanwu.northstar.entity;

import java.io.Serializable;

import lombok.Data;
import xyz.redtorch.pb.CoreEnum.CurrencyEnum;
import xyz.redtorch.pb.CoreField.AccountField;

@Data
public class AccountInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -92867553096817323L;
	
	String accountId;  // ID，通常是 <账户代码@币种@网关>
	String code;  // 代码
	String name;  // 名称
	String holder;  // 持有人
	CurrencyEnum currency;  // 币种
	double preBalance;  // 昨日权益
	double balance;  // 权益
	double available;  // 可用资金
	double commission;  // 手续费
	double margin;  // 保证金占用
	double closeProfit;  // 平仓盈亏
	double positionProfit;  // 持仓盈亏
	double deposit;  // 入金
	double withdraw;  // 出金
	String gatewayId;  // 网关ID
	
	public static AccountInfo convertFrom(AccountField accountField) {
		AccountInfo info = new AccountInfo();
		info.accountId = accountField.getAccountId();
		info.code = accountField.getCode();
		info.name = accountField.getName();
		info.holder = accountField.getHolder();
		info.currency = accountField.getCurrency();
		info.preBalance = accountField.getPreBalance();
		info.balance = accountField.getBalance();
		info.available = accountField.getAvailable();
		info.commission = accountField.getCommission();
		info.margin = accountField.getMargin();
		info.closeProfit = accountField.getCloseProfit();
		info.positionProfit = accountField.getPositionProfit();
		info.deposit = accountField.getDeposit();
		info.withdraw = accountField.getWithdraw();
		info.gatewayId = accountField.getGatewayId();
		return info;
	}
}
