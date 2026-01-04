package com.example.demo.doctor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.auth.AuthHelper;
import com.example.demo.user.Role;
import com.example.demo.user.User;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class DoctorDashboardController {
	
	@Autowired
	private AuthHelper authHelper;
	
	@GetMapping("/doctor/dashboard")
	public String getDoctorDashboard(HttpServletRequest request, Model model) {
		if (!authHelper.hasRole(request, Role.DOCTOR)) {
			return "redirect:/doctor/login";
		}

		User user = authHelper.getCurrentUser(request);
		model.addAttribute("user", user);
		return "doctor_dashboard.html";
	}
}

