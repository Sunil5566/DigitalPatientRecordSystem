package com.example.demo.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.user.Role;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

import jakarta.annotation.PostConstruct;

@Component
public class Admin {
	@Autowired
	private UserRepository userRepo;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@Value("${DPRS_ADMIN_PASSWORD:admin123}")
	private String password;
	
	@PostConstruct
	private void setUp() {
		if (userRepo.count() == 0) {
			User admin = new User();
			admin.setFirstName("Admin");
			admin.setLastName("System");
			admin.setRole(Role.ADMIN);
			admin.setEmail("admin@hospital.com");
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode(password));
			admin.setActive(true);
			userRepo.save(admin);
		}
	}
}
