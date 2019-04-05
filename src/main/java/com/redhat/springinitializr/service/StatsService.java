package com.redhat.springinitializr.service;

import java.util.Collection;

import com.redhat.springinitializr.model.ProjectGenerationStatistic;

public interface StatsService {
	Collection<ProjectGenerationStatistic> getAllStatisticsOrdered();
}
