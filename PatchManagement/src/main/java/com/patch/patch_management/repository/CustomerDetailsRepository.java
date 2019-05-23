package com.patch.patch_management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.patch.patch_management.entity.CustomerDetailsEntity;

@Repository
public interface CustomerDetailsRepository extends JpaRepository<CustomerDetailsEntity, Integer>
{

	List<CustomerDetailsEntity> findBySiteKey(String siteKey);

}
