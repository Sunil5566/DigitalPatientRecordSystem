package com.example.demo.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.user.Role;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AuthHelper {

	@Autowired
	private JwtService jwtService;

	@Autowired
	private UserRepository userRepository;

	public User getCurrentUser(HttpServletRequest request) {
		User user = (User) request.getAttribute("currentUser");
		if (user != null) {
			// Refresh user from database to get latest data
			return userRepository.findById(user.getId()).orElse(null);
		}
		return null;
	}

	public boolean isAuthenticated(HttpServletRequest request) {
		return getCurrentUser(request) != null;
	}

	public boolean hasRole(HttpServletRequest request, Role requiredRole) {
		User user = getCurrentUser(request);
		return user != null && user.getRole() == requiredRole && user.getActive();
	}

	public String getJwtToken(HttpServletRequest request) {
		jakarta.servlet.http.Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (jakarta.servlet.http.Cookie cookie : cookies) {
				if ("JWT_TOKEN".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

}

