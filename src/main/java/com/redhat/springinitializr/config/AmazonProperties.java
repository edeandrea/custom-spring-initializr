package com.redhat.springinitializr.config;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import com.amazonaws.regions.Regions;

@ConfigurationProperties(prefix = "amazon", ignoreUnknownFields = false)
@Validated
public class AmazonProperties {
	@Valid
	private final Aws aws = new Aws();

	@Valid
	private final DynamoDb dynamoDb = new DynamoDb();

	public Aws getAws() {
		return this.aws;
	}

	public DynamoDb getDynamoDb() {
		return this.dynamoDb;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static class Aws {
		@NotEmpty(message = "accessKey can't be empty")
		private String accessKey;

		@NotEmpty(message = "secretKey can't be empty")
		private String secretKey;

		@NotNull(message = "There must be a region specified")
		private Regions region;

		public String getAccessKey() {
			return this.accessKey;
		}

		public void setAccessKey(String accessKey) {
			this.accessKey = accessKey;
		}

		public String getSecretKey() {
			return this.secretKey;
		}

		public void setSecretKey(String secretKey) {
			this.secretKey = secretKey;
		}

		public Regions getRegion() {
			return this.region;
		}

		public void setRegion(Regions region) {
			this.region = region;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	public static class DynamoDb {
		private String endpoint;

		public String getEndpoint() {
			return this.endpoint;
		}

		public void setEndpoint(String endpoint) {
			this.endpoint = endpoint;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}
}
