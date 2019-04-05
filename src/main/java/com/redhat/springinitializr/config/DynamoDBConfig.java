package com.redhat.springinitializr.config;

import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.redhat.springinitializr.repository.StatsRepository;

@Configuration
@EnableDynamoDBRepositories(basePackageClasses = { StatsRepository.class })
public class DynamoDBConfig {
	private final AmazonProperties amazonProperties;

	public DynamoDBConfig(AmazonProperties amazonProperties) {
		this.amazonProperties = amazonProperties;
	}

	@Bean
	public AmazonDynamoDB amazonDynamoDB() {
		return AmazonDynamoDBClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(getAmazonAWSCredentials()))
				.withRegion(this.amazonProperties.getAws().getRegion())
				.build();
	}

	private AWSCredentials getAmazonAWSCredentials() {
		return new BasicAWSCredentials(this.amazonProperties.getAws().getAccessKey(), this.amazonProperties.getAws().getSecretKey());
	}
}
