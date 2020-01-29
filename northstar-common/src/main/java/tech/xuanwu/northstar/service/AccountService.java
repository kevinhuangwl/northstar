package tech.xuanwu.northstar.service;

import java.util.List;

import xyz.redtorch.pb.CoreField.AccountField;

/**
 * 账户服务，用于提供账户操作的业务逻辑
 * @author kevinhuangwl
 *
 */
public interface AccountService {

	/**
	 * 查询账户信息
	 * @return
	 */
	List<AccountField> getAccountsInfo();
	
	
}
