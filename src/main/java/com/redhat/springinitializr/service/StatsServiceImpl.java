package com.redhat.springinitializr.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import com.redhat.springinitializr.mappings.mapstruct.RequestStatisticMapper;
import com.redhat.springinitializr.model.ProjectGenerationStatistic;
import com.redhat.springinitializr.repository.StatsRepository;

@Service
public class StatsServiceImpl implements StatsService {
	private static final Logger LOGGER = LoggerFactory.getLogger(StatsServiceImpl.class);
	private final StatsRepository statsRepository;

	public StatsServiceImpl(StatsRepository statsRepository) {
		this.statsRepository = statsRepository;
	}

	@Override
	public Collection<ProjectGenerationStatistic> getAllStatisticsOrdered() {
		return Optional.ofNullable(this.statsRepository.findAll())
			.map(Iterable::spliterator)
			.map(spliterator -> StreamSupport.stream(spliterator, false))
			.orElseGet(Stream::empty)
			.filter(Objects::nonNull)
			.peek(repoStat -> LOGGER.debug("Converting repo stat {} to {}", repoStat, ProjectGenerationStatistic.class.getName()))
			.map(RequestStatisticMapper.INSTANCE::requestToStats)
			.peek(modelStat -> LOGGER.debug("Converted to model stat {}", modelStat))
			.sorted(Comparator.comparing(ProjectGenerationStatistic::getGenerationDateTime))
			.collect(Collectors.toList());
	}
}
