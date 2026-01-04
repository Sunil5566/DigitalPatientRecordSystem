package com.example.demo.patient;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.user.User;

@Repository
public interface PatientFollowupRepository extends JpaRepository<PatientFollowup, Integer> {
	
	List<PatientFollowup> findByPatient(Patient patient);
	
	List<PatientFollowup> findByDoctor(User doctor);
	
	List<PatientFollowup> findByPatientAndDoctor(Patient patient, User doctor);

}

