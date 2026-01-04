package com.example.demo.nurse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.auth.AuthHelper;
import com.example.demo.user.Role;
import com.example.demo.user.User;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class NurseDashboardController {
	
	@Autowired
	private AuthHelper authHelper;
	
	@GetMapping("/nurse/dashboard")
	public String getNurseDashboard(HttpServletRequest request, Model model) {
		if (!authHelper.hasRole(request, Role.NURSE)) {
			return "redirect:/nurse/login";
		}

		User user = authHelper.getCurrentUser(request);
		model.addAttribute("user", user);
		return "nurse_dashboard.html";
	}
}

