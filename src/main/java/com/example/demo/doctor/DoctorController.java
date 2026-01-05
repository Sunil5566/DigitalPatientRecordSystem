package com.example.demo.doctor;

import java.time.LocalDateTime;
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
import com.example.demo.patient.PatientFollowup;
import com.example.demo.patient.PatientFollowupRepository;
import com.example.demo.patient.PatientRepository;
import com.example.demo.user.Role;
import com.example.demo.user.User;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class DoctorController {

	@Autowired
	private AuthHelper authHelper;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private PatientRecordRepository patientRecordRepository;

	@Autowired
	private PatientFollowupRepository patientFollowupRepository;

	@Autowired
	private AppointmentRepository appointmentRepository;

	@GetMapping("/doctor/patients")
	public String viewPatients(HttpServletRequest request, Model model,
			@RequestParam(name = "search", required = false) String search) {
		if (!authHelper.hasRole(request, Role.DOCTOR)) {
			return "redirect:/doctor/login";
		}

		User doctor = authHelper.getCurrentUser(request);

		List<Patient> patients;
		if (search != null && !search.isBlank()) {
			patients = patientRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(search,
					search);
		} else {
			patients = patientRepository.findAll();
		}

		model.addAttribute("user", doctor);
		model.addAttribute("patients", patients);
		model.addAttribute("search", search);
		return "doctor/patients.html";
	}

	@GetMapping("/doctor/patient/{patientId}")
	public String viewPatientDetails(@PathVariable("patientId") int patientId, HttpServletRequest request,
			Model model) {
		if (!authHelper.hasRole(request, Role.DOCTOR)) {
			return "redirect:/doctor/login";
		}

		User doctor = authHelper.getCurrentUser(request);

		Patient patient = patientRepository.findById(patientId).orElse(null);
		if (patient == null) {
			return "redirect:/doctor/patients";
		}

		List<PatientRecord> records = patientRecordRepository.findByPatientAndDoctor(patient, doctor);
		List<Appointment> appointments = appointmentRepository.findByPatient(patient);

		model.addAttribute("user", doctor);
		model.addAttribute("patient", patient);
		model.addAttribute("records", records);
		model.addAttribute("appointments", appointments);
		return "doctor/patient-details.html";
	}

	@GetMapping("/doctor/patient-record/add")
	public String getAddPatientRecordPage(HttpServletRequest request, Model model,
			@RequestParam(name = "patientId", required = false) Integer patientId) {
		if (!authHelper.hasRole(request, Role.DOCTOR)) {
			return "redirect:/doctor/login";
		}

		User doctor = authHelper.getCurrentUser(request);

		List<Patient> patients = patientRepository.findAll();
		Patient selectedPatient = null;
		if (patientId != null) {
			selectedPatient = patientRepository.findById(patientId).orElse(null);
		}

		model.addAttribute("user", doctor);
		model.addAttribute("patients", patients);
		model.addAttribute("selectedPatient", selectedPatient);
		return "doctor/patient-record-add.html";
	}

	@PostMapping("/doctor/patient-record/add")
	public String addPatientRecord(@RequestParam("patientId") int patientId,
			@RequestParam(name = "diagnosis", required = false) String diagnosis,
			@RequestParam(name = "prescriptions", required = false) String prescriptions,
			@RequestParam(name = "labResults", required = false) String labResults,
			@RequestParam(name = "visitNotes", required = false) String visitNotes,
			@RequestParam(name = "visitType", required = false) String visitType,
			@RequestParam(name = "remarks", required = false) String remarks, HttpServletRequest request) {
		if (!authHelper.hasRole(request, Role.DOCTOR)) {
			return "redirect:/doctor/login";
		}

		User doctor = authHelper.getCurrentUser(request);

		Patient patient = patientRepository.findById(patientId).orElse(null);
		if (patient == null) {
			return "redirect:/doctor/patient-record/add";
		}

		PatientRecord record = new PatientRecord();
		record.setPatient(patient);
		record.setDoctor(doctor);
		record.setDiagnosis(diagnosis);
		record.setPrescriptions(prescriptions);
		record.setLabResults(labResults);
		record.setVisitNotes(visitNotes);
		record.setVisitType(visitType != null ? visitType : "FOLLOWUP");
		record.setRemarks(remarks);

		patientRecordRepository.save(record);
		return "redirect:/doctor/patient/" + patientId;
	}

	@GetMapping("/doctor/patient-record/{recordId}/edit")
	public String getEditPatientRecordPage(@PathVariable("recordId") int recordId, HttpServletRequest request,
			Model model) {
		if (!authHelper.hasRole(request, Role.DOCTOR)) {
			return "redirect:/doctor/login";
		}

		User doctor = authHelper.getCurrentUser(request);

		PatientRecord record = patientRecordRepository.findById(recordId).orElse(null);
		if (record == null || record.getDoctor().getId() != doctor.getId()) {
			return "redirect:/doctor/patients";
		}

		model.addAttribute("user", doctor);
		model.addAttribute("record", record);
		return "doctor/patient-record-edit.html";
	}

	@PostMapping("/doctor/patient-record/{recordId}/edit")
	public String updatePatientRecord(@PathVariable("recordId") int recordId,
			@RequestParam(name = "diagnosis", required = false) String diagnosis,
			@RequestParam(name = "prescriptions", required = false) String prescriptions,
			@RequestParam(name = "labResults", required = false) String labResults,
			@RequestParam(name = "visitNotes", required = false) String visitNotes,
			@RequestParam(name = "visitType", required = false) String visitType,
			@RequestParam(name = "remarks", required = false) String remarks, HttpServletRequest request) {
		if (!authHelper.hasRole(request, Role.DOCTOR)) {
			return "redirect:/doctor/login";
		}

		User doctor = authHelper.getCurrentUser(request);

		PatientRecord record = patientRecordRepository.findById(recordId).orElse(null);
		if (record == null || record.getDoctor().getId() != doctor.getId()) {
			return "redirect:/doctor/patients";
		}

		record.setDiagnosis(diagnosis);
		record.setPrescriptions(prescriptions);
		record.setLabResults(labResults);
		record.setVisitNotes(visitNotes);
		if (visitType != null) {
			record.setVisitType(visitType);
		}
		record.setRemarks(remarks);

		patientRecordRepository.save(record);
		return "redirect:/doctor/patient/" + record.getPatient().getId();
	}

	@PostMapping("/doctor/patient-record/{recordId}/delete")
	public String deletePatientRecord(@PathVariable("recordId") int recordId, HttpServletRequest request) {
		if (!authHelper.hasRole(request, Role.DOCTOR)) {
			return "redirect:/doctor/login";
		}

		User doctor = authHelper.getCurrentUser(request);

		PatientRecord record = patientRecordRepository.findById(recordId).orElse(null);
		if (record != null && record.getDoctor().getId() == doctor.getId()) {
			int patientId = record.getPatient().getId();
			patientRecordRepository.delete(record);
			return "redirect:/doctor/patient/" + patientId;
		}
		return "redirect:/doctor/patients";
	}

	@GetMapping("/doctor/appointments")
	public String viewAppointments(HttpServletRequest request, Model model) {
		if (!authHelper.hasRole(request, Role.DOCTOR)) {
			return "redirect:/doctor/login";
		}

		User doctor = authHelper.getCurrentUser(request);

		List<Appointment> appointments = appointmentRepository.findByDoctor(doctor);
		model.addAttribute("user", doctor);
		model.addAttribute("appointments", appointments);
		return "doctor/appointments.html";
	}

	@GetMapping("/doctor/appointment/add")
	public String getAddAppointmentPage(HttpServletRequest request, Model model,
			@RequestParam(name = "patientId", required = false) Integer patientId) {
		if (!authHelper.hasRole(request, Role.DOCTOR)) {
			return "redirect:/doctor/login";
		}

		User doctor = authHelper.getCurrentUser(request);

		List<Patient> patients = patientRepository.findAll();
		Patient selectedPatient = null;
		if (patientId != null) {
			selectedPatient = patientRepository.findById(patientId).orElse(null);
		}

		model.addAttribute("user", doctor);
		model.addAttribute("patients", patients);
		model.addAttribute("selectedPatient", selectedPatient);
		return "doctor/appointment-add.html";
	}

	@PostMapping("/doctor/appointment/add")
	public String addAppointment(@RequestParam("patientId") int patientId,
			@RequestParam(name = "appointmentDateTime", required = false) String appointmentDateTime,
			@RequestParam(name = "reason", required = false) String reason,
			@RequestParam(name = "notes", required = false) String notes,
			@RequestParam(name = "status", required = false) String status, HttpServletRequest request) {
		if (!authHelper.hasRole(request, Role.DOCTOR)) {
			return "redirect:/doctor/login";
		}

		User doctor = authHelper.getCurrentUser(request);

		Patient patient = patientRepository.findById(patientId).orElse(null);
		if (patient == null) {
			return "redirect:/doctor/appointment/add";
		}

		Appointment appointment = new Appointment();
		appointment.setPatient(patient);
		appointment.setDoctor(doctor);
		appointment.setReason(reason);
		appointment.setNotes(notes);
		appointment.setStatus(status != null ? status : "SCHEDULED");

		// Parse appointment date time
		if (appointmentDateTime != null && !appointmentDateTime.isBlank()) {
			try {
				// Handle HTML datetime-local format (yyyy-MM-ddTHH:mm or yyyy-MM-dd HH:mm)
				String dateTimeStr = appointmentDateTime.replace(" ", "T");
				if (!dateTimeStr.contains("T")) {
					dateTimeStr = dateTimeStr + "T00:00";
				}
				if (dateTimeStr.length() == 16) {
					dateTimeStr = dateTimeStr + ":00"; // Add seconds if missing
				}
				LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr);
				appointment.setAppointmentDateTime(dateTime);
			} catch (Exception e) {
				// If parsing fails, set to current time + 1 hour
				appointment.setAppointmentDateTime(LocalDateTime.now().plusHours(1));
			}
		} else {
			appointment.setAppointmentDateTime(LocalDateTime.now().plusHours(1));
		}

		appointmentRepository.save(appointment);
		return "redirect:/doctor/appointments";
	}

	@PostMapping("/doctor/appointment/{appointmentId}/update-status")
	public String updateAppointmentStatus(@PathVariable("appointmentId") int appointmentId,
			@RequestParam("status") String status,
			HttpServletRequest request) {
		if (!authHelper.hasRole(request, Role.DOCTOR)) {
			return "redirect:/doctor/login";
		}

		User doctor = authHelper.getCurrentUser(request);

		Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
		if (appointment != null && appointment.getDoctor().getId() == doctor.getId()) {
			appointment.setStatus(status);
			appointmentRepository.save(appointment);
		}

		return "redirect:/doctor/appointments";
	}

}
