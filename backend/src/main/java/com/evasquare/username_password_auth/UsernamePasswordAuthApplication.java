package com.evasquare.username_password_auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@SpringBootApplication
public class UsernamePasswordAuthApplication {

	public static void main(String[] args) {
		SpringApplication.run(UsernamePasswordAuthApplication.class, args);
	}
}
