package tech.xuanwu.northstar.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.common.ResultBean;
import tech.xuanwu.northstar.core.service.AccountService;
import tech.xuanwu.northstar.exception.NoSuchAccountException;
import xyz.redtorch.pb.CoreField.AccountField;
import xyz.redtorch.pb.CoreField.OrderField;
import xyz.redtorch.pb.CoreField.PositionField;
import xyz.redtorch.pb.CoreField.TradeField;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {
	
	@Autowired
	private AccountService acService;

	@GetMapping("/list")
	public ResultBean<List<AccountField>> getAccountInfoList() {
		return new ResultBean<List<AccountField>>(acService.getAccountInfoList());
	}

	@GetMapping("/position/${accountName}")
	public ResultBean<List<PositionField>> getPositionInfoList(@PathVariable String accountName) {
		
		try {
			return new ResultBean<List<PositionField>>(acService.getPositionInfoList(accountName));
		} catch (NoSuchAccountException e) {
			return new ResultBean<List<PositionField>>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
	}

	@GetMapping("/order/${accountName}")
	public ResultBean<List<OrderField>> getOrderInfoList(@PathVariable String accountName) {
		try {
			return new ResultBean<List<OrderField>>(acService.getOrderInfoList(accountName));
		} catch (NoSuchAccountException e) {
			return new ResultBean<List<OrderField>>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
	}

	@GetMapping("/trade/${accountName}")
	public ResultBean<List<TradeField>> getTransactionInfoList(@PathVariable String accountName) {
		try {
			return new ResultBean<List<TradeField>>(acService.getTransactionInfoList(accountName));
		} catch (NoSuchAccountException e) {
			return new ResultBean<List<TradeField>>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
	}

	@GetMapping("/connect/${accountName}")
	public ResultBean<Void> connectGateway(@PathVariable String accountName) {
		log.info("账户[{}]连接网关", accountName);
		try {
			acService.connectGateway(accountName);
		} catch (NoSuchAccountException e) {
			return new ResultBean<Void>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
		return new ResultBean(Void.TYPE);
	}

	@GetMapping("/disconnect/${accountName}")
	public ResultBean<Void> disconnectGateway(@PathVariable String accountName) {
		log.info("账户[{}]断开网关", accountName);
		try {
			acService.disconnectGateway(accountName);
		} catch (NoSuchAccountException e) {
			return new ResultBean<Void>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
		return new ResultBean(Void.TYPE);
	}

}
