package com.redhat.springinitializr.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ProjectGenerationStatistic implements Serializable {
	private String generationId;
	private Date generationDateTime;
	private String generationTimestamp;
	private String requestIp;
	private String requestIpv4;
	private String requestCountry;
	private String requestHost;
	private String requestUser;
	private String requestUserAgent;
	private String clientId;
	private String clientVersion;
	private String clientName;
	private String clientOSName;
	private String clientOSVersion;
	private String groupId;
	private String artifactId;
	private String packageName;
	private String bootVersion;
	private String javaVersion;
	private String language;
	private String packaging;
	private String type;
	private List<String> dependencies = new ArrayList<>();
	private String errorMessage;
	private boolean invalid;
	private boolean invalidJavaVersion;
	private boolean invalidLanguage;
	private boolean invalidPackaging;
	private boolean invalidType;
	private List<String> invalidDependencies = new ArrayList<>();

	public String getGenerationId() {
		return this.generationId;
	}

	public void setGenerationId(String generationId) {
		this.generationId = generationId;
	}

	public Date getGenerationDateTime() {
		return this.generationDateTime;
	}

	public void setGenerationDateTime(Date generationDateTime) {
		this.generationDateTime = generationDateTime;
	}

	public String getGenerationTimestamp() {
		return this.generationTimestamp;
	}

	public void setGenerationTimestamp(String generationTimestamp) {
		this.generationTimestamp = generationTimestamp;
	}

	public String getRequestIp() {
		return this.requestIp;
	}

	public void setRequestIp(String requestIp) {
		this.requestIp = requestIp;
	}

	public String getRequestIpv4() {
		return this.requestIpv4;
	}

	public void setRequestIpv4(String requestIpv4) {
		this.requestIpv4 = requestIpv4;
	}

	public String getRequestCountry() {
		return this.requestCountry;
	}

	public void setRequestCountry(String requestCountry) {
		this.requestCountry = requestCountry;
	}

	public String getRequestHost() {
		return this.requestHost;
	}

	public void setRequestHost(String requestHost) {
		this.requestHost = requestHost;
	}

	public String getRequestUser() {
		return this.requestUser;
	}

	public void setRequestUser(String requestUser) {
		this.requestUser = requestUser;
	}

	public String getRequestUserAgent() {
		return this.requestUserAgent;
	}

	public void setRequestUserAgent(String requestUserAgent) {
		this.requestUserAgent = requestUserAgent;
	}

	public String getClientId() {
		return this.clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientVersion() {
		return this.clientVersion;
	}

	public void setClientVersion(String clientVersion) {
		this.clientVersion = clientVersion;
	}

	public String getClientName() {
		return this.clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientOSName() {
		return this.clientOSName;
	}

	public void setClientOSName(String clientOSName) {
		this.clientOSName = clientOSName;
	}

	public String getClientOSVersion() {
		return this.clientOSVersion;
	}

	public void setClientOSVersion(String clientOSVersion) {
		this.clientOSVersion = clientOSVersion;
	}

	public String getGroupId() {
		return this.groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return this.artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getPackageName() {
		return this.packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getBootVersion() {
		return this.bootVersion;
	}

	public void setBootVersion(String bootVersion) {
		this.bootVersion = bootVersion;
	}

	public String getJavaVersion() {
		return this.javaVersion;
	}

	public void setJavaVersion(String javaVersion) {
		this.javaVersion = javaVersion;
	}

	public String getLanguage() {
		return this.language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPackaging() {
		return this.packaging;
	}

	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<String> getDependencies() {
		return this.dependencies;
	}

	public void setDependencies(List<String> dependencies) {
		this.dependencies = dependencies;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isInvalid() {
		return this.invalid;
	}

	public void setInvalid(boolean invalid) {
		this.invalid = invalid;
	}

	public boolean isInvalidJavaVersion() {
		return this.invalidJavaVersion;
	}

	public void setInvalidJavaVersion(boolean invalidJavaVersion) {
		this.invalidJavaVersion = invalidJavaVersion;
	}

	public boolean isInvalidLanguage() {
		return this.invalidLanguage;
	}

	public void setInvalidLanguage(boolean invalidLanguage) {
		this.invalidLanguage = invalidLanguage;
	}

	public boolean isInvalidPackaging() {
		return this.invalidPackaging;
	}

	public void setInvalidPackaging(boolean invalidPackaging) {
		this.invalidPackaging = invalidPackaging;
	}

	public boolean isInvalidType() {
		return this.invalidType;
	}

	public void setInvalidType(boolean invalidType) {
		this.invalidType = invalidType;
	}

	public List<String> getInvalidDependencies() {
		return this.invalidDependencies;
	}

	public void setInvalidDependencies(List<String> invalidDependencies) {
		this.invalidDependencies = invalidDependencies;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.generationId);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj.getClass() != getClass()) {
			return false;
		}

		ProjectGenerationStatistic rhs = (ProjectGenerationStatistic) obj;
		return new EqualsBuilder().append(this.generationId, rhs.generationId).isEquals();
	}
}
