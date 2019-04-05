package com.redhat.springinitializr;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.redhat.springinitializr.mappings.mapstruct.RequestDocumentMapper;
import com.redhat.springinitializr.repository.entity.ProjectRequest;
import io.spring.initializr.actuate.stat.ProjectRequestDocument;

public class DocumentMapperTests {

	@Test
	public void mapRequestToDoc(){

		String generationId = "id";
		Date generationDateTime = new Date();
		String generationTimeStamp = "1234";
		String requestIp = "requestip";
		String requestIpv4 = "requestipv4";
		String requestCountry = "US";
		String requestHost = "host";
		String requestUser = "user";
		String requestUserAgent = "userAgent";
		String clientId = "clientId";
		String clientVersion = "clientVersion";
		String clientName = "clientName";
		String clientOSName = "clientOSName";
		String clientOSVersion = "clientOSVersion";
		String groupId = "groupId";
		String artifactId = "artifactId";
		String packageName = "packageName";
		String bootVersion = "bootVersion";
		String javaVersion = "javaVersion";
		String language = "language";
		String packaging = "packaging";
		String type = "type";
		List<String> dependencies = new ArrayList<>();
		dependencies.add("d1");
		dependencies.add("d2");
		String errorMessage = "error";
		boolean invalid = false;
		boolean invalidJavaVersion = false;
		boolean invalidLanguage = false;
		boolean invalidPackaging = false;
		boolean invalidType = false;
		List<String> invalidDependencies = new ArrayList<>();
		invalidDependencies.add("d3");

		ProjectRequest pr = new ProjectRequest();

		pr.setGenerationId(generationId);
		pr.setGenerationDateTime(generationDateTime);
		pr.setGenerationTimestamp(generationTimeStamp);
		pr.setRequestIp(requestIp);
		pr.setRequestIpv4(requestIpv4);
		pr.setRequestCountry(requestCountry);
		pr.setRequestHost(requestHost);
		pr.setRequestUser(requestUser);
		pr.setRequestUserAgent(requestUserAgent);
		pr.setClientId(clientId);
		pr.setClientVersion(clientVersion);
		pr.setClientName(clientName);
		pr.setClientOSName(clientOSName);
		pr.setClientOSVersion(clientOSVersion);
		pr.setGroupId(groupId);
		pr.setArtifactId(artifactId);
		pr.setPackageName(packageName);
		pr.setBootVersion(bootVersion);
		pr.setJavaVersion(javaVersion);
		pr.setLanguage(language);
		pr.setPackaging(packaging);
		pr.setType(type);
		pr.setDependencies(dependencies);
		pr.setErrorMessage(errorMessage);
		pr.setInvalid(invalid);
		pr.setInvalidJavaVersion(invalidJavaVersion);
		pr.setInvalidLanguage(invalidLanguage);
		pr.setInvalidPackaging(invalidPackaging);
		pr.setInvalidType(invalidType);
		pr.setInvalidDependencies(invalidDependencies);

		ProjectRequestDocument prd = RequestDocumentMapper.INSTANCE.requestToDocument(pr);

		assertThat(prd)
			.isNotNull()
			.extracting(
				ProjectRequestDocument::getRequestIp,
				ProjectRequestDocument::getRequestIpv4,
				ProjectRequestDocument::getRequestCountry,
				ProjectRequestDocument::getClientId,
				ProjectRequestDocument::getClientVersion,
				ProjectRequestDocument::getGroupId,
				ProjectRequestDocument::getArtifactId,
				ProjectRequestDocument::getPackageName,
				ProjectRequestDocument::getBootVersion,
				ProjectRequestDocument::getJavaVersion,
				ProjectRequestDocument::getPackaging,
				ProjectRequestDocument::getType,
				ProjectRequestDocument::getDependencies,
				ProjectRequestDocument::getErrorMessage,
				ProjectRequestDocument::isInvalid,
				ProjectRequestDocument::isInvalidJavaVersion,
				ProjectRequestDocument::isInvalidPackaging,
				ProjectRequestDocument::isInvalidType,
				ProjectRequestDocument::getInvalidDependencies
			)
			.containsExactly(
				pr.getRequestIp(),
				pr.getRequestIpv4(),
				pr.getRequestCountry(),
				pr.getClientId(),
				pr.getClientVersion(),
				pr.getGroupId(),
				pr.getArtifactId(),
				pr.getPackageName(),
				pr.getBootVersion(),
				pr.getJavaVersion(),
				pr.getPackaging(),
				pr.getType(),
				pr.getDependencies(),
				pr.getErrorMessage(),
				pr.isInvalid(),
				pr.isInvalidJavaVersion(),
				pr.isInvalidPackaging(),
				pr.isInvalidType(),
				pr.getInvalidDependencies()
			);

		assertThat(prd.getGenerationTimestamp()).isEqualTo(1234);

	}

