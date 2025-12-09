package com.example.demo.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.example.demo.user.Gender;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;
import com.example.demo.user.UserType;

import jakarta.annotation.PostConstruct;

@Component
public class Admin {
	@Autowired
	private UserRepository userRepo;

	private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	@Value("${DPRS_ADMIN_PASSWORD}")
	private String password;
	@PostConstruct
   private void setUp() {
		if (userRepo.count() == 0) {
		
	   User admin = new User();
	   
	   admin.setFirstName("Admin");
	   admin.setLastName("admin");
	   admin.setType(UserType.ADMIN);
	   admin.setEmail("admin@gmail.com");
	   admin.setPassword(passwordEncoder.encode(password));
	   admin.setGender(Gender.MALE);
	   admin.setUsername("adminho");
	   userRepo.save(admin);
	}
	}
}
