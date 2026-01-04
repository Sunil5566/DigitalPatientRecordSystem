package com.example.demo.nurse;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.auth.AuthHelper;
import com.example.demo.patient.Patient;
import com.example.demo.patient.PatientRepository;
import com.example.demo.patient.Vitals;
import com.example.demo.patient.VitalsRepository;
import com.example.demo.user.Role;
import com.example.demo.user.User;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class NurseController {

	@Autowired
	private AuthHelper authHelper;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private VitalsRepository vitalsRepository;

	@GetMapping("/nurse/patients")
	public String viewPatients(HttpServletRequest request, Model model, @RequestParam(required = false) String search) {
		if (!authHelper.hasRole(request, Role.NURSE)) {
			return "redirect:/nurse/login";
		}

		User nurse = authHelper.getCurrentUser(request);

		List<Patient> patients;
		if (search != null && !search.isBlank()) {
			patients = patientRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(search,
					search);
		} else {
			patients = patientRepository.findAll();
		}

		model.addAttribute("user", nurse);
		model.addAttribute("patients", patients);
		model.addAttribute("search", search);
		return "nurse/patients.html";
	}

	@GetMapping("/nurse/patient/{patientId}")
	public String viewPatientDetails(@PathVariable int patientId, HttpServletRequest request, Model model) {
		if (!authHelper.hasRole(request, Role.NURSE)) {
			return "redirect:/nurse/login";
		}

		User nurse = authHelper.getCurrentUser(request);

		Patient patient = patientRepository.findById(patientId).orElse(null);
		if (patient == null) {
			return "redirect:/nurse/patients";
		}

		List<Vitals> vitalsHistory = vitalsRepository.findByPatientOrderByCreatedAtDesc(patient);

		model.addAttribute("user", nurse);
		model.addAttribute("patient", patient);
		model.addAttribute("vitalsHistory", vitalsHistory);
		return "nurse/patient-details.html";
	}

	@GetMapping("/nurse/patient/{patientId}/vitals/add")
	public String getAddVitalsPage(@PathVariable int patientId, HttpServletRequest request, Model model) {
		if (!authHelper.hasRole(request, Role.NURSE)) {
			return "redirect:/nurse/login";
		}

		User nurse = authHelper.getCurrentUser(request);

		Patient patient = patientRepository.findById(patientId).orElse(null);
		if (patient == null) {
			return "redirect:/nurse/patients";
		}

		model.addAttribute("user", nurse);
		model.addAttribute("patient", patient);
		return "nurse/add-vitals.html";
	}

	@PostMapping("/nurse/patient/{patientId}/vitals/add")
	public String addVitals(@PathVariable int patientId, @RequestParam(required = false) Double bloodPressureSystolic,
			@RequestParam(required = false) Double bloodPressureDiastolic,
			@RequestParam(required = false) Double temperature, @RequestParam(required = false) Double weight,
			@RequestParam(required = false) Double height, @RequestParam(required = false) Double bloodSugar,
			@RequestParam(required = false) Integer heartRate,
			@RequestParam(required = false) Integer respiratoryRate, @RequestParam(required = false) String notes,
			HttpServletRequest request) {
		if (!authHelper.hasRole(request, Role.NURSE)) {
			return "redirect:/nurse/login";
		}

		User nurse = authHelper.getCurrentUser(request);

		Patient patient = patientRepository.findById(patientId).orElse(null);
		if (patient == null) {
			return "redirect:/nurse/patients";
		}

		Vitals vitals = new Vitals();
		vitals.setPatient(patient);
		vitals.setRecordedBy(nurse);
		vitals.setBloodPressureSystolic(bloodPressureSystolic);
		vitals.setBloodPressureDiastolic(bloodPressureDiastolic);
		vitals.setTemperature(temperature);
		vitals.setWeight(weight);
		vitals.setHeight(height);
		vitals.setBloodSugar(bloodSugar);
		vitals.setHeartRate(heartRate);
		vitals.setRespiratoryRate(respiratoryRate);
		vitals.setNotes(notes);

		vitalsRepository.save(vitals);
		return "redirect:/nurse/patient/" + patientId;
	}

	@GetMapping("/nurse/patient/add")
	public String getAddPatientPage(HttpServletRequest request, Model model) {
		if (!authHelper.hasRole(request, Role.NURSE)) {
			return "redirect:/nurse/login";
		}

		User nurse = authHelper.getCurrentUser(request);

		model.addAttribute("user", nurse);
		return "nurse/add-patient.html";
	}

	@PostMapping("/nurse/patient/add")
	public String addPatient(@RequestParam String firstName, @RequestParam(required = false) String lastName,
			@RequestParam String gender, @RequestParam int age, @RequestParam(required = false) String contact,
			@RequestParam(required = false) String address, HttpServletRequest request) {
		if (!authHelper.hasRole(request, Role.NURSE)) {
			return "redirect:/nurse/login";
		}

		User nurse = authHelper.getCurrentUser(request);

		Patient patient = new Patient();
		patient.setFirstName(firstName);
		patient.setLastName(lastName);
		patient.setGender(com.example.demo.user.Gender.valueOf(gender));
		patient.setAge(age);
		patient.setContact(contact);
		patient.setAddress(address);

		patientRepository.save(patient);
		return "redirect:/nurse/patients";
	}

}

