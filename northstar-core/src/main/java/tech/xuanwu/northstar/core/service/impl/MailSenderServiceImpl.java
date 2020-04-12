package tech.xuanwu.northstar.core.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import tech.xuanwu.northstar.service.MailSenderService;
import xyz.redtorch.pb.CoreField.TradeField;

@Service
public class MailSenderServiceImpl implements MailSenderService {

	@Autowired
    private JavaMailSender mailSender;
	
	@Value("${spring.mail.subscribed}")
	private String subscribedMails;
	
	@Value("${spring.mail.username}")
	private String fromMail;
	

	@Override
	public void sendTradeMessage(TradeField trade) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessage(String title, String content) {
		SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromMail);
        message.setTo(subscribedMails);
        message.setSubject(title);
        message.setText(content);
        mailSender.send(message);
	}

}
