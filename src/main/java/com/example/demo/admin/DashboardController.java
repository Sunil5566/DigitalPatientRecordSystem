package com.example.demo.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.auth.Authentication;
import com.example.demo.user.User;
import com.example.demo.user.UserType;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class DashboardController {
	
	@Autowired
	private Authentication authentication;
	
	@GetMapping("/dashboard")
	public String getDashboard(HttpServletRequest request, Model model) {

		User user = authentication.authenticate(request);
		
		if (user == null) {
			return "redirect:/login";
		}

		model.addAttribute("user", user);

		
		switch (user.getType()) {
			case UserType.DOCTOR:
				return "doctor_dashboard.html";

			case UserType.NURSE:
				return "nurse_dashboard.html";

			case  UserType.ADMIN:
				return "admin_dashboard.html";  // your admin dashboard

			default:
				return "redirect:/login"; // OR error page
		}
	}
}
