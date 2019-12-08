package com.bolsadeideas.springboot.app.auth.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.bolsadeideas.springboot.app.auth.SimpleGrantedAuthorityMixin;
import com.bolsadeideas.springboot.app.auth.filter.JWTAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

@Service
public class JWTServiceImpl implements JWTService {

	@Override
	public String create(Authentication auth) throws IOException {
		
		String username = auth.getName();
		Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
		
		Claims claims = Jwts.claims();
		claims.put("authorities", new ObjectMapper().writeValueAsString(roles));
		
        String token = Jwts.builder()
        				.setClaims(claims)
                        .setSubject(username)
                        .signWith(JWTAuthenticationFilter.SECRET_KEY)
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 14000000L))
                        .compact();
		
		return token;
	}

	@Override
	public boolean validate(String token) {
		
		try {
			getClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			return false;
		}
	
	}

	@Override
	public Claims getClaims(String token) {
		
		Claims claims = Jwts.parser()
				.setSigningKey(JWTAuthenticationFilter.SECRET_KEY)
				.parseClaimsJws(resolve(token))
				.getBody();
		
		return claims;
		
	}

	@Override
	public String getUsername(String token) {
		return getClaims(token).getSubject();
	}

	@Override
	public Collection<? extends GrantedAuthority> getRoles(String token) throws IOException {
		
		Object roles = getClaims(token).get("authorities");
		Collection<? extends GrantedAuthority> authorities = Arrays.asList(new ObjectMapper()
				.addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityMixin.class)
				.readValue(roles.toString().getBytes(), SimpleGrantedAuthority[].class));
		
		return authorities;
		
	}

	@Override
	public String resolve(String token) {
		
		if (null != token && token.startsWith("Bearer "))
			return token.replace("Bearer ", "");
		else
			return null;
		
	}

}
