package com.system.cfmanage.JWT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.system.cfmanage.constants.CafeConstants;
import com.system.cfmanage.utils.CafeUtils;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private CustomerUsersDetailsSevice service;
	
	Claims claims = null;
	
	private String userName = null;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if(request.getServletPath().matches("/user/login|/user/forgotPassword|/user/signup")) {
			filterChain.doFilter(request, response);
		} else {
//			String token = request.getHeader(AUTHORIZATION).substring("Bearer".length());
			String authorizationHeader = request.getHeader(AUTHORIZATION);
			String token = null;
			String role = null;
			
//			take the username and all claims out of the token
			if(authorizationHeader!=null && authorizationHeader.startsWith("Bearer ")) {
				token = authorizationHeader.substring("Bearer ".length());
				userName = jwtUtil.extractUsername(token);
				claims = jwtUtil.extractAllClaims(token);
				role = (String) claims.get("role");
			}
			
			if(userName!=null && SecurityContextHolder.getContext().getAuthentication()==null) {
				UserDetails userDetails = service.loadUserByUsername(userName);
				
				if(userDetails == null) {
					response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	                response.getWriter().write("User not found");
	                return;
				}
				
				Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
				
				authorities.add(new SimpleGrantedAuthority(role));
				
				if(jwtUtil.validateToken(token, userDetails)!=null) {
					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = 
										new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
					
					usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					
					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
				}
			}
			filterChain.doFilter(request, response);
		}
		
	}
	
	public boolean isAdmin() {
		return "admin".equalsIgnoreCase((String) claims.get("role"));
	}

	public boolean isUser() {
		return "user".equalsIgnoreCase((String) claims.get("role"));
	}
	
	public String getCurrentUserId() {
		System.out.println(claims);
		return (String) claims.get("id");
	}
	
	public String getCurrentUser() {
		return userName;
	}
}
