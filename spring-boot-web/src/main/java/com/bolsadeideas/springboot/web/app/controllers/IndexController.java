package com.bolsadeideas.springboot.web.app.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.bolsadeideas.springboot.web.app.models.Usuario;

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

	@GetMapping("/profile")
	public String profile(Model model) {
		Usuario usuario = new Usuario();
		usuario.setNombre("Andrés");
		usuario.setApellido("Apellido");
		usuario.setEmail("user@email.com");
		model.addAttribute("usuario", usuario);
		model.addAttribute("titulo", "Perfil del usuario: "+usuario.getNombre());
		return "profile";
	}
	
	@GetMapping("/listar")
	public String listar(Model model) {
		List<Usuario> usuarios = Arrays.asList(
			new Usuario("Andrés","Guzman","andres@correo.com"),
			new Usuario("Rubén","Fernández","ruben@correo.com"),
			new Usuario("Jane","Doe","jane@correo.com"),
			new Usuario("Anonimo","Doe","anonimo@correo.com")
		);
		model.addAttribute("titulo", "Listado de usuarios");
		model.addAttribute("usuarios", usuarios);
		return "listar";
	}
}