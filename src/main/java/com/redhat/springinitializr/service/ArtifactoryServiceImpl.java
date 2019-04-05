package com.redhat.springinitializr.service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.redhat.springinitializr.config.AppProperties.Repository;
import com.redhat.springinitializr.repository.ArtifactoryRepository;

@Service
public class ArtifactoryServiceImpl implements ArtifactoryService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ArtifactoryServiceImpl.class);
	private static final long ONE_HOUR_MILLIS = 1L * 60L * 60L * 1000L;

	// ConcurrentHashMap params derived off of reading this article:
	// https://ria101.wordpress.com/2011/12/12/concurrenthashmap-avoid-a-common-misuse/
	private final ConcurrentMap<String, String> versionsCache = new ConcurrentHashMap<>(2, 0.9f, 1);
	private final ConcurrentMap<String, String> springBootVersionsCache = new ConcurrentHashMap<>(2, 0.9f, 1);
	private final ArtifactoryRepository artifactoryRepository;

	public ArtifactoryServiceImpl(ArtifactoryRepository artifactoryRepository) {
		this.artifactoryRepository = artifactoryRepository;
	}

	@Override
	public String getLatestVersion(String repositoryKey, Repository artifactoryRepo, Optional<String> versionLabel) {
		LOGGER.info("Getting latest version from {}", artifactoryRepo);

		var latestVersion = this.versionsCache.computeIfAbsent(
				String.format("%s.%s", repositoryKey, versionLabel.orElse("")),
				key -> this.artifactoryRepository.getLatestVersion(artifactoryRepo.getArtifactory(), versionLabel).orElseGet(artifactoryRepo::getDefaultVersion));

		LOGGER.debug("Got latest version: {}", latestVersion);
		return latestVersion;
	}

	@Override
	public String getSpringBootVersion(String acidFrameworkVersion, String selectedSpringBootVersion) {
		LOGGER.info("Getting spring boot version given acid framework version = {} & selected spring boot version = {}", acidFrameworkVersion, selectedSpringBootVersion);

		var springBootVersion = Optional.ofNullable(this.springBootVersionsCache.computeIfAbsent(acidFrameworkVersion,
			key -> this.artifactoryRepository.getSpringBootVersion(acidFrameworkVersion).orElse(null)))
			.orElse(selectedSpringBootVersion);

		LOGGER.debug("Got spring boot version = {}", springBootVersion);
		return springBootVersion;
	}

	@Scheduled(fixedDelay = ONE_HOUR_MILLIS, initialDelay = ONE_HOUR_MILLIS)
	public void refreshVersionsCache() {
		LOGGER.info("Refreshing versions cache. Current cache contents: {}", this.versionsCache);

		this.versionsCache.clear();
		this.springBootVersionsCache.clear();
	}
}
