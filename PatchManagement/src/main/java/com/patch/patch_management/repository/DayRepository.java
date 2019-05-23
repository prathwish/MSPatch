package com.patch.patch_management.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.patch.patch_management.entity.DayEntity;

@Repository
public interface DayRepository extends JpaRepository<DayEntity, Integer>
{

	DayEntity findByName(String value);


}
