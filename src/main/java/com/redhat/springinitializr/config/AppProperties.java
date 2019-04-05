package com.redhat.springinitializr.config;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "redhat.springinitializr", ignoreUnknownFields = false)
@Validated
public class AppProperties {
	/**
	 * The Artifactory configuration
	 */
	@Valid
	private final Artifactory artifactory = new Artifactory();

	/**
	 * The ACID Framework configuration
	 */
	@Valid
	private final Repository acidFramework = new Repository();

	/**
	 * The Swagger Documentation configuration
	 */
	@Valid
	private final Repository swaggerDocumentation = new Repository();

	/**
	 * The Gradle configuration
	 */
	@Valid
	private final Repository mycompanyGradle = new Repository();

	/**
	 * Whether or not to publish statistics to the database
	 */
	private boolean publishStatistics = true;

	/**
	 * Gets the Artifactory configuration
	 *
	 * @return The Artifactory configuration
	 */
	public Artifactory getArtifactory() {
		return this.artifactory;
	}

	/**
	 * Gets the ACID Framework configuration
	 *
	 * @return The ACID Framework configuration
	 */
	public Repository getAcidFramework() {
		return this.acidFramework;
	}

	/**
	 * Gets the Swagger Documentation configuration
	 *
	 * @return The Swagger Documentation configuration
	 */
	public Repository getSwaggerDocumentation() {
		return this.swaggerDocumentation;
	}

	/**
	 * Gets the My Company Gradle configuration
	 *
	 * @return The My Company Gradle configuration
	 */
	public Repository getMycompanyGradle() {
		return this.mycompanyGradle;
	}

	/**
	 * Whether or not to publish statistics to the database
	 *
	 * @return True if statistics should be published to the database. False otherwise.
	 */
	public boolean isPublishStatistics() {
		return this.publishStatistics;
	}

	/**
	 * Sets whether or not to publish statistics to the database
	 *
	 * @param publishStatistics Whether or not to publish statistics to the database
	 */
	public void setPublishStatistics(boolean publishStatistics) {
		this.publishStatistics = publishStatistics;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * The Artifactory configuration
	 */
	public static final class Artifactory {
		/**
		 * The Artifactory base url. Can't be empty
		 */
		@NotEmpty(message = "Artifactory baseUrl can not be empty")
		private String baseUrl;

		/**
		 * The Artifactory latest version URI Template
		 */
		@NotEmpty(message = "Latest version URI template can't be empty")
		private String latestVersionUriTemplate = "/artifactory/api/search/latestVersion?g={groupId}&a={artifactId}&repos={repos}";

		/**
		 * The Artifactory POM URI Template
		 */
		@NotEmpty(message = "Artifact POM URI template can't be empty")
		private String artifactPomUriTemplate = "/artifactory/{repo}/{groupId}/{artifactId}/{version}/{artifactId}-{version}.pom";

		/**
		 * Gets the Artifactory latest version URI Template
		 * <p>
		 * Default is /artifactory/api/search/latestVersion?g={groupId}&amp;a={artifactId}&amp;repos={repos}
		 * </p>
		 *
		 * @return The Artifactory latest version URI Template
		 */
		public String getLatestVersionUriTemplate() {
			return this.latestVersionUriTemplate;
		}

		/**
		 * Sets the Artifactory latest version URI Template
		 *
		 * @param latestVersionUriTemplate The Artifactory latest version URI template
		 */
		public void setLatestVersionUriTemplate(String latestVersionUriTemplate) {
			this.latestVersionUriTemplate = latestVersionUriTemplate;
		}

		/**
		 * Gets the Artifactory base url
		 *
		 * @return The Artifactory base url
		 */
		public String getBaseUrl() {
			return this.baseUrl;
		}

		/**
		 * Sets the Artifactory base url
		 *
		 * @param baseUrl The Artifactory base url
		 */
		public void setBaseUrl(String baseUrl) {
			this.baseUrl = baseUrl;
		}

		/**
		 * Gets the artifact POM URI template
		 *
		 * @return The artifact POM URI template
		 */
		public String getArtifactPomUriTemplate() {
			return this.artifactPomUriTemplate;
		}

		/**
		 * Sets the artifact POM URI template
		 *
		 * @param artifactPomUriTemplate The artifact POM URI template
		 */
		public void setArtifactPomUriTemplate(String artifactPomUriTemplate) {
			this.artifactPomUriTemplate = artifactPomUriTemplate;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	/**
	 * Repository configuration
	 */
	public static final class Repository {
		/**
		 * The Artifactory config for ACID Framework
		 */
		@Valid
		private final ArtifactoryRepo artifactory = new ArtifactoryRepo();

		/**
		 * The ACID Framework default version
		 */
		@NotEmpty(message = "Default version can't be empty")
		private String defaultVersion;

		/**
		 * Gets the ACID Framework default version
		 *
		 * @return The ACID Framework default version
		 */
		public String getDefaultVersion() {
			return this.defaultVersion;
		}

		/**
		 * Sets the ACID Framework default version
		 *
		 * @param defaultVersion The ACID Framework default version
		 */
		public void setDefaultVersion(String defaultVersion) {
			this.defaultVersion = defaultVersion;
		}

		/**
		 * Gets the Artifactory config for ACID Framework
		 *
		 * @return The Artifactory config for ACID Framework
		 */
		public ArtifactoryRepo getArtifactory() {
			return this.artifactory;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	public static final class ArtifactoryRepo {
		/**
		 * The groupId
		 */
		@NotEmpty(message = "The groupId can't be empty")
		private String groupId;

		/**
		 * The artifactId
		 */
		@NotEmpty(message = "The artifactId can't be empty")
		private String artifactId;

		/**
		 * The repo
		 */
		@NotEmpty(message = "The repo can't be empty")
		private String repo = "commercial-it-java-prod-local";

		/**
		 * Gets the group id
		 *
		 * @return The group id
		 */
		public String getGroupId() {
			return this.groupId;
		}

		/**
		 * Sets the group id
		 *
		 * @param groupId The group id
		 */
		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}

		/**
		 * Gets the artifact id
		 *
		 * @return The artifact id
		 */
		public String getArtifactId() {
			return this.artifactId;
		}

		/**
		 * Sets the artifact id
		 *
		 * @param artifactId The artifact id
		 */
		public void setArtifactId(String artifactId) {
			this.artifactId = artifactId;
		}

		/**
		 * Gets the repo
		 *
		 * @return The repo
		 */
		public String getRepo() {
			return this.repo;
		}

		/**
		 * Sets the repo
		 *
		 * @param repo The repo
		 */
		public void setRepo(String repo) {
			this.repo = repo;
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}
}
