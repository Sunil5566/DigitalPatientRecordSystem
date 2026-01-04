package com.example.demo.patient;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VitalsRepository extends JpaRepository<Vitals, Integer> {
	
	List<Vitals> findByPatient(Patient patient);
	
	List<Vitals> findByPatientOrderByCreatedAtDesc(Patient patient);

}

