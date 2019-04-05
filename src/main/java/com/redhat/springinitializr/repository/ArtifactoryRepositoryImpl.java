package com.redhat.springinitializr.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.apache.commons.lang3.StringUtils;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriTemplate;

import com.redhat.springinitializr.config.AppProperties;
import com.redhat.springinitializr.config.AppProperties.ArtifactoryRepo;
import com.redhat.springinitializr.generated.maven.Project;

@Repository
public class ArtifactoryRepositoryImpl implements ArtifactoryRepository {
	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactoryRepositoryImpl.class);

	private final RestOperations restOperations;
	private final AppProperties appProperties;

	public ArtifactoryRepositoryImpl(RestTemplateBuilder restTemplateBuilder, AppProperties appProperties) {
		this.restOperations = restTemplateBuilder.build();
		this.appProperties = appProperties;
	}

	@Override
	public Optional<String> getLatestVersion(ArtifactoryRepo artifactoryRepo, Optional<String> versionLabel) {
		String url = String.format(
			"%s%s%s",
			this.appProperties.getArtifactory().getBaseUrl(),
			this.appProperties.getArtifactory().getLatestVersionUriTemplate(),
			versionLabel.map(version -> "&v={version}").orElse(""));

		try {
			var uriParams = getLatestVersionUriParams(artifactoryRepo, versionLabel);
			LOGGER.info("Making GET request to {}", new UriTemplate(url).expand(uriParams));
			return Optional.ofNullable(this.restOperations.getForObject(url, String.class, uriParams));
		}
		catch (RestClientException ex) {
			handleRestClientException(ex);
			return Optional.empty();
		}
	}

	@Override
	public Optional<String> getSpringBootVersion(String acidFrameworkVersion) {
		String url = String.format("%s%s", this.appProperties.getArtifactory().getBaseUrl(), this.appProperties.getArtifactory().getArtifactPomUriTemplate());

		try {
			var uriParams = getPomUriParams(acidFrameworkVersion);
			LOGGER.info("Making GET request to {}", new UriTemplate(url).expand(uriParams));

			return getSpringBootVersion(Optional.ofNullable(this.restOperations.getForObject(url, Project.class, uriParams)));
		}
		catch (RestClientException ex) {
			handleRestClientException(ex);
			return Optional.empty();
		}
	}

	private Optional<String> getSpringBootVersion(Optional<Project> mavenPomProject) {
		return mavenPomProject
			.map(Project::getProperties)
			.map(Project.Properties::getAnies)
			.filter(Objects::nonNull)
			.map(List::stream)
			.orElseGet(Stream::empty)
			.filter(element -> StringUtils.equals(StringUtils.trimToNull(element.getTagName()), "spring.boot.version"))
			.findFirst()
			.map(Element::getFirstChild)
			.map(Node::getNodeValue);
	}

	private static void handleRestClientException(RestClientException ex) {
		if ((ex instanceof RestClientResponseException) && (((RestClientResponseException) ex).getRawStatusCode() == HttpStatus.NOT_FOUND.value())) {
			LOGGER.info("Got 404 back from Artifactory. Maybe what we were looking for isn't there?");
		}
		else {
			LOGGER.error("Got exception back from Artifactory: {}", ex.getMessage(), ex);
		}
	}

	private static Map<String, String> getLatestVersionUriParams(ArtifactoryRepo artifactoryRepo, Optional<String> versionLabel) {
		var uriParams = new HashMap<String, String>();
		uriParams.put("groupId", artifactoryRepo.getGroupId());
		uriParams.put("artifactId", artifactoryRepo.getArtifactId());
		uriParams.put("repos", artifactoryRepo.getRepo());

		versionLabel.ifPresent(version -> uriParams.put("version", version));

		LOGGER.debug("Using URI params = {}", uriParams);

		return uriParams;
	}

	private Map<String, String> getPomUriParams(String acidFrameworkVersion) {
		var uriParams = new HashMap<String, String>();
		AppProperties.Repository acidFrameworkRepo = this.appProperties.getAcidFramework();

		uriParams.put("repo", acidFrameworkRepo.getArtifactory().getRepo());
		uriParams.put("groupId", acidFrameworkRepo.getArtifactory().getGroupId().replace('.', '/'));
		uriParams.put("artifactId", acidFrameworkRepo.getArtifactory().getArtifactId());
		uriParams.put("version", Optional.ofNullable(acidFrameworkVersion).orElseGet(acidFrameworkRepo::getDefaultVersion));

		return uriParams;
	}
}
