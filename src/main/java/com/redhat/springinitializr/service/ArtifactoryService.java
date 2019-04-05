package com.redhat.springinitializr.service;

import java.util.Optional;

import com.redhat.springinitializr.config.AppProperties.Repository;

public interface ArtifactoryService {
	/**
	 * Gets the latest version from an Artifactory {@link Repository}
	 *
	 * @param repositoryKey The repository key
	 * @param artifactoryRepo The {@link Repository}
	 * @return The latest version
	 */
	default String getLatestVersion(String repositoryKey, Repository artifactoryRepo) {
		return getLatestVersion(repositoryKey, artifactoryRepo, Optional.empty());
	}

	/**
	 * Gets the latest version from an Artifactory {@link Repository} given an optional version label
	 * @param repositoryKey The repository key
	 * @param artifactoryRepo The {@link Repository}
	 * @param versionLabel The version label
	 * @return The latest version
	 */
	public String getLatestVersion(String repositoryKey, Repository artifactoryRepo, Optional<String> versionLabel);

	/**
	 * Gets the Spring Boot version for a given ACID Framework version
	 *
	 * @param acidFrameworkVersion      The ACID Framework version
	 * @param selectedSpringBootVersion The Spring Boot version the user selected
	 * @return The spring boot version
	 */
	public String getSpringBootVersion(String acidFrameworkVersion, String selectedSpringBootVersion);
}
