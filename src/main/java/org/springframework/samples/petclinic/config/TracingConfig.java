package org.springframework.samples.petclinic.config;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.samples.petclinic.tracing.TracingMapCache;
import org.springframework.samples.petclinic.tracing.TracingWrappedJdbcTemplate;

import io.opencensus.exporter.trace.jaeger.JaegerTraceExporter;



@Configuration
@EnableCaching
public class TracingConfig {
	
	public TracingConfig(@Value("${tracing.jaegerUrl}") String jaegerThriftEndpoint) {
		JaegerTraceExporter.createAndRegister(jaegerThriftEndpoint, "PetClinic");
	}

	@Bean
	public CacheManager cacheManager() {
		SimpleCacheManager cacheManager = new SimpleCacheManager();
		cacheManager.setCaches(Arrays.asList(new TracingMapCache("hello_cache")));
		return cacheManager;
	}
	
	@Bean
	public TracingWrappedJdbcTemplate getJdbcTemplate(DataSource ds) {
		return new TracingWrappedJdbcTemplate(ds);
	}
    
	@Bean
	public FilterRegistrationBean tracingFilter() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		registrationBean.setFilter(new org.springframework.samples.petclinic.tracing.TracingFilter());
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}
}
