package com.example.demo.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.auth.AuthHelper;
import com.example.demo.patient.Appointment;
import com.example.demo.patient.AppointmentRepository;
import com.example.demo.patient.Patient;
import com.example.demo.patient.PatientRecord;
import com.example.demo.patient.PatientRecordRepository;
import com.example.demo.patient.PatientRepository;
import com.example.demo.user.Role;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AdminController {

	@Autowired
	private AuthHelper authHelper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private PatientRecordRepository patientRecordRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@GetMapping("/admin/users")
	public String manageUsers(HttpServletRequest request, Model model) {
		if (!authHelper.hasRole(request, Role.ADMIN)) {
			return "redirect:/admin/login";
		}

		User currentUser = authHelper.getCurrentUser(request);
		List<User> doctors = userRepository.findByRole(Role.DOCTOR);
		List<User> nurses = userRepository.findByRole(Role.NURSE);

		model.addAttribute("user", currentUser);
		model.addAttribute("doctors", doctors);
		model.addAttribute("nurses", nurses);
		return "admin/users.html";
	}

	@PostMapping("/admin/users/{userId}/approve")
	public String approveUser(@PathVariable int userId, HttpServletRequest request) {
		if (!authHelper.hasRole(request, Role.ADMIN)) {
			return "redirect:/admin/login";
		}

		User currentUser = authHelper.getCurrentUser(request);

		User user = userRepository.findById(userId).orElse(null);
		if (user != null) {
			user.setActive(true);
			userRepository.save(user);
		}
		return "redirect:/admin/users";
	}

	@PostMapping("/admin/users/{userId}/block")
	public String blockUser(@PathVariable int userId, HttpServletRequest request) {
		if (!authHelper.hasRole(request, Role.ADMIN)) {
			return "redirect:/admin/login";
		}

		User currentUser = authHelper.getCurrentUser(request);

		User user = userRepository.findById(userId).orElse(null);
		if (user != null) {
			user.setActive(false);
			userRepository.save(user);
		}
		return "redirect:/admin/users";
	}

	@GetMapping("/admin/patients")
	public String viewAllPatients(HttpServletRequest request, Model model,
			@RequestParam(required = false) String search) {
		if (!authHelper.hasRole(request, Role.ADMIN)) {
			return "redirect:/admin/login";
		}

		User currentUser = authHelper.getCurrentUser(request);

		List<Patient> patients;
		if (search != null && !search.isBlank()) {
			patients = patientRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(search,
					search);
		} else {
			patients = patientRepository.findAll();
		}

		model.addAttribute("user", currentUser);
		model.addAttribute("patients", patients);
		model.addAttribute("search", search);
		return "admin/patients.html";
	}

	@GetMapping("/admin/records")
	public String viewAllRecords(HttpServletRequest request, Model model) {
		if (!authHelper.hasRole(request, Role.ADMIN)) {
			return "redirect:/admin/login";
		}

		User currentUser = authHelper.getCurrentUser(request);

		List<PatientRecord> records = patientRecordRepository.findAll();
		model.addAttribute("user", currentUser);
		model.addAttribute("records", records);
		return "admin/records.html";
	}

	@GetMapping("/admin/appointments")
	public String viewAllAppointments(HttpServletRequest request, Model model) {
		if (!authHelper.hasRole(request, Role.ADMIN)) {
			return "redirect:/admin/login";
		}

		User currentUser = authHelper.getCurrentUser(request);

		List<Appointment> appointments = appointmentRepository.findAll();
		model.addAttribute("user", currentUser);
		model.addAttribute("appointments", appointments);
		return "admin/appointments.html";
	}

}

