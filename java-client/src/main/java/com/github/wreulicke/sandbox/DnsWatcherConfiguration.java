package com.github.wreulicke.sandbox;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.spotify.dns.DnsSrvResolver;
import com.spotify.dns.DnsSrvResolvers;
import com.spotify.dns.LookupResult;

@Configuration
public class DnsWatcherConfiguration {
	
	
	private static final Logger log = LoggerFactory.getLogger(DnsWatcherConfiguration.class);
	
	@Bean
	DnsSrvResolver watcher() {
		return new CacheDnsResolver(DnsSrvResolvers.newBuilder()
			.dnsLookupTimeoutMillis(1000)
			.retentionDurationMillis(30)
			.retainingDataOnFailures(true)
			.build(), CacheBuilder.newBuilder()
			.expireAfterWrite(30, TimeUnit.MILLISECONDS).build()
		);
	}
	
	public static class CacheDnsResolver implements DnsSrvResolver {
		
		private final DnsSrvResolver delegates;
		
		private final Cache<String, List<LookupResult>> caches;
		
		public CacheDnsResolver(DnsSrvResolver delegates, Cache<String, List<LookupResult>> caches) {
			this.delegates = delegates;
			this.caches = caches;
		}
		
		@Override
		public List<LookupResult> resolve(String fqdn) {
			try {
				return caches.get(fqdn, () -> {
					log.info("requested: {}", fqdn);
					return delegates.resolve(fqdn);
				});
			} catch (ExecutionException e) {
				// TODO handle error
				throw new RuntimeException(e);
			}
		}
	}
}
