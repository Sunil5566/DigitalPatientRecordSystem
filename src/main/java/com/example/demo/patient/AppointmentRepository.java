package com.example.demo.patient;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.user.User;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
	
	List<Appointment> findByDoctor(User doctor);
	
	List<Appointment> findByPatient(Patient patient);
	
	List<Appointment> findByDoctorAndAppointmentDateTimeBetween(User doctor, LocalDateTime start, LocalDateTime end);
	
	List<Appointment> findByStatus(String status);

}

