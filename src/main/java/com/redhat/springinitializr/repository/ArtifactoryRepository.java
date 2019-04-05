package com.redhat.springinitializr.repository;

import java.util.Optional;

import com.redhat.springinitializr.config.AppProperties.ArtifactoryRepo;

public interface ArtifactoryRepository {
	/**
	 * Gets the latest version from an artifactory repo
	 *
	 * @param artifactoryRepo The {@link ArtifactoryRepo}
	 * @param versionLabel The version label
	 * @return The latest version
	 */
	public Optional<String> getLatestVersion(ArtifactoryRepo artifactoryRepo, Optional<String> versionLabel);

	/**
	 * Gets the Spring Boot version for a given ACID Framework version
	 *
	 * @param acidFrameworkVersion The ACID Framework version
	 * @return The spring boot version
	 */
	public Optional<String> getSpringBootVersion(String acidFrameworkVersion);
}
