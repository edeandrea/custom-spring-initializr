package com.redhat.springinitializr;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.InitializrMetadataBuilder;
import io.spring.initializr.metadata.InitializrMetadataProvider;

@SpringBootTest(
	webEnvironment = WebEnvironment.RANDOM_PORT,
	properties = {
		"amazon.aws.access-key=blah",
		"amazon.aws.secret-key=blah"
	}
)
@ActiveProfiles({ "local" })
public class SmokeTests {
	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private InitializrMetadataProvider metadataProvider;

	@Test
	public void metadataCanBeSerialized() throws URISyntaxException, IOException {
		RequestEntity<Void> request = RequestEntity.get(new URI("/"))
			.accept(MediaType.parseMediaType("application/vnd.initializr.v2.1+json"))
			.build();
		ResponseEntity<String> response = this.restTemplate.exchange(request,
			String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		new ObjectMapper().readTree(response.getBody());
	}

	@Test
	public void configurationCanBeSerialized() throws URISyntaxException {
		RequestEntity<Void> request = RequestEntity.get(new URI("/metadata/config"))
			.accept(MediaType.APPLICATION_JSON).build();
		ResponseEntity<String> response = this.restTemplate.exchange(request,
			String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		InitializrMetadata actual = InitializrMetadataBuilder.create()
			.withInitializrMetadata(
				new ByteArrayResource(response.getBody().getBytes()))
			.build();
		assertThat(actual).isNotNull();
		InitializrMetadata expected = this.metadataProvider.get();
		assertThat(actual.getDependencies().getAll().size())
			.isEqualTo(expected.getDependencies().getAll().size());
		assertThat(actual.getConfiguration().getEnv().getBoms().size())
			.isEqualTo(expected.getConfiguration().getEnv().getBoms().size());
	}
}
