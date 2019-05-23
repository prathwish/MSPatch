package com.patch.patch_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.patch.patch_management.entity.TimeZoneDetailsEntity;

public interface TimeZoneDetailsRepository extends JpaRepository<TimeZoneDetailsEntity, Integer>
{

	List<TimeZoneDetailsEntity> findByTimeZoneEntityId_Id(Integer value);

	TimeZoneDetailsEntity findByName(String value);
}
