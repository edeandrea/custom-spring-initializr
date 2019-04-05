package com.redhat.springinitializr.mappings.mapstruct;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.redhat.springinitializr.repository.entity.ProjectRequest;

@Mapper
public interface RequestDocumentMapper {
	RequestDocumentMapper INSTANCE = Mappers.getMapper(RequestDocumentMapper.class);

	ProjectRequest documentToRequest (io.spring.initializr.actuate.stat.ProjectRequestDocument doc);

	@InheritInverseConfiguration
	io.spring.initializr.actuate.stat.ProjectRequestDocument requestToDocument (ProjectRequest request);
}
