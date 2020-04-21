package tech.xuanwu.northstar.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.common.ResultBean;
import tech.xuanwu.northstar.common.ReturnCode;
import tech.xuanwu.northstar.constant.ErrorHint;
import tech.xuanwu.northstar.service.MailSenderService;

@Slf4j
@RestController
@RequestMapping("/mail")
@Api(tags = "邮件接口")
public class MailSenderController {

	@Autowired
	MailSenderService service;
	
	@PostMapping("/msg")
	@ApiOperation("发送邮件消息")
	public ResultBean<Void> sendMessage(String title, String content){
		try {
			Assert.hasText(title, ErrorHint.EMPTY_PARAM);
			Assert.hasText(content, ErrorHint.EMPTY_PARAM);
			service.sendMessage(title, content);
			return new ResultBean<Void>(null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResultBean<Void>(ReturnCode.ERROR, ErrorHint.FAIL_TO_SEND_MAIL);
		}
	}
}
