package com.bolsadeideas.springboot.reactor.app;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.bolsadeideas.springboot.reactor.app.models.Usuario;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootReactorApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringBootReactorApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootReactorApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		List<String> usuariosList = new ArrayList<>();
		
		usuariosList.add("Rubén Fernández");
		usuariosList.add("Andrés Guzm");
		usuariosList.add("Trang Bu");
		usuariosList.add("Juan Villa");
		usuariosList.add("Bruce Lee");
		usuariosList.add("Bruce Willis");
		
		Flux<String> nombres = Flux.fromIterable(usuariosList);
		//Flux<String> nombres = Flux.just("Rubén Fernández", "Andrés Guzm", "Trang Bu", "Juan Villa", "Bruce Lee", "Bruce Willis");
		
		Flux<Usuario> usuarios = nombres.map(nombre -> new Usuario(nombre.split(" ")[0].toUpperCase(), nombre.split(" ")[1].toUpperCase()))
			.filter(usuario -> usuario.getNombre().toLowerCase().equals("bruce"))
			.doOnNext(usuario -> {
				if (null == usuario)
					throw new RuntimeException("Los nombres no pueden estar vacíos.");
				else
					System.out.println(usuario.getNombre() + " " + usuario.getApellido());
			}).map(usuario -> {
					String nombre = usuario.getNombre().toLowerCase();
					usuario.setNombre(nombre);
					return usuario;
				});
		
		usuarios.subscribe(e -> log.info(e.toString()), 
				error -> log.error(error.getMessage()),
				new Runnable() {
					@Override
					public void run() {
						log.info("¡Ha finalizado la ejecución del observable con éxito!");						
					}
				});
		
	}

}
