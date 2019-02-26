package com.github.wreulicke.sandbox;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("backend")
public class BackendProperties {
	
	private URL endpoint;
	
	public BackendProperties() throws MalformedURLException {
	}
	
	public URL getEndpoint() {
		return endpoint;
	}
	
	public void setEndpoint(URL endpoint) {
		this.endpoint = endpoint;
	}
}
