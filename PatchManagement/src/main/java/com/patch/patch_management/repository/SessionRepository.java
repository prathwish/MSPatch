package com.patch.patch_management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.patch.patch_management.entity.SessionEntity;

@Repository
public interface SessionRepository extends JpaRepository<SessionEntity, Integer>
{

	SessionEntity findByName(String name);

}
