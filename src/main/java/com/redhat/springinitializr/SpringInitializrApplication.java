package com.redhat.springinitializr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.resource.ResourceUrlProvider;

import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.web.project.LegacyStsController;

@SpringBootApplication
@EnableCaching
public class SpringInitializrApplication extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(SpringInitializrApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringInitializrApplication.class);
	}

	@Bean
	public LegacyStsController legacyStsController(InitializrMetadataProvider metadataProvider, ResourceUrlProvider resourceUrlProvider) {
		return new LegacyStsController(metadataProvider, resourceUrlProvider);
	}
}
