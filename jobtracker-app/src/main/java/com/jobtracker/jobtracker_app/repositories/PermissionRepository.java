package com.jobtracker.jobtracker_app.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracker.jobtracker_app.entities.Permission;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, String> {
    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, String id);

    List<Permission> findAllByIdInAndIsActiveTrueAndDeletedAtIsNull(List<String> ids);
}
