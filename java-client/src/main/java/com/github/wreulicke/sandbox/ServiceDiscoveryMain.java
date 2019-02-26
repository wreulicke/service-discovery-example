package com.github.wreulicke.sandbox;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = ReactiveUserDetailsServiceAutoConfiguration.class)
public class ServiceDiscoveryMain {
	
	public static void main(String[] args) throws IOException {
		SpringApplication.run(ServiceDiscoveryMain.class, args);
	}
}
