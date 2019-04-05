package com.redhat.springinitializr.mappings.mapstruct;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.redhat.springinitializr.model.ProjectGenerationStatistic;
import com.redhat.springinitializr.repository.entity.ProjectRequest;

@Mapper
public interface RequestStatisticMapper {

	RequestStatisticMapper INSTANCE = Mappers.getMapper(RequestStatisticMapper.class);

	ProjectRequest statsToRequest (ProjectGenerationStatistic stats);

	@InheritInverseConfiguration
	ProjectGenerationStatistic requestToStats (ProjectRequest request);
}
