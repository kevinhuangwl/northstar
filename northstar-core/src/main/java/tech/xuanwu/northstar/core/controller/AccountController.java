package tech.xuanwu.northstar.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

	@GetMapping("/position")
	@ApiOperation("获取账户持仓信息")
	public ResultBean<List<PositionInfo>> getPositionInfoList(@RequestParam String gatewayId) {
		
		try {
			return new ResultBean<List<PositionInfo>>(acService.getPositionInfoList(gatewayId));
		} catch (NoSuchAccountException e) {
			log.error("", e);
			return new ResultBean<List<PositionInfo>>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
	}

	@GetMapping("/order")
	@ApiOperation("获取账户订单信息")
	public ResultBean<List<OrderInfo>> getOrderInfoList(@RequestParam String gatewayId) {
		try {
			return new ResultBean<List<OrderInfo>>(acService.getOrderInfoList(gatewayId));
		} catch (NoSuchAccountException e) {
			log.error("", e);
			return new ResultBean<List<OrderInfo>>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
	}

	@GetMapping("/trade")
	@ApiOperation("获取账户成交信息")
	public ResultBean<List<TransactionInfo>> getTransactionInfoList(@RequestParam String gatewayId) {
		try {
			return new ResultBean<List<TransactionInfo>>(acService.getTransactionInfoList(gatewayId));
		} catch (NoSuchAccountException e) {
			log.error("", e);
			return new ResultBean<List<TransactionInfo>>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
	}

	@GetMapping("/connect")
	@ApiOperation("连接账户网关")
	public ResultBean<Void> connectGateway() {
		log.info("账户CTP连接网关");
		try {
			acService.connectGateway();
		} catch (Exception e) {
			log.error("", e);
			return new ResultBean<Void>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
		return new ResultBean(Void.TYPE);
	}

	@GetMapping("/disconnect")
	@ApiOperation("断开账户网关")
	public ResultBean<Void> disconnectGateway(@RequestParam String gatewayId) {
		log.info("账户[{}]断开网关", gatewayId);
		try {
			acService.disconnectGateway(gatewayId);
		} catch (NoSuchAccountException e) {
			log.error("", e);
			return new ResultBean<Void>(ResultBean.ReturnCode.ERROR, e.getMessage());
		}
		return new ResultBean(Void.TYPE);
	}

}
