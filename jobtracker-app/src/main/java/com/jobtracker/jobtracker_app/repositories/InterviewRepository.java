package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Interview;
import com.jobtracker.jobtracker_app.enums.InterviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InterviewRepository extends JpaRepository<Interview, String> {
    @Query("SELECT i FROM Interview i LEFT JOIN FETCH i.interviewers WHERE i.application.id = :applicationId AND i.deletedAt IS NULL")
    List<Interview> findByApplicationIdWithInterviewers(String applicationId);

    Optional<Interview> findByIdAndDeletedAtIsNull(String id);

    Optional<Interview> findByIdAndCompany_IdAndDeletedAtIsNull(String id, String companyId);

    @Query("SELECT i FROM Interview i LEFT JOIN FETCH i.job LEFT JOIN FETCH i.application " +
            "WHERE i.company.id = :companyId AND i.deletedAt IS NULL AND i.status = :status " +
            "AND i.scheduledDate >= :fromDate ORDER BY i.scheduledDate ASC")
    List<Interview> findUpcomingByCompany(
            @Param("companyId") String companyId,
            @Param("status") InterviewStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            Pageable pageable);

    @Query(value = "SELECT DISTINCT i FROM Interview i LEFT JOIN FETCH i.interviewers " +
            "WHERE i.company.id = :companyId AND i.deletedAt IS NULL " +
            "AND (:applicationId IS NULL OR i.application.id = :applicationId) " +
            "AND (:jobId IS NULL OR i.job.id = :jobId) " +
            "AND (:interviewerId IS NULL OR EXISTS (SELECT 1 FROM InterviewInterviewer ii WHERE ii.interview.id = i.id AND ii.interviewer.id = :interviewerId AND (ii.isDeleted = false))) " +
            "AND (:status IS NULL OR i.status = :status) " +
            "AND (:fromDate IS NULL OR i.scheduledDate >= :fromDate) " +
            "AND (:toDate IS NULL OR i.scheduledDate <= :toDate) " +
            "ORDER BY i.scheduledDate DESC")
    List<Interview> searchByCompany(
            @Param("companyId") String companyId,
            @Param("applicationId") String applicationId,
            @Param("jobId") String jobId,
            @Param("interviewerId") String interviewerId,
            @Param("status") InterviewStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);

    @Query("SELECT COUNT(DISTINCT i) FROM Interview i " +
            "WHERE i.company.id = :companyId AND i.deletedAt IS NULL " +
            "AND (:applicationId IS NULL OR i.application.id = :applicationId) " +
            "AND (:jobId IS NULL OR i.job.id = :jobId) " +
            "AND (:interviewerId IS NULL OR EXISTS (SELECT 1 FROM InterviewInterviewer ii WHERE ii.interview.id = i.id AND ii.interviewer.id = :interviewerId AND (ii.isDeleted = false))) " +
            "AND (:status IS NULL OR i.status = :status) " +
            "AND (:fromDate IS NULL OR i.scheduledDate >= :fromDate) " +
            "AND (:toDate IS NULL OR i.scheduledDate <= :toDate)")
    long countByCompanyAndFilter(
            @Param("companyId") String companyId,
            @Param("applicationId") String applicationId,
            @Param("jobId") String jobId,
            @Param("interviewerId") String interviewerId,
            @Param("status") InterviewStatus status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate);

    @Query("SELECT i FROM Interview i " +
            "WHERE i.status = :status " +
            "AND i.deletedAt IS NULL " +
            "AND i.scheduledDate BETWEEN :fromDate AND :toDate")
    List<Interview> findRemindersWindow(@Param("status") InterviewStatus status,
                                        @Param("fromDate") LocalDateTime fromDate,
                                        @Param("toDate") LocalDateTime toDate);
}




