package com.example.demo.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.ValidationError;
import com.example.demo.Utilities;
import com.example.demo.user.Role;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthHelper authHelper;

	// Landing page - role selection
	@GetMapping("/")
	public String landingPage() {
		return "index.html";
	}

	// Admin Login
	@GetMapping("/admin/login")
	public String getAdminLoginPage(Model model) {
		model.addAttribute("error", new ValidationError());
		model.addAttribute("form", new LoginForm());
		return "admin/login.html";
	}

	@PostMapping("/admin/login")
	public String adminLogin(LoginForm form, Model model, HttpServletResponse response) {
		return processLogin(form, model, response, Role.ADMIN, "/admin/dashboard");
	}

	// Doctor Login
	@GetMapping("/doctor/login")
	public String getDoctorLoginPage(Model model) {
		model.addAttribute("error", new ValidationError());
		model.addAttribute("form", new LoginForm());
		return "doctor/login.html";
	}

	@PostMapping("/doctor/login")
	public String doctorLogin(LoginForm form, Model model, HttpServletResponse response) {
		return processLogin(form, model, response, Role.DOCTOR, "/doctor/dashboard");
	}

	// Nurse Login
	@GetMapping("/nurse/login")
	public String getNurseLoginPage(Model model) {
		model.addAttribute("error", new ValidationError());
		model.addAttribute("form", new LoginForm());
		return "nurse/login.html";
	}

	@PostMapping("/nurse/login")
	public String nurseLogin(LoginForm form, Model model, HttpServletResponse response) {
		return processLogin(form, model, response, Role.NURSE, "/nurse/dashboard");
	}

	private String processLogin(LoginForm form, Model model, HttpServletResponse response, Role requiredRole,
			String successRedirect) {
		// Find user by email
		User user = userRepository.findByEmail(form.getEmail()).orElse(null);

		// Validate credentials
		if (user == null || !passwordEncoder.matches(form.getPassword(), user.getPassword())) {
			model.addAttribute("error", new ValidationError("Invalid credentials!"));
			model.addAttribute("form", form);
			return getLoginPageForRole(requiredRole);
		}

		// Check if user is active
		if (!user.getActive()) {
			model.addAttribute("error", new ValidationError("Your account has been deactivated. Please contact administrator."));
			model.addAttribute("form", form);
			return getLoginPageForRole(requiredRole);
		}

		// Check if role matches
		if (user.getRole() != requiredRole) {
			model.addAttribute("error", new ValidationError("Invalid role. Please login from the correct portal."));
			model.addAttribute("form", form);
			return getLoginPageForRole(requiredRole);
		}

		// Generate JWT token
		String token = jwtService.generateToken(user);

		// Set JWT token in cookie
		Cookie cookie = new Cookie("JWT_TOKEN", token);
		cookie.setPath("/");
		cookie.setHttpOnly(true);
		cookie.setMaxAge(86400); // 24 hours
		response.addCookie(cookie);

		// Redirect to appropriate dashboard
		return "redirect:" + successRedirect;
	}

	private String getLoginPageForRole(Role role) {
		switch (role) {
		case ADMIN:
			return "admin/login.html";
		case DOCTOR:
			return "doctor/login.html";
		case NURSE:
			return "nurse/login.html";
		default:
			return "index.html";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpServletResponse response) {
		// Clear JWT token cookie
		Cookie cookie = new Cookie("JWT_TOKEN", "");
		cookie.setPath("/");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		return "redirect:/";
	}

	// Admin-only registration
	@GetMapping("/admin/register")
	public String getRegisterPage(jakarta.servlet.http.HttpServletRequest request, Model model) {
		if (!authHelper.hasRole(request, Role.ADMIN)) {
			return "redirect:/admin/login";
		}

		model.addAttribute("error", new ValidationError());
		model.addAttribute("registerForm", new RegistrationForm());
		return "admin/register.html";
	}

	@PostMapping("/admin/register")
	public String registerUser(RegistrationForm form, Model model, jakarta.servlet.http.HttpServletRequest request) {
		if (!authHelper.hasRole(request, Role.ADMIN)) {
			return "redirect:/admin/login";
		}

		// Validation
		if (form.getFirstName() == null || form.getFirstName().isBlank()) {
			model.addAttribute("error", new ValidationError("First name cannot be empty"));
			model.addAttribute("registerForm", form);
			return "admin/register.html";
		}

		if (form.getUserType() == null || form.getUserType().isBlank()) {
			model.addAttribute("error", new ValidationError("User type must be selected (DOCTOR or NURSE)"));
			model.addAttribute("registerForm", form);
			return "admin/register.html";
		}

		if (!form.getUserType().equals("DOCTOR") && !form.getUserType().equals("NURSE")) {
			model.addAttribute("error", new ValidationError("User type must be DOCTOR or NURSE"));
			model.addAttribute("registerForm", form);
			return "admin/register.html";
		}

		if (!Utilities.isValidPassword(form.getPassword())) {
			model.addAttribute("error", new ValidationError(
					"Password should be at least 8 characters long and should contain at least one number and at least one uppercase letter"));
			model.addAttribute("registerForm", form);
			return "admin/register.html";
		}

		if (!form.getPassword().equals(form.getConfirmPassword())) {
			model.addAttribute("error", new ValidationError("Passwords do not match"));
			model.addAttribute("registerForm", form);
			return "admin/register.html";
		}

		if (userRepository.existsByEmail(form.getEmail())) {
			model.addAttribute("error", new ValidationError("Email already exists"));
			model.addAttribute("registerForm", form);
			return "admin/register.html";
		}

		// Create user
		User user = new User();
		user.setFirstName(form.getFirstName());
		user.setLastName(form.getLastName() != null ? form.getLastName() : "");
		user.setEmail(form.getEmail());
		user.setUsername(form.getEmail()); // Use email as username
		user.setPassword(passwordEncoder.encode(form.getPassword()));
		user.setRole(Role.valueOf(form.getUserType()));
		user.setActive(true); // Admin creates active users

		userRepository.save(user);

		return "redirect:/admin/users";
	}

}
