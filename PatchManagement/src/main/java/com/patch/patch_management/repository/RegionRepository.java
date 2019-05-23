package com.patch.patch_management.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.patch.patch_management.entity.RegionEntity;

@Repository
public interface RegionRepository extends JpaRepository<RegionEntity, Integer>
{

	RegionEntity findByName(String value);


}
