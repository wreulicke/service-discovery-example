package com.github.wreulicke.sandbox;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory;
import com.spotify.dns.DnsSrvResolver;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class BackendConfiguration {
	
	private final DnsSrvResolver resolver;
	
	private final BackendProperties properties;
	
	public BackendConfiguration(DnsSrvResolver resolver, BackendProperties properties) {
		this.resolver = resolver;
		this.properties = properties;
	}
	
	@Bean
	Backend backend() {
		Retrofit retrofit = new Retrofit.Builder()
			.baseUrl(HttpUrl.get(properties.getEndpoint()))
			.client(new OkHttpClient.Builder()
				.addInterceptor(new OkHttpClientLoadBalancedInterceptor(resolver))
				.build())
			.addConverterFactory(JacksonConverterFactory.create())
			.addCallAdapterFactory(ReactorCallAdapterFactory.create())
			.build();
		
		return retrofit.create(Backend.class);
	}
	
}
