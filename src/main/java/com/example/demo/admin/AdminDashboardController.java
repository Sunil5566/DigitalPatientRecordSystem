package com.example.demo.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.auth.AuthHelper;
import com.example.demo.user.Role;
import com.example.demo.user.User;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AdminDashboardController {
	
	@Autowired
	private AuthHelper authHelper;
	
	@GetMapping("/admin/dashboard")
	public String getAdminDashboard(HttpServletRequest request, Model model) {
		if (!authHelper.hasRole(request, Role.ADMIN)) {
			return "redirect:/admin/login";
		}

		User user = authHelper.getCurrentUser(request);
		model.addAttribute("user", user);
		return "admin_dashboard.html";
	}
}

