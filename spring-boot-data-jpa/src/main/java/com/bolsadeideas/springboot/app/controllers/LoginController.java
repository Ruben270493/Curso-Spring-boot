package com.bolsadeideas.springboot.app.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

	@GetMapping("/login")
	public String Login(@RequestParam(value = "error", required = false) String error, Model model, Principal principal) {
		
		if (principal != null)
			return "redirect:/";
		
		if (error != null)
			model.addAttribute("error", "El usuario o la contraseña es incorrecta.");
		
		return "login";
		
	}
	
}
