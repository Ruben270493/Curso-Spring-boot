package com.bolsadeideas.springboot.app.auth.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.json.JsonParseException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.bolsadeideas.springboot.app.auth.service.JWTService;
import com.bolsadeideas.springboot.app.models.entity.Usuario;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.SignatureAlgorithm;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
	
	public static final SecretKeySpec SECRET_KEY = new SecretKeySpec("algunaLlaveSecreta.masfuerte.12345".getBytes(), SignatureAlgorithm.HS256.getJcaName());
	private AuthenticationManager authenticationManager;
	private JWTService jwtService;
	
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTService jwtService) {
		
		this.authenticationManager = authenticationManager;
		setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/api/login", "POST"));
		
		this.jwtService = jwtService;
		
	} 
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		
		String username = obtainUsername(request);
		String password = obtainPassword(request);
		
		if (username != null && password != null) {
		
			logger.info("Username desde request parameter (form-data): " + username);
			logger.info("Password desde request parameter (form-data): " + password);
			
		} else {
			
			Usuario user = null;
			
			try {
				
				user = new ObjectMapper().readValue(request.getInputStream(), Usuario.class);
				
				username = user.getUsername();
				password = user.getPassword();
			
				logger.info("Username desde request parameter (raw): " + username);
				logger.info("Password desde request parameter (raw): " + password);
				
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		}
		
		username = username.trim();
		
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
		
		return authenticationManager.authenticate(authToken);
		
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		String token = jwtService.create(authResult);
        
        response.addHeader("Authorization", "Bearer " + token);
        
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("token", token);
        body.put("user", (User) authResult.getPrincipal());
        body.put("mensaje", "Hola "+ authResult.getName() +", ¡Has iniciado sesión con éxito!");
        
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(200);
        response.setContentType("application/json");
		
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		
		Map<String, Object> body = new HashMap<String, Object>();
		
		body.put("mensaje", "Error de autenticación: username o password incorrecto.");
		body.put("error", failed.getMessage());
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        response.setStatus(401);
        response.setContentType("application/json");
        
	}

}
