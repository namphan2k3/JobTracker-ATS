package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Job;
import com.jobtracker.jobtracker_app.enums.JobStatus;
import com.jobtracker.jobtracker_app.enums.JobType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, String> {
    @EntityGraph(attributePaths = {
            "company",
            "jobStatus",
            "jobType"
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

    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT j FROM Job j " +
            "WHERE j.jobStatus = 'PUBLISHED' " +
            "AND j.deletedAt IS NULL " +
            "AND j.company.deletedAt IS NULL " +
            "AND j.company.isActive = true " +
            "AND (j.deadlineDate IS NULL OR j.deadlineDate >= :today) " +
            "AND (j.expiresAt IS NULL OR j.expiresAt >= :now) " +
            "AND (:companyId IS NULL OR j.company.id = :companyId) " +
            "AND (:jobType IS NULL OR j.jobType = :jobType) " +
            "AND (:isRemote IS NULL OR j.isRemote = :isRemote) " +
            "AND (:location IS NULL OR :location = '' OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) " +
            "AND (:search IS NULL OR :search = '' " +
            "OR LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(j.position) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Job> searchPublishedJobs(
            @Param("companyId") String companyId,
            @Param("jobType") JobType jobType,
            @Param("isRemote") Boolean isRemote,
            @Param("location") String location,
            @Param("search") String search,
            @Param("today") LocalDate today,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT j FROM Job j " +
            "WHERE j.id = :id " +
            "AND j.jobStatus = 'PUBLISHED' " +
            "AND j.deletedAt IS NULL " +
            "AND j.company.deletedAt IS NULL " +
            "AND j.company.isActive = true " +
            "AND (j.deadlineDate IS NULL OR j.deadlineDate >= :today) " +
            "AND (j.expiresAt IS NULL OR j.expiresAt >= :now)")
    Optional<Job> findPublishedJobById(
            @Param("id") String id,
            @Param("today") LocalDate today,
            @Param("now") LocalDateTime now
    );

    long countByCompany_IdAndDeletedAtIsNull(String companyId);

    long countByCompany_IdAndJobStatusAndDeletedAtIsNull(String companyId, JobStatus jobStatus);

    @Query(value = "SELECT COUNT(*) FROM jobs j WHERE j.company_id = :companyId AND j.job_status = 'PUBLISHED' " +
            "AND j.deleted_at IS NULL AND MONTH(j.published_at) = MONTH(CURRENT_DATE) AND YEAR(j.published_at) = YEAR(CURRENT_DATE)", nativeQuery = true)
    long countPublishedThisMonth(@Param("companyId") String companyId);

    @Query(value = "SELECT COUNT(*) FROM jobs j WHERE j.company_id = :companyId AND j.job_status = 'PUBLISHED' " +
            "AND j.deleted_at IS NULL AND MONTH(j.published_at) = MONTH(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH)) " +
            "AND YEAR(j.published_at) = YEAR(DATE_SUB(CURRENT_DATE, INTERVAL 1 MONTH))", nativeQuery = true)
    long countPublishedLastMonth(@Param("companyId") String companyId);

    @Query("SELECT j FROM Job j WHERE j.deadlineDate = :deadlineDate AND j.deletedAt IS NULL")
    List<Job> findByDeadlineDate(@Param("deadlineDate") LocalDate deadlineDate);
}
