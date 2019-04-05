package com.redhat.springinitializr.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ AppProperties.class, AmazonProperties.class })
public class AppConfig {}
