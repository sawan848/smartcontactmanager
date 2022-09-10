package com.smartcontactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smartcontactmanager.helper.Messages;
import com.smartcontactmanager.model.Contact;
import com.smartcontactmanager.model.User;
import com.smartcontactmanager.repository.ContactRepository;
import com.smartcontactmanager.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserConroller {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	
	//create order for payment
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder() {
		System.out.println("order function executed");
		return "done";
	}
	
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		String userName=principal.getName();
		//get the user using username
		User user=userRepository.getUserByUserName(userName);
		System.out.println("name: "+user.getName());
		System.out.println("username: "+userName);
		
		model.addAttribute("user", user);
	}
	
	@RequestMapping("/index")
	public String dashBoard(Model model,Principal principal) {

		model.addAttribute("title","User Dashboard");
		return "normal/user_dashboard";
	}
	
	
	
	//open add form handler	
	@GetMapping("/add_contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title","Add Contact");
		model.addAttribute("contact",new Contact());
		return "normal/add_contact";
	}
	
	@PostMapping("/process_contact")
	public String processContact(
			@ModelAttribute Contact contact,
			@RequestParam("img") MultipartFile multipartFile,
			Principal principal,HttpSession session
		
			) {
		
		try {
				String name=principal.getName();
				User user=userRepository.getUserByUserName(name);
				contact.setUser(user);
				user.getContacts().add(contact);

				contact.setImgUrl(multipartFile.getOriginalFilename());
				
				userRepository.save(user);
				System.out.println("added to data base");
				System.out.println("data: "+contact);
				
				// success message 
				session.setAttribute("message", 
						new Messages("Your contact is added !! Add more.. ",  "success"));
				
				
			
			//img 
			if (multipartFile.isEmpty()) {
				System.out.println("file is empty");
				contact.setImgUrl("/static/img/contact.png");
			}
				else {
			
				File file=new ClassPathResource("static/img").getFile();
				Path path=Paths.get(file.getAbsolutePath()+File.separator+multipartFile.getOriginalFilename());
				Files.copy(multipartFile.getInputStream(),path ,StandardCopyOption.REPLACE_EXISTING);
				System.out.println("file uploaded");
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			// success error 
			session.setAttribute("message", 
					new Messages("something wrong  !! Try Again ",  "danger"));
			
			
		}
		
		
		return "normal/add_contact";
	}
	
	@GetMapping("/show_contacts/{page}")
	//perpage 
	public String showContacts(@PathVariable("page") int page,Model model,Principal principal) {
		
		String userName=principal.getName();
		User user=userRepository.getUserByUserName(userName);
		Pageable pageable=PageRequest.of(page, 2);
		
		Page<Contact>contacts=contactRepository.
				findContactsByUser(user.getId(),pageable);
		
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
//
//		String usernname=principal.getName();
//		User user=userRepository.getUserByUserName(usernname);
//		List<Contact> contacts=user.getContacts();
		
		model.addAttribute("title","Show Users Contacts");
		return "normal/show_contacts";
	}
	
	
	//show particular contact details
	@RequestMapping("/{cId}/contact_details")
	public String showContactDetails(
			@PathVariable("cId") int cid,
			Model model,
			Principal principal) throws Exception {
		
		Optional<Contact>contactOptional=contactRepository.findById(cid);
		Contact contact=contactOptional.get();
		
		String userName=principal.getName();
		 User user=userRepository.getUserByUserName(userName);
		 
		if (user.getId()==contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title",contact.getName());
		}else {
			throw new Exception("error");
		}
		
		return "normal/contact_details";
	}
	
	//delete  contact handle
	@GetMapping("/delete/{cId}")
	public String deleteContact(
			@PathVariable("cId")int cId,
			Model model,
			HttpSession session,
			Principal principal) {
		
		Contact contact=contactRepository.findById(cId).get();
		//check ....assignment ..image delete
		
		//delete old photos
		
		//check user available or not
		
		//delete
//		contact.setUser(null);
//		contactRepository.delete(contact);

		User user=userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		userRepository.save(user);
		
		
		System.out.println("delete");
		session.setAttribute("message", new Messages("contact deleted successfully","success"));
		
		return "redirect:/user/show_contacts/0";
		
	}

	
	@PostMapping("/update_contact/{cId}")
	public String updateForm(@PathVariable("cId") int cId,Model model) {
		model.addAttribute("title","Update Contact" );
		
		Contact contact=contactRepository.findById(cId).get();
		model.addAttribute("contact", contact);
		
		return "normal/update_contact";
	}
	
	@PostMapping("/contact_update")
	public String updateHandler(
				@ModelAttribute Contact contact,	
				@RequestParam("img") MultipartFile multipartFile,
				Model model,
				HttpSession session,
				Principal principal) {
		
		try {
			//old contact details
			Contact oldContact=contactRepository.findById(contact.getCId()).get();
			if (!multipartFile.isEmpty()) {
				
				//file work
				//rewrite
				//delete old photo

				File deleteFile=new ClassPathResource("static/img").getFile();
				File file1=new File(deleteFile,oldContact.getImgUrl());
				file1.delete();
				
				//update new photo

				File file=new ClassPathResource("static/img").getFile();
				Path path=Paths.get(file.getAbsolutePath()+File.separator+multipartFile.getOriginalFilename());
				Files.copy(multipartFile.getInputStream(),path ,StandardCopyOption.REPLACE_EXISTING);
				contact.setImgUrl(multipartFile.getOriginalFilename());
			
			}else {
				contact.setImgUrl(oldContact.getImgUrl());
			}
			
			User user=userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			contactRepository.save(contact);

			session.setAttribute("message", new Messages("contact updated successfully","success"));
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		System.out.println("Contact Name "+contact.getName());
		System.out.println("Contact Id "+contact.getCId());
		System.out.println("Contact Id "+multipartFile.getOriginalFilename());
		
		return "redirect:/user/"+contact.getCId()+"/contact_details";
	}
	
	@GetMapping("/profile")
	public String profileHandler(Model model) {
		model.addAttribute("title","profile");
		return "normal/profile";
	}
	
	@GetMapping("/settings")
	public String openSetting(Model model) {
		model.addAttribute("title", "Settings");
		return "normal/settings";
	}
	
	@PostMapping("/change_password")
	public String  changePassword(
				   @RequestParam("oldPassword")String oldPassword,
				   @RequestParam("newPassword")String newPassword ,
				   Principal principal,
				   HttpSession session) {
		
		
		System.out.println("old password: "+ oldPassword);
		System.out.println("new password: "+ newPassword);
		
		String userName=principal.getName();
		User cureentUser=userRepository.getUserByUserName(userName);
		System.out.println(cureentUser.getPassword());
		
		
		if (passwordEncoder.matches(oldPassword,cureentUser.getPassword() )) {
			
			//change the password
			cureentUser.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(cureentUser);

			session.setAttribute("message", new Messages("password updated successfully","success"));
		}else {

			session.setAttribute("message", new Messages("please enter corrent old password ","danger"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/user/index";
	}
	
}
