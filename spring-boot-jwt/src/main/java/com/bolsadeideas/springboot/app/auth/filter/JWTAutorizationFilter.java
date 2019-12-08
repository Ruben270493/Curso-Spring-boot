package com.bolsadeideas.springboot.app.auth.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

public class JWTAutorizationFilter extends BasicAuthenticationFilter {

	public JWTAutorizationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String header = request.getHeader("Authorization");
		
		if (!requiresAuthentication(header)) {
			chain.doFilter(request, response);
			return;
		}
		
	}
	
	protected boolean requiresAuthentication(String header) {
		
		if (header == null || !header.startsWith("Bearer ")) {
			return false;
		} else {
			return true;
		}
		
	}

}