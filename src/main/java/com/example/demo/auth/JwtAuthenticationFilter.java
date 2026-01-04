package com.example.demo.auth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserRepository userRepository;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = extractTokenFromRequest(request);

		if (token != null && jwtService.validateToken(token)) {
			try {
				String username = jwtService.extractUsername(token);
				User user = userRepository.findByUsername(username).orElse(null);

				if (user != null && user.getActive()) {
					request.setAttribute("currentUser", user);
				}
			} catch (Exception e) {
				// Invalid token, continue without authentication
			}
		}

		filterChain.doFilter(request, response);
	}

	private String extractTokenFromRequest(HttpServletRequest request) {
		// Try to get token from cookie
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("JWT_TOKEN".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}

		// Try to get token from Authorization header
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}

		return null;
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return path.startsWith("/css/") || 
			   path.startsWith("/js/") || 
			   path.startsWith("/images/") ||
			   path.equals("/") ||
			   path.startsWith("/admin/login") ||
			   path.startsWith("/doctor/login") ||
			   path.startsWith("/nurse/login");
	}

}

