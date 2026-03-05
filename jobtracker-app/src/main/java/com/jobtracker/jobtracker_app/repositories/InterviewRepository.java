package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Interview;
import com.jobtracker.jobtracker_app.enums.InterviewStatus;
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
}




