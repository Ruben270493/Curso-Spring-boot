package com.bolsadeideas.springboot.web.app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app")
public class IndexController {
	
	/* Existen distintas anotaciones para crear rutas: RequestMapping, GetMapping y PostMapping.
	*  RequestMapping: Sirve tanto para GET cómo para POST, el method se puede especificar con el parámetro "method", pero si se omite por defecto es GET.
	*  GetMapping: Funciona igual que el RequestMapping pero siempre utiliza el método GET.
	*  PostMapping: Funciona igual que el GetMapping pero siempre utilizando el método POST. */
	@GetMapping({"/index","/","/home"})
	public String index(Model model) {
		// Envío de parámetros a la vista (Variable ${titulo}).
		model.addAttribute("titulo","¡Hola Spring Framework!");
		return "index";
	}
	
}
