package tech.xuanwu.northstar.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import tech.xuanwu.northstar.common.ResultBean;
import tech.xuanwu.northstar.common.ReturnCode;
import tech.xuanwu.northstar.constant.ErrorHint;
import tech.xuanwu.northstar.service.MailSenderService;

@Slf4j
@RestController
@RequestMapping("/mail")
public class MailSenderController {

	@Autowired
	MailSenderService service;
	
	@PostMapping("/msg")
	@ApiOperation("发送邮件消息")
	public ResultBean<Void> sendMessage(String title, String content){
		try {
			service.sendMessage(title, content);
			return new ResultBean<Void>(null);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResultBean<Void>(ReturnCode.ERROR, ErrorHint.FAIL_TO_SEND_MAIL);
		}
	}
}
