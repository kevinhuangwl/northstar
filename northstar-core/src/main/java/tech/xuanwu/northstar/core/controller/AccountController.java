package tech.xuanwu.northstar.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.common.ResultBean;
import tech.xuanwu.northstar.core.service.AccountService;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.OrderInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.entity.TransactionInfo;
import tech.xuanwu.northstar.exception.NoSuchAccountException;

@Slf4j
@RestController
@RequestMapping("/account")
@Api(tags = "用户管理相关接口")
public class AccountController {
	
	@Autowired
	private AccountService acService;

	@GetMapping("/list")
	@ApiOperation("查询账户列表")
	public ResultBean<List<AccountInfo>> getAccountInfoList() {
		return new ResultBean<List<AccountInfo>>(acService.getAccountInfoList());
	}

	@GetMapping("/position/{accountGatewayId}")
	@ApiOperation("获取账户持仓信息")
	public ResultBean<List<PositionInfo>> getPositionInfoList(@PathVariable String accountGatewayId) {
		
		try {
			return new ResultBean<List<PositionInfo>>(acService.getPositionInfoList(accountGatewayId));
		} catch (NoSuchAccountException e) {
			return new ResultBean<List<PositionInfo>>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
	}

	@GetMapping("/order/{accountGatewayId}")
	@ApiOperation("获取账户订单信息")
	public ResultBean<List<OrderInfo>> getOrderInfoList(@PathVariable String accountGatewayId) {
		try {
			return new ResultBean<List<OrderInfo>>(acService.getOrderInfoList(accountGatewayId));
		} catch (NoSuchAccountException e) {
			return new ResultBean<List<OrderInfo>>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
	}

	@GetMapping("/trade/{accountGatewayId}")
	@ApiOperation("获取账户成交信息")
	public ResultBean<List<TransactionInfo>> getTransactionInfoList(@PathVariable String accountGatewayId) {
		try {
			return new ResultBean<List<TransactionInfo>>(acService.getTransactionInfoList(accountGatewayId));
		} catch (NoSuchAccountException e) {
			return new ResultBean<List<TransactionInfo>>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
	}

	@GetMapping("/connect/{accountGatewayId}")
	@ApiOperation("连接账户网关")
	public ResultBean<Void> connectGateway(@PathVariable String accountGatewayId) {
		log.info("账户[{}]连接网关", accountGatewayId);
		try {
			acService.connectGateway(accountGatewayId);
		} catch (NoSuchAccountException e) {
			return new ResultBean<Void>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
		return new ResultBean(Void.TYPE);
	}

	@GetMapping("/disconnect/{accountGatewayId}")
	@ApiOperation("断开账户网关")
	public ResultBean<Void> disconnectGateway(@PathVariable String accountGatewayId) {
		log.info("账户[{}]断开网关", accountGatewayId);
		try {
			acService.disconnectGateway(accountGatewayId);
		} catch (NoSuchAccountException e) {
			return new ResultBean<Void>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
		return new ResultBean(Void.TYPE);
	}

}
