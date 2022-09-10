package com.smartcontactmanager.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontactmanager.model.User;
import com.smartcontactmanager.repository.UserRepository;
import com.smartcontactmanager.service.EmailService;

@Controller
public class ForgotController {
	
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	Random random=new Random(1000);
	
	@RequestMapping("/forgot")
	public String openEmailForm() {
		return "normal/forgot_email";
	}

	
	@PostMapping("/send_otp")
	public String sebdOTP(@RequestParam("email") String email,HttpSession session ) {
		System.out.println("Eamil: "+email);
		
		int otp=random.nextInt(99999);
		System.out.println("OTP "+otp);
		String subject="OTP from SCM";
		String message=""
				+ "<div style='border:1px solid #e2e2e2; padding:20px;color:#7B1FA2;'>"
				+ "<h1>"
				+" OTP is "
				+"<b>"	+otp
				+"</b>"
				+"</h1>"
				+ "</div>";
		String to=email;
		boolean flag=emailService.sendEmail(subject,message, to);
		if (flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
		
			return "normal/verify_otp";
		}else {

			session.setAttribute("message","check your email id !!");
			return "normal/forgot_email";
		}
	
	}
	
	//verify otp
	@PostMapping("/verify_otp")
	public String verifyOTP(@RequestParam("otp")int otp,HttpSession session) {
		int myOTP=(int)(session.getAttribute("myotp"));
		String email=(String)session.getAttribute("email");
		if (myOTP==otp) {
			//password change
			User user=userRepository.getUserByUserName(email);
			if (user==null) {
				//send error message
				session.setAttribute("message","user does not exit with this email id");
				return "normal/forgot_email";
				
			}else {
				//send change password form
				return "normal/password_change";
			}
		}else {		

			session.setAttribute("message","wrong enetered otp");
			return "normal/verify_otp";
		}
		
	}
	
	@PostMapping("/change_password")
	public String changePassword(@RequestParam("newPassword") String newPassword,HttpSession session) {
		String email=(String)session.getAttribute("email");
		User user=userRepository.getUserByUserName(email);
		user.setPassword(passwordEncoder.encode(newPassword));
		userRepository.save(user);
		session.setAttribute("", user);
		return "redirect:/login?change=password change successfully";
	}
	
	
	

}
