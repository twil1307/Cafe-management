package com.system.cfmanage.utils;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailUtils {
	@Autowired
	private JavaMailSender emailSender;

	public void sendSimpleMessage(String to, String subject, String text, List<String> list) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("leducCare@gmail.com");
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		if(list!=null && list.size() > 0) {
			message.setCc(getCcArray(list));;			
		}
		
		emailSender.send(message);
	}

	private String[] getCcArray(List<String> listCc) {
		String[] cc = new String[listCc.size()];
		for (int i = 0; i < listCc.size(); i++) {
			cc[i] = listCc.get(i);
		}
		return cc;
	}
	
	public void forgotPasswordMail(String to, String subject, String otp) throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom("leducCare@gmail.com");
		helper.setTo(to);
		helper.setSubject(subject);
		String htmlMsg = "<p><b>Your Login details for Cafe Management System</b><br><b>Email: </b> " + to + " <br><b>OTP: </b> " + otp + "<br><a href=\"http://localhost:4200/\">Use this OTP to reset your password</a></p>";

		message.setContent(htmlMsg, "text/html");
		emailSender.send(message);
	}
}
