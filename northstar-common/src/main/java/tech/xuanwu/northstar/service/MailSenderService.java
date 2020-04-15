package tech.xuanwu.northstar.service;

import xyz.redtorch.pb.CoreField.TradeField;

/**
 * 邮件服务接口
 * @author kevinhuangwl
 *
 */
public interface MailSenderService {

	public void sendTradeMessage(TradeField trade) throws Exception;
	
	public void sendMessage(String title, String content) throws Exception;
}
