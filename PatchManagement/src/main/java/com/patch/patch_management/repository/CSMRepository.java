package com.patch.patch_management.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.patch.patch_management.entity.CSMDetailsEntity;

@Repository
public interface CSMRepository extends JpaRepository<CSMDetailsEntity, Integer>
{

	CSMDetailsEntity findBySiteKey(String siteKey);

}
