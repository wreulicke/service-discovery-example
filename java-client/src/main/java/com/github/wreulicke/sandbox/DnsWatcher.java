package com.github.wreulicke.sandbox;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.spotify.dns.ChangeNotifier;
import com.spotify.dns.DnsSrvResolver;
import com.spotify.dns.DnsSrvWatcher;
import com.spotify.dns.DnsSrvWatchers;
import com.spotify.dns.LookupResult;

@Component
public class DnsWatcher implements InitializingBean {
	
	private static final Logger log = LoggerFactory.getLogger(DnsWatcher.class);
	
	private final DnsSrvWatcher<LookupResult> watcher;
	
	private final DnsSrvResolver resolver;
	
	private final BackendProperties properties;
	
	DnsWatcher(DnsSrvResolver resolver, BackendProperties properties) {
		this.resolver = resolver;
		this.properties = properties;
		watcher = DnsSrvWatchers.newBuilder(this.resolver)
			.polling(30, TimeUnit.SECONDS)
			.withErrorHandler((fqdn, exception) -> log.info("service is not found:{}", fqdn, exception))
			.build();
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		ChangeNotifier<LookupResult> notifier = watcher.watch(properties.getEndpoint().getHost());
		notifier.setListener(changeNotification -> log.info("changed:{}", changeNotification.current()), false);
	}
	
	public List<LookupResult> resolve(String fqdn) {
		return resolver.resolve(fqdn);
	}
}
