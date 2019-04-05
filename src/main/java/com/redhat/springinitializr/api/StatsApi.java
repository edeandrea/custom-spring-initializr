package com.redhat.springinitializr.api;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.redhat.springinitializr.model.ProjectGenerationStatistic;
import com.redhat.springinitializr.service.StatsService;

@Controller
@RequestMapping("/stats")
public class StatsApi {
	private final StatsService statsService;

	public StatsApi(StatsService statsService) {
		this.statsService = statsService;
	}

	@GetMapping(path = "/stats.xlsx")
	public StatsExcelView getStats(Model model) {
		Collection<ProjectGenerationStatistic> stats = Optional.ofNullable(this.statsService.getAllStatisticsOrdered())
			.map(Collection::stream)
			.orElseGet(Stream::empty)
			.collect(Collectors.toUnmodifiableList());

		model.addAttribute("stats", stats);

		return new StatsExcelView();
	}
}
