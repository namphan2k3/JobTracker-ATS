package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.EmailOutbox;
import com.jobtracker.jobtracker_app.enums.AggregateType;
import com.jobtracker.jobtracker_app.enums.EmailStatus;
import com.jobtracker.jobtracker_app.enums.EmailType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmailOutboxRepository extends JpaRepository<EmailOutbox, String> {
    @Query("""
            SELECT e FROM EmailOutbox e
            WHERE e.status = 'PENDING'
            AND (e.nextRetryAt IS NULL OR e.nextRetryAt <= CURRENT_TIMESTAMP)
            AND (e.retryCount < e.maxRetries)
            ORDER BY e.createdAt ASC
            """)
    List<EmailOutbox> findPendingEmails(Pageable pageable);

    Optional<EmailOutbox> findByIdAndCompany_Id(String id, String companyId);

    @Query("""
            SELECT e FROM EmailOutbox e
            WHERE e.company.id = :companyId
            AND (:status IS NULL OR e.status = :status)
            AND (:emailType IS NULL OR e.emailType = :emailType)
            AND (:aggregateType IS NULL OR :aggregateType = '' OR e.aggregateType = :aggregateType)
            AND (:aggregateId IS NULL OR :aggregateId = '' OR e.aggregateId = :aggregateId)
            AND (:toEmail IS NULL OR :toEmail = '' OR LOWER(e.toEmail) LIKE LOWER(CONCAT('%', :toEmail, '%')))
            AND (:startDate IS NULL OR e.createdAt >= :startDate)
            AND (:endDate IS NULL OR e.createdAt <= :endDate)
            ORDER BY e.createdAt DESC
            """)
    Page<EmailOutbox> searchEmailHistory(
            @Param("companyId") String companyId,
            @Param("status") EmailStatus status,
            @Param("emailType") EmailType emailType,
            @Param("aggregateType") AggregateType aggregateType,
            @Param("aggregateId") String aggregateId,
            @Param("toEmail") String toEmail,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
