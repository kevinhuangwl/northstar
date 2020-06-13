package tech.xuanwu.northstar.core.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.common.ResultBean;
import tech.xuanwu.northstar.common.ReturnCode;
import tech.xuanwu.northstar.constant.ErrorHint;
import tech.xuanwu.northstar.entity.AccountConnectionInfo;
import tech.xuanwu.northstar.entity.AccountInfo;
import tech.xuanwu.northstar.entity.OrderInfo;
import tech.xuanwu.northstar.entity.OrderVO;
import tech.xuanwu.northstar.entity.PositionInfo;
import tech.xuanwu.northstar.entity.PositionVO;
import tech.xuanwu.northstar.entity.TransactionInfo;
import tech.xuanwu.northstar.entity.TransactionVO;
import tech.xuanwu.northstar.exception.NoSuchAccountException;
import tech.xuanwu.northstar.service.AccountService;
import xyz.redtorch.pb.CoreEnum.ConnectStatusEnum;

@Slf4j
@RestController
@RequestMapping("/account")
@Api(tags = "用户管理相关接口")
public class AccountController {

	@Autowired
	private AccountService acService;

	@GetMapping("/list")
	@ApiOperation("查询账户列表")
	public ResultBean<List<AccountConnectionInfo>> getAccountList() {
		return new ResultBean<List<AccountConnectionInfo>>(acService.getAccountList());
	}

	@GetMapping("/info")
	@ApiOperation("获取账户信息")
	public ResultBean<AccountInfo> getAccountInfo(String accountId) throws NoSuchAccountException {
		Assert.hasText(accountId, ErrorHint.EMPTY_PARAM);
		return new ResultBean<>(acService.getAccountInfo(accountId));
	}

	@GetMapping("/position")
	@ApiOperation("获取账户持仓信息")
	public ResultBean<List<PositionVO>> getPositionList(String accountId) throws NoSuchAccountException {
		Assert.hasText(accountId, ErrorHint.EMPTY_PARAM);
		return new ResultBean<List<PositionVO>>(acService.getPositionInfoList(accountId).stream()
				.map(p -> PositionVO.convertFrom(p)).collect(Collectors.toList()));
	}
	
	@GetMapping("/positionInfo")
	@ApiOperation("获取账户持仓信息")
	public ResultBean<List<PositionInfo>> getPositionInfoList(String accountId) throws NoSuchAccountException {
		Assert.hasText(accountId, ErrorHint.EMPTY_PARAM);
		return new ResultBean<List<PositionInfo>>(acService.getPositionInfoList(accountId));
	}

	@GetMapping("/order")
	@ApiOperation("获取账户订单信息")
	public ResultBean<List<OrderVO>> getOrderList(String accountId) throws NoSuchAccountException {
		Assert.hasText(accountId, ErrorHint.EMPTY_PARAM);
		return new ResultBean<List<OrderVO>>(acService.getOrderInfoList(accountId).stream()
				.map(o -> OrderVO.convertFrom(o)).collect(Collectors.toList()));
	}
	
	@GetMapping("/orderInfo")
	@ApiOperation("获取账户订单信息")
	public ResultBean<List<OrderInfo>> getOrderInfoList(String accountId) throws NoSuchAccountException {
		Assert.hasText(accountId, ErrorHint.EMPTY_PARAM);
		return new ResultBean<List<OrderInfo>>(acService.getOrderInfoList(accountId));
	}

	@GetMapping("/trade")
	@ApiOperation("获取账户成交信息")
	public ResultBean<List<TransactionVO>> getTransactionList(String accountId) throws NoSuchAccountException {
		Assert.hasText(accountId, ErrorHint.EMPTY_PARAM);
		return new ResultBean<List<TransactionVO>>(acService.getTransactionInfoList(accountId).stream()
				.map(t -> TransactionVO.convertFrom(t)).collect(Collectors.toList()));
	}
	
	@GetMapping("/tradeInfo")
	@ApiOperation("获取账户成交信息")
	public ResultBean<List<TransactionInfo>> getTransactionInfoList(String accountId) throws NoSuchAccountException {
		Assert.hasText(accountId, ErrorHint.EMPTY_PARAM);
		return new ResultBean<List<TransactionInfo>>(acService.getTransactionInfoList(accountId));
	}

	@GetMapping("/connect")
	@ApiOperation("连接账户网关")
	public ResultBean<Void> connectGateway(String accountId) throws Exception {
		Assert.hasText(accountId, ErrorHint.EMPTY_PARAM);
		log.info("账户[{}]连接网关", accountId);
		acService.connect(accountId);
		return new ResultBean<>(null);
	}

	@GetMapping("/disconnect")
	@ApiOperation("断开账户网关")
	public ResultBean<Void> disconnectGateway(String accountId) throws NoSuchAccountException {
		Assert.hasText(accountId, ErrorHint.EMPTY_PARAM);
		log.info("账户[{}]断开网关", accountId);
		acService.disconnect(accountId);
		return new ResultBean<>(null);
	}

}
