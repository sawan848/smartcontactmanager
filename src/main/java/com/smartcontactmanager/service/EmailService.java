package com.smartcontactmanager.service;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {
	
	public boolean sendEmail(String subject,String message,String to) {
		
		boolean flag=false;
		String from="ksawan802@gmail.com";
		String password="SpringBoot48";
		String host="smtp.gmail.com";
		Properties properties=System.getProperties();
		System.out.println("Properties "+properties);
		
		properties.put("mail.smtp.host",host);
		properties.put("mail.smtp.port","465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		Session session=Session.getInstance(properties,new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		});
		session.setDebug(true);
		MimeMessage message2=new MimeMessage(session);
		try {
			message2.setFrom(from);
			message2.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
			message2.setSubject(subject);
//			message2.setText(message);
			message2.setContent(message,"text/html");
			
			Transport.send(message2);
			System.out.println("sent successfull");
			flag=true;
		} catch (Exception e) {
		e.printStackTrace();
		}
		return flag;
	}

}