	@Test
	public void mapDocToRequest(){

		long generationTimeStamp = 1234;
		String requestIp = "requestip";
		String requestIpv4 = "requestipv4";
		String requestCountry = "US";
		String clientId = "clientId";
		String clientVersion = "clientVersion";
		String groupId = "groupId";
		String artifactId = "artifactId";
		String packageName = "packageName";
		String bootVersion = "bootVersion";
		String javaVersion = "javaVersion";
		String language = "language";
		String packaging = "packaging";
		String type = "type";
		List<String> dependencies = new ArrayList<>();
		dependencies.add("d1");
		dependencies.add("d2");
		String errorMessage = "error";
		boolean invalid = false;
		boolean invalidJavaVersion = false;
		boolean invalidLanguage = false;
		boolean invalidPackaging = false;
		boolean invalidType = false;
		List<String> invalidDependencies = new ArrayList<>();
		invalidDependencies.add("d3");

		ProjectRequestDocument prd = new ProjectRequestDocument();

		prd.setGenerationTimestamp(generationTimeStamp);
		prd.setRequestIp(requestIp);
		prd.setRequestIpv4(requestIpv4);
		prd.setRequestCountry(requestCountry);
		prd.setClientId(clientId);
		prd.setClientVersion(clientVersion);
		prd.setGroupId(groupId);
		prd.setArtifactId(artifactId);
		prd.setPackageName(packageName);
		prd.setBootVersion(bootVersion);
		prd.setJavaVersion(javaVersion);
		prd.setLanguage(language);
		prd.setPackaging(packaging);
		prd.setType(type);
		prd.setErrorMessage(errorMessage);
		prd.setInvalid(invalid);
		prd.setInvalidJavaVersion(invalidJavaVersion);
		prd.setInvalidLanguage(invalidLanguage);
		prd.setInvalidPackaging(invalidPackaging);
		prd.setInvalidType(invalidType);

		ProjectRequest pr = RequestDocumentMapper.INSTANCE.documentToRequest(prd);

		assertThat(pr)
			.isNotNull()
			.extracting(
				ProjectRequest::getRequestIp,
				ProjectRequest::getRequestIpv4,
				ProjectRequest::getRequestCountry,
				ProjectRequest::getClientId,
				ProjectRequest::getClientVersion,
				ProjectRequest::getGroupId,
				ProjectRequest::getArtifactId,
				ProjectRequest::getPackageName,
				ProjectRequest::getBootVersion,
				ProjectRequest::getJavaVersion,
				ProjectRequest::getPackaging,
				ProjectRequest::getType,
				ProjectRequest::getDependencies,
				ProjectRequest::getErrorMessage,
				ProjectRequest::isInvalid,
				ProjectRequest::isInvalidJavaVersion,
				ProjectRequest::isInvalidPackaging,
				ProjectRequest::isInvalidType,
				ProjectRequest::getInvalidDependencies
			)
			.containsExactly(
				prd.getRequestIp(),
				prd.getRequestIpv4(),
				prd.getRequestCountry(),
				prd.getClientId(),
				prd.getClientVersion(),
				prd.getGroupId(),
				prd.getArtifactId(),
				prd.getPackageName(),
				prd.getBootVersion(),
				prd.getJavaVersion(),
				prd.getPackaging(),
				prd.getType(),
				prd.getDependencies(),
				prd.getErrorMessage(),
				prd.isInvalid(),
				prd.isInvalidJavaVersion(),
				prd.isInvalidPackaging(),
				prd.isInvalidType(),
				prd.getInvalidDependencies()
			);

		assertThat(pr).isNotNull();
		assertThat(pr.getGenerationTimestamp()).isEqualTo("1234");

	}

}
