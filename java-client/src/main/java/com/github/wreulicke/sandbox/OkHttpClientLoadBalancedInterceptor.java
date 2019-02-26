package com.github.wreulicke.sandbox;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spotify.dns.DnsSrvResolver;
import com.spotify.dns.LookupResult;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpClientLoadBalancedInterceptor implements Interceptor {
	
	private static final Logger log = LoggerFactory.getLogger(OkHttpClientLoadBalancedInterceptor.class);
	
	private final DnsSrvResolver resolver;
	
	private final AtomicInteger counter = new AtomicInteger();
	
	public OkHttpClientLoadBalancedInterceptor(DnsSrvResolver resolver) {
		this.resolver = resolver;
	}
	
	@Override
	public Response intercept(Chain chain) throws IOException {
		Request original = chain.request();
		HttpUrl originalUrl = original.url();
		String serviceId = originalUrl.host();
		List<LookupResult> results = resolver.resolve(serviceId);
		
		if (results == null || results.isEmpty()) {
			throw new UnknownHostException(serviceId + " is not found.");
		}
		log.info("results:{}", results);
		
		// FIXME: this is experimental implementation.
		int current = counter.incrementAndGet();
		LookupResult result = results.get(current % results.size());
		
		InetAddress address = InetAddress.getByName(result.host().substring(0, result.host().length() - 1));
		HttpUrl httpUrl = HttpUrl.get(
			"http://" + address.getHostAddress() + ":" + result.port());
		HttpUrl url = originalUrl.newBuilder()
			.scheme(originalUrl.scheme())
			.host(httpUrl.host())
			.port(result.port())
			.build();
		
		log.info("request to {}", url.host());
		Request request = original.newBuilder()
			.url(url)
			.build();
		
		return chain.proceed(request);
	}
}
