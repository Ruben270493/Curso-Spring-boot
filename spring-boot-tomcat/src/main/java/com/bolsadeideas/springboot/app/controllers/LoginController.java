package com.bolsadeideas.springboot.app.controllers;

import java.security.Principal;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

	@Autowired
	private MessageSource messageSource;
	
	@GetMapping("/login")
	public String Login(@RequestParam(value = "logout", required = false) String logout, @RequestParam(value = "error", required = false) String error, Model model, Principal principal, Locale locale) {
		
		if (principal != null)
			return "redirect:/";
		
		if (null != error)
			model.addAttribute("error", messageSource.getMessage("text.login.error", null, locale));
		
		if (null != logout)
			model.addAttribute("info", messageSource.getMessage("text.login.info", null, locale));
		
		return "login";
		
	}
	
}
