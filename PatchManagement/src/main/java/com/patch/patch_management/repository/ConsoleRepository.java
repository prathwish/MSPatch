package com.patch.patch_management.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.patch.patch_management.entity.ConsoleEntity;

@Repository
public interface ConsoleRepository extends JpaRepository<ConsoleEntity, Integer>
{

	ConsoleEntity findByName(String value);

}
