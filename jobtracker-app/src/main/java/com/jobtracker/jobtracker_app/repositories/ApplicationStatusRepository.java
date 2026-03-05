package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationStatusRepository extends JpaRepository<ApplicationStatus, String>, JpaSpecificationExecutor<ApplicationStatus> {

    Optional<ApplicationStatus> findByNameAndCompany_IdAndDeletedAtIsNull(String name, String companyId);

    Optional<ApplicationStatus> findByIdAndCompany_IdAndDeletedAtIsNull(String id, String companyId);

    Optional<ApplicationStatus> findByCompany_IdAndIsDefaultTrueAndDeletedAtIsNull(String companyId);

    Optional<ApplicationStatus> findByCompanyIsNullAndIsDefaultTrueAndDeletedAtIsNull();

    Optional<ApplicationStatus> findByIdAndCompany_IdAndIsActiveTrueAndDeletedAtIsNull(String id, String companyId);

    List<ApplicationStatus> findByCompany_IdAndDeletedAtIsNull(String companyId);

    @Query("SELECT s FROM ApplicationStatus s WHERE (s.company.id = :companyId OR s.company IS NULL) " +
            "AND s.isActive = true AND s.deletedAt IS NULL ORDER BY s.sortOrder")
    List<ApplicationStatus> findActiveStatusesForDashboard(@Param("companyId") String companyId);
}

