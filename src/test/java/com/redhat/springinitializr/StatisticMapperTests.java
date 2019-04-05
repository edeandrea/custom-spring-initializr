package com.redhat.springinitializr;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.redhat.springinitializr.mappings.mapstruct.RequestStatisticMapper;
import com.redhat.springinitializr.model.ProjectGenerationStatistic;
import com.redhat.springinitializr.repository.entity.ProjectRequest;

public class StatisticMapperTests {

	@Test
	public void statToRequestTest(){

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

		ProjectGenerationStatistic pgs = new ProjectGenerationStatistic();

		pgs.setGenerationId(generationId);
		pgs.setGenerationDateTime(generationDateTime);
		pgs.setGenerationTimestamp(generationTimeStamp);
		pgs.setRequestIp(requestIp);
		pgs.setRequestIpv4(requestIpv4);
		pgs.setRequestCountry(requestCountry);
		pgs.setRequestHost(requestHost);
		pgs.setRequestUser(requestUser);
		pgs.setRequestUserAgent(requestUserAgent);
		pgs.setClientId(clientId);
		pgs.setClientVersion(clientVersion);
		pgs.setClientName(clientName);
		pgs.setClientOSName(clientOSName);
		pgs.setClientOSVersion(clientOSVersion);
		pgs.setGroupId(groupId);
		pgs.setArtifactId(artifactId);
		pgs.setPackageName(packageName);
		pgs.setBootVersion(bootVersion);
		pgs.setJavaVersion(javaVersion);
		pgs.setLanguage(language);
		pgs.setPackaging(packaging);
		pgs.setType(type);
		pgs.setDependencies(dependencies);
		pgs.setErrorMessage(errorMessage);
		pgs.setInvalid(invalid);
		pgs.setInvalidJavaVersion(invalidJavaVersion);
		pgs.setInvalidLanguage(invalidLanguage);
		pgs.setInvalidPackaging(invalidPackaging);
		pgs.setInvalidType(invalidType);
		pgs.setInvalidDependencies(invalidDependencies);

		ProjectRequest pr = RequestStatisticMapper.INSTANCE.statsToRequest(pgs);

		assertThat(pr)
			.isNotNull()
			.extracting(
				ProjectRequest::getGenerationId,
				ProjectRequest::getGenerationDateTime,
				ProjectRequest::getGenerationTimestamp,
				ProjectRequest::getRequestIp,
				ProjectRequest::getRequestIpv4,
				ProjectRequest::getRequestCountry,
				ProjectRequest::getRequestHost,
				ProjectRequest::getRequestUser,
				ProjectRequest::getRequestUserAgent,
				ProjectRequest::getClientId,
				ProjectRequest::getClientVersion,
				ProjectRequest::getClientName,
				ProjectRequest::getClientOSName,
				ProjectRequest::getClientOSVersion,
				ProjectRequest::getGroupId,
				ProjectRequest::getArtifactId,
				ProjectRequest::getPackageName,
				ProjectRequest::getBootVersion,
				ProjectRequest::getJavaVersion,
				ProjectRequest::getLanguage,
				ProjectRequest::getPackaging,
				ProjectRequest::getType,
				ProjectRequest::getDependencies,
				ProjectRequest::getErrorMessage,
				ProjectRequest::isInvalid,
				ProjectRequest::isInvalidJavaVersion,
				ProjectRequest::isInvalidLanguage,
				ProjectRequest::isInvalidPackaging,
				ProjectRequest::isInvalidType,
				ProjectRequest::getInvalidDependencies
			)
			.containsExactly(
				pgs.getGenerationId(),
				pgs.getGenerationDateTime(),
				pgs.getGenerationTimestamp(),
				pgs.getRequestIp(),
				pgs.getRequestIpv4(),
				pgs.getRequestCountry(),
				pgs.getRequestHost(),
				pgs.getRequestUser(),
				pgs.getRequestUserAgent(),
				pgs.getClientId(),
				pgs.getClientVersion(),
				pgs.getClientName(),
				pgs.getClientOSName(),
				pgs.getClientOSVersion(),
				pgs.getGroupId(),
				pgs.getArtifactId(),
				pgs.getPackageName(),
				pgs.getBootVersion(),
				pgs.getJavaVersion(),
				pgs.getLanguage(),
				pgs.getPackaging(),
				pgs.getType(),
				pgs.getDependencies(),
				pgs.getErrorMessage(),
				pgs.isInvalid(),
				pgs.isInvalidJavaVersion(),
				pgs.isInvalidLanguage(),
				pgs.isInvalidPackaging(),
				pgs.isInvalidType(),
				pgs.getInvalidDependencies()
			);

	}

	@Test
	public void requestToStatTest(){

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

		ProjectGenerationStatistic pgs = RequestStatisticMapper.INSTANCE.requestToStats(pr);

		assertThat(pgs)
			.isNotNull()
			.extracting(
				ProjectGenerationStatistic::getGenerationId,
				ProjectGenerationStatistic::getGenerationDateTime,
				ProjectGenerationStatistic::getGenerationTimestamp,
				ProjectGenerationStatistic::getRequestIp,
				ProjectGenerationStatistic::getRequestIpv4,
				ProjectGenerationStatistic::getRequestCountry,
				ProjectGenerationStatistic::getRequestHost,
				ProjectGenerationStatistic::getRequestUser,
				ProjectGenerationStatistic::getRequestUserAgent,
				ProjectGenerationStatistic::getClientId,
				ProjectGenerationStatistic::getClientVersion,
				ProjectGenerationStatistic::getClientName,
				ProjectGenerationStatistic::getClientOSName,
				ProjectGenerationStatistic::getClientOSVersion,
				ProjectGenerationStatistic::getGroupId,
				ProjectGenerationStatistic::getArtifactId,
				ProjectGenerationStatistic::getPackageName,
				ProjectGenerationStatistic::getBootVersion,
				ProjectGenerationStatistic::getJavaVersion,
				ProjectGenerationStatistic::getLanguage,
				ProjectGenerationStatistic::getPackaging,
				ProjectGenerationStatistic::getType,
				ProjectGenerationStatistic::getDependencies,
				ProjectGenerationStatistic::getErrorMessage,
				ProjectGenerationStatistic::isInvalid,
				ProjectGenerationStatistic::isInvalidJavaVersion,
				ProjectGenerationStatistic::isInvalidLanguage,
				ProjectGenerationStatistic::isInvalidPackaging,
				ProjectGenerationStatistic::isInvalidType,
				ProjectGenerationStatistic::getInvalidDependencies
			)
			.containsExactly(
				pr.getGenerationId(),
				pr.getGenerationDateTime(),
				pr.getGenerationTimestamp(),
				pr.getRequestIp(),
				pr.getRequestIpv4(),
				pr.getRequestCountry(),
				pr.getRequestHost(),
				pr.getRequestUser(),
				pr.getRequestUserAgent(),
				pr.getClientId(),
				pr.getClientVersion(),
				pr.getClientName(),
				pr.getClientOSName(),
				pr.getClientOSVersion(),
				pr.getGroupId(),
				pr.getArtifactId(),
				pr.getPackageName(),
				pr.getBootVersion(),
				pr.getJavaVersion(),
				pr.getLanguage(),
				pr.getPackaging(),
				pr.getType(),
				pr.getDependencies(),
				pr.getErrorMessage(),
				pr.isInvalid(),
				pr.isInvalidJavaVersion(),
				pr.isInvalidLanguage(),
				pr.isInvalidPackaging(),
				pr.isInvalidType(),
				pr.getInvalidDependencies()
			);

	}
}
