package com.patch.patch_management.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.patch.patch_management.entity.CustomerMasterDataEntity;

@Repository
public interface CustomerMasterDataRepository extends JpaRepository<CustomerMasterDataEntity, Integer>
{

	CustomerMasterDataEntity findByHospitalId(Integer masterId);

	CustomerMasterDataEntity findBySiteKey(String string);

	List<CustomerMasterDataEntity> findAllByOrderByCustomerName();

}
