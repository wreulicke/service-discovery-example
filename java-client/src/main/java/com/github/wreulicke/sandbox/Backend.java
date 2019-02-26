package com.github.wreulicke.sandbox;

import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;
import retrofit2.http.GET;

public interface Backend {

	@GET("/")
	Mono<JsonNode> api();

}
