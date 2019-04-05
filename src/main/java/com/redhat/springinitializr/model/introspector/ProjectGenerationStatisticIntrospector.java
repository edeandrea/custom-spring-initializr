package com.redhat.springinitializr.model.introspector;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import com.redhat.springinitializr.model.ProjectGenerationStatistic;

public final class ProjectGenerationStatisticIntrospector {
	private static ProjectGenerationStatisticIntrospector instance = null;
	private Map<String, Method> gettersMap = new LinkedHashMap<>();

	private ProjectGenerationStatisticIntrospector() {
		super();
	}

	public static synchronized ProjectGenerationStatisticIntrospector getInstance() {
		if (instance == null) {
			instance = new ProjectGenerationStatisticIntrospector();
			instance.initialize();
		}

		return instance;
	}

	public Set<String> getPropertyNames() {
		return Collections.unmodifiableSet(this.gettersMap.keySet());
	}

	public Optional<Object> getValue(ProjectGenerationStatistic statistic, String fieldName) {
		Assert.notNull(StringUtils.trimToNull(fieldName), "Field name can not be null");
		Assert.notNull(statistic, "Statistic can't be null");

		return Optional.ofNullable(fieldName)
			.map(StringUtils::trimToNull)
			.map(this.gettersMap::get)
			.map(getterMethod -> ReflectionUtils.invokeMethod(getterMethod, statistic));
	}

	private void initialize() {
		this.gettersMap.putAll(
			getBeanInfo()
				.map(BeanInfo::getPropertyDescriptors)
				.filter(Objects::nonNull)
				.map(Arrays::stream)
				.orElseGet(Stream::empty)
				.filter(propertyDescriptor -> Objects.nonNull(propertyDescriptor.getReadMethod()))
				.collect(Collectors.toMap(PropertyDescriptor::getName, PropertyDescriptor::getReadMethod))
		);
	}

	private static Optional<BeanInfo> getBeanInfo() {
		try {
			return Optional.ofNullable(Introspector.getBeanInfo(ProjectGenerationStatistic.class));
		}
		catch (IntrospectionException ex) {
			return Optional.empty();
		}
	}
}
