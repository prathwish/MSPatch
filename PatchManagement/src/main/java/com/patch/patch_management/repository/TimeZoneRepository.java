package com.patch.patch_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.patch.patch_management.entity.RegionEntity;
import com.patch.patch_management.entity.TimeZoneEntity;

@Repository
public interface TimeZoneRepository extends JpaRepository<TimeZoneEntity, Integer>
{

	TimeZoneEntity findByName(String value);
	
	List<TimeZoneEntity> findByRegionEntityId_Id(Integer value);

}
