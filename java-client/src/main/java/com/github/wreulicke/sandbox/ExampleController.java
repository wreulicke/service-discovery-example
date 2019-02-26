package com.github.wreulicke.sandbox;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;

@RestController
public class ExampleController {
	
	private final Backend backend;
	
	public ExampleController(Backend backend) {
		this.backend = backend;
	}
	
	@GetMapping("/")
	public Mono<JsonNode> get() {
		return backend.api();
	}
}
