package com.example.demo.auth;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.user.Role;
import com.example.demo.user.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	@Value("${jwt.secret:DigitalPatientRecordSystemSecretKeyForJWTTokenGeneration2024}")
	private String secretKey;

	@Value("${jwt.expiration:86400000}") // 24 hours
	private Long expiration;

	private SecretKey getSigningKey() {
		// For jjwt 0.12.x, Keys.hmacShaKeyFor() automatically handles key length
		// It will use the key as-is if it's long enough, or derive a proper key
		return Keys.hmacShaKeyFor(secretKey.getBytes());
	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Role extractRole(String token) {
		Claims claims = extractAllClaims(token);
		String roleStr = claims.get("role", String.class);
		return Role.valueOf(roleStr);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public String generateToken(User user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("role", user.getRole().name());
		claims.put("userId", user.getId());
		return createToken(claims, user.getUsername());
	}

	private String createToken(Map<String, Object> claims, String subject) {
		return Jwts.builder()
				.claims(claims)
				.subject(subject)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + expiration))
				.signWith(getSigningKey())
				.compact();
	}

	public Boolean validateToken(String token, User user) {
		final String username = extractUsername(token);
		return (username.equals(user.getUsername()) && !isTokenExpired(token));
	}

	public Boolean validateToken(String token) {
		try {
			return !isTokenExpired(token);
		} catch (Exception e) {
			return false;
		}
	}

}

