package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.demo.auth.JwtAuthenticationFilter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	// JWT filter is registered via @Component annotation
	// This config is for any additional interceptors if needed
}

