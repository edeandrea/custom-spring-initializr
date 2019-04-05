package com.redhat.springinitializr.stats;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.StringUtils;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.redhat.springinitializr.config.AppProperties;
import com.redhat.springinitializr.mappings.mapstruct.RequestDocumentMapper;
import com.redhat.springinitializr.repository.StatsRepository;
import com.redhat.springinitializr.repository.entity.ProjectRequest;
import io.spring.initializr.actuate.stat.ProjectRequestDocumentFactory;
import io.spring.initializr.generator.ProjectRequestEvent;
import io.spring.initializr.metadata.InitializrMetadataProvider;
import io.spring.initializr.util.Agent;
import io.spring.initializr.util.Agent.AgentId;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

@Component
public class ProjectGenerationStatPublisher {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProjectGenerationStatPublisher.class);
	private static final Pattern IP_PATTERN = Pattern.compile("[0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*");

	private final ProjectRequestDocumentFactory documentFactory;
	private final StatsRepository statsRepository;
	private final AppProperties appProperties;

	private enum UserAgentField {
		OS_NAME("OperatingSystemName") {
			@Override
			public void setField(ProjectRequest request, String fieldValue) {
				request.setClientOSName(fieldValue);
			}
		},
		OS_VERSION("OperatingSystemVersion") {
			@Override
			public void setField(ProjectRequest request, String fieldValue) {
				request.setClientOSVersion(fieldValue);
			}
		},
		BROWSER_NAME("AgentName") {
			@Override
			public void setField(ProjectRequest request, String fieldValue) {
				request.setClientName(fieldValue);
			}
		},
		BROWSER_VERSION("AgentVersion") {
			@Override
			public void setField(ProjectRequest request, String fieldValue) {
				request.setClientVersion(fieldValue);
			}
		};

		private String fieldName;

		UserAgentField(String fieldName) {
			this.fieldName = fieldName;
		}

		public abstract void setField(ProjectRequest request, String fieldValue);

		public String getFieldName() {
			return this.fieldName;
		}
	}

	public ProjectGenerationStatPublisher(InitializrMetadataProvider metadataProvider, StatsRepository statsRepository, AppProperties appProperties) {
		this.documentFactory = new ProjectRequestDocumentFactory(metadataProvider);
		this.statsRepository = statsRepository;
		this.appProperties = appProperties;
	}

	@EventListener
	@Async
	public void handleEvent(ProjectRequestEvent event) {
		if (this.appProperties.isPublishStatistics()) {
			var document = this.documentFactory.createDocument(event);
			LOGGER.debug("Publishing {}", document);

			var projectRequest = RequestDocumentMapper.INSTANCE.documentToRequest(document);
			populateRequestAttributes(projectRequest, event.getProjectRequest());
			LOGGER.debug("Going to write to database: {}", projectRequest);

			var savedRequest = this.statsRepository.save(projectRequest);
			LOGGER.debug("Got response back: {}", savedRequest);
		}
	}

	private static void populateRequestAttributes(ProjectRequest request, io.spring.initializr.generator.ProjectRequest projectRequest) {
		var params = Optional.ofNullable(projectRequest)
			.map(io.spring.initializr.generator.ProjectRequest::getParameters);

		if (StringUtils.isBlank(StringUtils.trimToNull(request.getRequestIp()))) {
			request.setRequestIp(getParameter("requestIp", params));
		}

		if (StringUtils.isBlank(StringUtils.trimToNull(request.getRequestIpv4()))) {
			Optional.ofNullable(request.getRequestIp()).ifPresent(requestIp -> request.setRequestIpv4(extractIpv4(requestIp)));
		}

		request.setRequestHost(getParameter("requestHost", params));
		request.setRequestUser(getParameter("requestUser", params));
		request.setRequestUserAgent(getParameter("user-agent", params));

		Optional.ofNullable(request.getRequestUserAgent())
			.map(StringUtils::trimToNull)
			.filter(userAgent -> Agent.fromUserAgent(userAgent).getId() == AgentId.BROWSER)
			.ifPresent(userAgent -> {
					UserAgent ua = UserAgentAnalyzer
						.newBuilder()
						.withoutCache()
						.withFields(Stream.of(UserAgentField.values()).map(UserAgentField::getFieldName).collect(Collectors.toList()))
						.build()
						.parse(userAgent);

				Stream.of(UserAgentField.values()).forEach(userAgentField -> userAgentField.setField(request, ua.getValue(userAgentField.getFieldName())));
		});

		Optional.ofNullable(request.getGenerationTimestamp()).ifPresent(generationTimestamp -> {
			try {
				request.setGenerationDateTime(new Date(Long.parseLong(generationTimestamp)));
			}
			catch (NumberFormatException ex) {
				LOGGER.warn("Got {} while creating generationDateTime from {}", ex.getMessage(), generationTimestamp, ex);
			}
		});
	}

	private static String getParameter(String parameter, Optional<Map<String, Object>> params) {
		return params
			.map(paramsMap -> paramsMap.get(parameter))
			.filter(String.class::isInstance)
			.map(String.class::cast)
			.orElse(null);
	}

	private static String extractIpv4(String candidate) {
		return Optional.ofNullable(candidate)
			.map(StringUtils::trimToNull)
			.map(IP_PATTERN::matcher)
			.filter(Matcher::find)
			.map(Matcher::group)
			.orElse(null);
	}
}
