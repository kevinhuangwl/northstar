package tech.xuanwu.northstar.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.common.ResultBean;
import tech.xuanwu.northstar.common.ReturnCode;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.OrderInfo;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.entity.TransactionInfo;
import tech.xuanwu.northstar.exception.NoSuchAccountException;
import tech.xuanwu.northstar.service.AccountService;

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

	@GetMapping("/position")
	@ApiOperation("获取账户持仓信息")
	public ResultBean<List<PositionInfo>> getPositionInfoList(String gatewayId) {
		
		try {
			return new ResultBean<List<PositionInfo>>(acService.getPositionInfoList(gatewayId));
		} catch (NoSuchAccountException e) {
			log.error("", e);
			return new ResultBean<List<PositionInfo>>(ReturnCode.ERROR, e.getMessage());
		}
	}

	@GetMapping("/order")
	@ApiOperation("获取账户订单信息")
	public ResultBean<List<OrderInfo>> getOrderInfoList(String gatewayId) {
		try {
			return new ResultBean<List<OrderInfo>>(acService.getOrderInfoList(gatewayId));
		} catch (NoSuchAccountException e) {
			log.error("", e);
			return new ResultBean<List<OrderInfo>>(ReturnCode.ERROR, e.getMessage());
		}
	}

	@GetMapping("/trade")
	@ApiOperation("获取账户成交信息")
	public ResultBean<List<TransactionInfo>> getTransactionInfoList(String gatewayId) {
		try {
			return new ResultBean<List<TransactionInfo>>(acService.getTransactionInfoList(gatewayId));
		} catch (NoSuchAccountException e) {
			log.error("", e);
			return new ResultBean<List<TransactionInfo>>(ReturnCode.ERROR, e.getMessage());
		}
	}

	@GetMapping("/connect")
	@ApiOperation("连接账户网关")
	public ResultBean<Void> connectGateway(String gatewayId) {
		log.info("账户CTP连接网关");
		try {
			acService.connectGateway(gatewayId);
			return new ResultBean<>(null);
		} catch (Exception e) {
			log.error("", e);
			return new ResultBean<Void>(ReturnCode.ERROR, e.getMessage());
		}
	}

	@GetMapping("/disconnect")
	@ApiOperation("断开账户网关")
	public ResultBean<Void> disconnectGateway(String gatewayId) {
		log.info("账户[{}]断开网关", gatewayId);
		try {
			acService.disconnectGateway(gatewayId);
			return new ResultBean<>(null);
		} catch (NoSuchAccountException e) {
			log.error("", e);
			return new ResultBean<Void>(ReturnCode.ERROR, e.getMessage());
		}
	}

}
