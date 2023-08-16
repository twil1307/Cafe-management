package com.system.cfmanage.JWT;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtUtil {
	
	@Value("${project.SECRET_KEY}")
	private String secret;
	
	public String extractUsername(String token) {
//		String userName = extractAllClaims(token).getSubject();
//		
//		System.out.println(userName);
		
//		Pass token and a function aiming to get Subject (Username) to the extractClaims
		return extractClaims(token, new Function<Claims, String>() {
			@Override
			public String apply(Claims claims) {
				return claims.getSubject();
			}
		});
		
		
	}
	
//	Get the expiration time from the token
	public Date extractExpiration(String token) {
		return extractClaims(token, Claims::getExpiration);
	}
	
//	Check if is token expired
	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
//	Validate if the token is available or not
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (userDetails.getUsername().equals(username) && isTokenExpired(token));
	}
	
	private String createToken(Map<String, Object> claims, String subject) {
		System.out.println(secret);
		String encodedString = Base64.getEncoder().encodeToString(secret.getBytes());
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) //10 hours
				.signWith(SignatureAlgorithm.HS256, encodedString).compact();
	}
	
	public String generateToken(String username, String role) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", role);
		return createToken(claims, username);
	}
	
//	Extract specific Claims from the JWT by passing parameters
	public <T> T extractClaims(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		
		return claimResolver.apply(claims);
	}
	
//	Get all claims from token
	public Claims extractAllClaims(String token) {
		String encodedString = Base64.getEncoder().encodeToString(secret.getBytes());
		return Jwts.parser().setSigningKey(encodedString).parseClaimsJws(token).getBody();
	}
	
}
