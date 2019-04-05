package com.redhat.springinitializr.repository;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.socialsignin.spring.data.dynamodb.repository.EnableScanCount;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.redhat.springinitializr.repository.entity.ProjectRequest;

@EnableScan
@EnableScanCount
public interface StatsRepository extends PagingAndSortingRepository<ProjectRequest, String> {
}
