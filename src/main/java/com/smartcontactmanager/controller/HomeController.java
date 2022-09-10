package com.smartcontactmanager.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smartcontactmanager.helper.Messages;
import com.smartcontactmanager.model.User;
import com.smartcontactmanager.repository.UserRepository;

@Controller
public class HomeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;

    @RequestMapping("/")
    public String home(Model model){
        model.addAttribute("title","Home-Smart Contact Canager");
        return "home";
    }

    @RequestMapping("/about")
    public String about(Model model){
        model.addAttribute("title","About-Smart Contact Canager");
        return "about";
    }
    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("title","Login-Smart Contact Canager");
       
        return "login";
    }
    
    @RequestMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title","Register-Smart Contact Canager"); 
        model.addAttribute("user",new User());
        return "signup";
    }
    
    @RequestMapping(value = "/do-register",method = RequestMethod.POST)
    public String registerUser(@Valid
    		@ModelAttribute("user") User user, BindingResult result,
             @RequestParam(value = "agreement",defaultValue ="false")
              boolean agreement,
             
              Model model,
              HttpSession session) {
    		
    	try {
    	 	
        	if (!agreement) {
    			System.out.println("you not agreed the terms and condition");
    			throw new Exception("you not agreed the terms and condition");
    		}

        	if (result.hasErrors()){
                System.out.println("Error"+result.toString());
        	    model.addAttribute("user",user);
        	    return "signup";
            }

        	user.setRole("ROLE_USER");
        	user.setEnabled(true);
        	user.setImgUrl("default.png");
        	user.setPassword(passwordEncoder.encode(user.getPassword()));
        	
        	System.out.println("Aggrement "+agreement);
        	System.out.println("user"+user.toString());
        	
        	User result2=userRepository.save(user);
        	
            model.addAttribute("user", new User());
			session.setAttribute("message", 
					new Messages("successfully register","alert-success"));
			return "signup";
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", 
					new Messages("something went wrong"+e.getMessage(),"alert-danger"));
			return "signup";
		}
    
    	
    }
    
    
  
}
