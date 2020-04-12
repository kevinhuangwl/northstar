package tech.xuanwu.northstar.service;

import xyz.redtorch.pb.CoreField.TradeField;

public interface MailSenderService {

	public void sendTradeMessage(TradeField trade);
	
	public void sendMessage(String title, String content);
}
