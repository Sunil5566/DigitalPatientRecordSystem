package com.example.demo.patient;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.user.User;

@Repository
public interface PatientRecordRepository extends JpaRepository<PatientRecord, Integer> {
	
	List<PatientRecord> findByPatient(Patient patient);
	
	List<PatientRecord> findByDoctor(User doctor);
	
	List<PatientRecord> findByPatientAndDoctor(Patient patient, User doctor);

}

