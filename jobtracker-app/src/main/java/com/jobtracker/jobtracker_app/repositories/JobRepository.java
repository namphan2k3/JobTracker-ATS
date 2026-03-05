package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Job;
import com.jobtracker.jobtracker_app.enums.JobStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, String> {
    @EntityGraph(attributePaths = {
            "company",
            "status",
            "jobType",
            "priority",
            "experienceLevel"
    })
    @Query("SELECT j FROM Job j " +
            "WHERE j.company.id = :companyId " +
            "AND j.deletedAt IS NULL " +
            "AND (:jobStatus IS NULL OR j.jobStatus = :jobStatus)" +
            "AND (:isRemote IS NULL OR j.isRemote = :isRemote)" +
            "AND (:search IS NULL " +
            "OR LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%'))" +
            "OR LOWER(j.position) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Job> searchJobs(
            @Param("companyId") String companyId,
            @Param("jobStatus") JobStatus jobStatus,
            @Param("isRemote") Boolean isRemote,
            @Param("search") String search,
            Pageable pageable
    );

    Optional<Job> findByIdAndDeletedAtIsNull(String id);

    Optional<Job> findByIdAndCompany_IdAndDeletedAtIsNull(String id, String companyId);

    long countByCompany_IdAndDeletedAtIsNull(String companyId);

    long countByCompany_IdAndJobStatusAndDeletedAtIsNull(String companyId, JobStatus jobStatus);

    @Query(value = "SELECT COUNT(*) FROM jobs j WHERE j.company_id = :companyId AND j.job_status = 'PUBLISHED' " +
            "AND j.deleted_at IS NULL AND MONTH(j.published_at) = MONTH(CURRENT_DATE) AND YEAR(j.published_at) = YEAR(CURRENT_DATE)", nativeQuery = true)
    long countPublishedThisMonth(@Param("companyId") String companyId);

    @Query(value = "SELECT COUNT(*) FROM jobs j WHERE j.company_id = :companyId AND j.job_status = 'PUBLISHED' " +
            "AND j.deleted_at IS NULL AND MONTH(j.published_at) = MONTH(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH)) " +
            "AND YEAR(j.published_at) = YEAR(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH))", nativeQuery = true)
    long countPublishedLastMonth(@Param("companyId") String companyId);
}
