package com.redhat.springinitializr.generator;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.StringUtils;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.redhat.springinitializr.config.AppProperties;
import com.redhat.springinitializr.service.ArtifactoryService;
import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.util.Version;

@Component
@RefreshScope
public class ProjectGenerator extends io.spring.initializr.generator.ProjectGenerator {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectGenerator.class);
	private static final Pattern MAJOR_VERSION_PATTERN = Pattern.compile("(\\d\\.\\d)\\..*");

	private final InitializrMetadataProvider initializrMetadataProvider;
	private final ArtifactoryService artifactoryService;
	private final AppProperties appProperties;

	public ProjectGenerator(InitializrMetadataProvider initializrMetadataProvider, ArtifactoryService artifactoryService, AppProperties appProperties) {
		this.initializrMetadataProvider = initializrMetadataProvider;
		this.artifactoryService = artifactoryService;
		this.appProperties = appProperties;
	}

	private static Optional<HttpServletRequest> getRequest() {
		return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
			.filter(ServletRequestAttributes.class::isInstance)
			.map(ServletRequestAttributes.class::cast)
			.map(ServletRequestAttributes::getRequest);
	}

	private static String getRequestIp(Optional<HttpServletRequest> request) {
		return request
			.map(httpServletRequest -> Optional.ofNullable(httpServletRequest.getHeader("X-Forwarded-For")).orElseGet(httpServletRequest::getRemoteAddr))
			.orElse(null);
	}

	private static void populateAdditionalParameters(ProjectRequest projectRequest) {
		var httpServletRequest = getRequest();

		projectRequest.getParameters().put("requestIp", getRequestIp(httpServletRequest));
		projectRequest.getParameters().put("requestHost", httpServletRequest.map(HttpServletRequest::getRemoteHost).orElse(null));
		projectRequest.getParameters().put("requestUser", httpServletRequest.map(HttpServletRequest::getRemoteUser).orElse(null));
	}

	@Override
	protected File generateProjectStructure(ProjectRequest request, Map<String, Object> model) {
		var rootDir = super.generateProjectStructure(request, model);

		LOGGER.info("Generating customizations");

		var projectDir = getInitializerProjectDir(rootDir, request);

		handleProjectGradleSetup(projectDir, model);
		generateMainSourceSet(model, projectDir, request);
		generateTestSourceSet(model, projectDir, request);

		populateAdditionalParameters(request);

		return rootDir;
	}

	private void handleProjectGradleSetup(File projectDir, Map<String, Object> model) {
		// gradle.properties file
		write(new File(projectDir, "gradle.properties"), "gradle.properties", model);

		// gradle-wrapper.properties
		write(new File(projectDir, "gradle/wrapper/gradle-wrapper.properties"), "gradle-wrapper.properties", model);
	}

	@Override
	protected Map<String, Object> resolveModel(ProjectRequest originalRequest) {
		var model = super.resolveModel(originalRequest);
		var dependencies = originalRequest.getResolvedDependencies();
		var bootVersion = Version.parse(originalRequest.getBootVersion());
		var initializrMetadata = this.initializrMetadataProvider.get();

		boolean hasAcidService = hasAcidServiceDependency(dependencies);
		boolean hasAcidDependency = hasAcidDependency(dependencies);
		boolean isBoot2 = bootVersion.getMajor() == 2;

		findMapstructDependency(dependencies).ifPresent(mapstructDependency -> {
			model.put("mapstructVersion", mapstructDependency.getVersion());
		});

		findLombokDependency(dependencies).ifPresent(lombokDependency -> {
			model.put("lombokDependency", String.format("%s:%s", lombokDependency.getGroupId(), lombokDependency.getArtifactId()));
		});

		model.put("hasAcidService", hasAcidService);
		model.put("hasAcidDependency", hasAcidDependency);
		model.put("artifactoryBaseUrl", this.appProperties.getArtifactory().getBaseUrl());
		model.put("artifactoryEscapedBaseUrl", StringUtils.replaceAll(this.appProperties.getArtifactory().getBaseUrl(), ":", "\\:"));
		model.put("mycompanyGradleVersion", this.artifactoryService.getLatestVersion("mycompanyGradle", this.appProperties.getMycompanyGradle()));
		model.put("isBoot1", bootVersion.getMajor() == 1);
		model.put("isBoot2", isBoot2);
		model.put("useJUnitJupiter", isBoot2 && !hasAcidDependency);

		if (hasAcidService) {
			// Get the swagger documentation version
			model.put("swaggerDocumentationVersion", this.artifactoryService.getLatestVersion("swaggerDocumentation", this.appProperties.getSwaggerDocumentation()));

			// Check to see if either spring-boot-starter-web or spring-boot-starter-webflux is selected
			// If neither has been selected, then automatically select spring-boot-starter-web
			if (!hasWebOrWebfluxDependency(dependencies)) {
				var webDependency = initializrMetadata.getDependencies().get("web");
				dependencies.add(webDependency);

				((List<Dependency>) model.get("compileDependencies")).add(0, webDependency);
			}
		}

		if (hasAcidDependency) {
			// Get the acid framework version
			var acidFrameworkVersion = this.artifactoryService.getLatestVersion("acidFramework", this.appProperties.getAcidFramework(), Optional.ofNullable(String.format("%s.*", bootVersion.getMajor())));
			model.put("acidFrameworkVersion", acidFrameworkVersion);

			setSpringBootVersion(acidFrameworkVersion, model, originalRequest);

			// Remove the spring-cloud-dependencies BOM if it is there
			if (isBoot2) {
				var springCloudBom = initializrMetadata.getConfiguration().getEnv().getBoms().get("spring-cloud");

				Assert.notNull(springCloudBom, "spring-cloud BOM can't be null");

				Consumer<List<Map<String, String>>> removeSpringCloudDependenciesBomsConsumer = boms ->
					boms.removeIf(bom -> StringUtils.equals(bom.get("groupId"), springCloudBom.getGroupId()) && StringUtils.equals(bom.get("artifactId"), springCloudBom.getArtifactId()));

				var resolvedBoms = (List<Map<String, String>>) model.get("resolvedBoms");
				Optional.ofNullable(resolvedBoms).ifPresent(removeSpringCloudDependenciesBomsConsumer::accept);
				Optional.ofNullable((List<Map<String, String>>) model.get("reversedBoms")).ifPresent(removeSpringCloudDependenciesBomsConsumer::accept);

				originalRequest.getBoms().remove("spring-cloud");

				model.put("hasBoms", !originalRequest.getBoms().isEmpty());

				// Remove the build properties from the buildscript & main ext blocks
				BiConsumer<Optional<Set<Entry<String, String>>>, String> buildPropertiesConsumer =
					(buildProperties, propertyKey) -> buildProperties.ifPresent(theBuildProperties -> theBuildProperties.removeIf(buildProp -> StringUtils.equals(buildProp.getKey(), propertyKey)));

				buildPropertiesConsumer.accept(Optional.ofNullable((Set<Entry<String, String>>) model.get("buildPropertiesGradle")), "springBootVersion");
				buildPropertiesConsumer.accept(Optional.ofNullable((Set<Entry<String, String>>) model.get("buildPropertiesVersions")), "springCloudVersion");
				Optional.ofNullable((Set<Entry<String, String>>) model.get("buildPropertiesGradle")).ifPresent(buildPropertiesGradle -> buildPropertiesGradle.removeIf(buildPropertyGradle -> StringUtils.equals(buildPropertyGradle.getKey(), "springBootVersion")));
				Optional.ofNullable((Set<Entry<String, String>>) model.get("buildPropertiesVersions")).ifPresent(buildPropertiesVersions -> buildPropertiesVersions.removeIf(buildPropertiesVersion -> StringUtils.equals(buildPropertiesVersion.getKey(), "springCloudVersion")));
			}
		}

		return model;
	}

	private void setSpringBootVersion(String acidFrameworkVersion, Map<String, Object> model, ProjectRequest originalRequest) {
		String selectedSpringBootVersion = originalRequest.getBootVersion();
		String selectedSpringBootMajorVersion = getMajorVersion(selectedSpringBootVersion);
		String springBootMajorVersion = getMajorVersion(this.artifactoryService.getSpringBootVersion(acidFrameworkVersion, selectedSpringBootVersion));

		model.put("springBootMajorVersionMatchesAcidSpringBootVersion", StringUtils.equals(selectedSpringBootMajorVersion, springBootMajorVersion));
	}

	private static String getMajorVersion(String fullVersion) {
		return Optional.ofNullable(fullVersion)
			.map(MAJOR_VERSION_PATTERN::matcher)
			.filter(Matcher::find)
			.map(matcher -> matcher.group(1))
			.orElse(null);
	}

	private static boolean hasDependency(List<Dependency> dependencies, Predicate<Dependency> searchCriteria) {
		Assert.notNull(searchCriteria, "searchCriteria can't be null");

		return Optional.ofNullable(dependencies)
			.map(List::stream)
			.orElseGet(Stream::empty)
			.anyMatch(searchCriteria);
	}

	private static Optional<Dependency> findDependency(List<Dependency> dependencies, Predicate<Dependency> searchCriteria) {
		Assert.notNull(searchCriteria, "searchCriteria can't be null");

		return Optional.ofNullable(dependencies)
			.map(List::stream)
			.orElseGet(Stream::empty)
			.filter(searchCriteria)
			.findAny();
	}

	private static Optional<Dependency> findLombokDependency(List<Dependency> dependencies) {
		return findDependency(dependencies, dependency -> StringUtils.equals(dependency.getId(), "lombok"));
	}

	private static Optional<Dependency> findMapstructDependency(List<Dependency> dependencies) {
		return findDependency(dependencies, dependency -> StringUtils.equals(dependency.getId(), "mapstruct"));
	}

	private static boolean hasAcidServiceDependency(List<Dependency> dependencies) {
		return hasDependency(dependencies, dependency -> StringUtils.equals(dependency.getId(), "acid-service"));
	}

	private static boolean hasAcidDependency(List<Dependency> dependencies) {
		return hasDependency(dependencies, dependency -> StringUtils.startsWith(dependency.getId(), "acid-"));
	}

	private static boolean hasWebOrWebfluxDependency(List<Dependency> dependencies) {
		return hasDependency(dependencies, dependency -> StringUtils.equals(dependency.getId(), "web") || StringUtils.equals(dependency.getId(), "webflux"));
	}

	private void generateMainSourceSet(Map<String, Object> model, File projectDir, ProjectRequest request) {
		generateMainJavaSources(model, projectDir, request);
		generateMainResources(model, projectDir, request);
	}

	private void generateMainResources(Map<String, Object> model, File projectDir, ProjectRequest request) {
		LOGGER.info("Generating customizations to src/main/resources");

		var dependencies = request.getResolvedDependencies();

		if (hasAcidServiceDependency(dependencies)) {
			new File(projectDir, "src/main/resources/templates").delete();
			new File(projectDir, "src/main/resources/static").delete();
		}

		new File(projectDir, "src/main/resources/application.properties").delete();
		write(new File(projectDir, "src/main/resources/application.yml"), "application.yml", model);
	}

	private void generateMainJavaSources(Map<String, Object> model, File projectDir, ProjectRequest request) {
		LOGGER.info("Generating customizations to src/main/java");
		var applicationJavaSource = new File(new File(projectDir, "src/main/java"), request.getPackageName().replace(".", "/"));
		var configDir = new File(applicationJavaSource, "configuration");
		configDir.mkdirs();
		new File(projectDir, "src/javaClient/java").mkdirs();

		if (hasAcidServiceDependency(request.getResolvedDependencies())) {
			write(new File(configDir, "SwaggerConfig.java"), "SwaggerConfig.java", model);
		}
	}

	private void generateTestSourceSet(Map<String, Object> model, File projectDir, ProjectRequest request) {
		generateTestJavaSources(model, projectDir, request);
		generateTestResources(model, projectDir, request);
	}

	private void generateTestResources(Map<String, Object> model, File projectDir, ProjectRequest request) {
		LOGGER.info("Generating customizations to src/test/resources");
		var testResourcesDir = new File(projectDir, "src/test/resources");
		testResourcesDir.mkdirs();

		if (hasAcidServiceDependency(request.getResolvedDependencies())) {
			write(new File(testResourcesDir, "api.yml"), "api.yml", model);
		}
	}

	private void generateTestJavaSources(Map<String, Object> model, File projectDir, ProjectRequest request) {
		LOGGER.info("Generating customizations to src/test/java");
		File applicationJavaSource = new File(new File(projectDir, "src/test/java"), request.getPackageName().replace(".", "/"));

		if (hasAcidServiceDependency(request.getResolvedDependencies())) {
			write(new File(applicationJavaSource, "SwaggerDocumentationGenerator.java"), "SwaggerDocumentationGenerator.java", model);
		}
	}

	private File getInitializerProjectDir(File rootDir, ProjectRequest request) {
		if (StringUtils.isNotBlank(request.getBaseDir())) {
			var dir = new File(rootDir, request.getBaseDir());
			dir.mkdirs();

			return dir;
		}
		else {
			return rootDir;
		}
	}
}
