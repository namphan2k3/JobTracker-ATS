package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.dto.responses.dashboard.ApplicationsByStatusResponse;
import com.jobtracker.jobtracker_app.entities.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {

    @Query("SELECT a FROM Application a LEFT JOIN FETCH a.job LEFT JOIN FETCH a.status WHERE a.id = :id AND a.company.id = :companyId AND a.deletedAt IS NULL")
    Optional<Application> findByIdAndCompany_IdWithJobAndStatus(@Param("id") String id, @Param("companyId") String companyId);

    Optional<Application> findByIdAndCompany_IdAndDeletedAtIsNull(String id, String companyId);

    boolean existsByIdAndCompany_Id(String id, String companyId);

    long countByCompany_IdAndDeletedAtIsNull(String companyId);

    Optional<Application> findByApplicationTokenAndDeletedAtIsNull(String applicationToken);

    @Query("SELECT a FROM Application a WHERE a.company.id = :companyId AND a.deletedAt IS NULL " +
            "AND (:status IS NULL OR :status = '' OR a.status.name = :status) " +
            "AND (:jobId IS NULL OR :jobId = '' OR a.job.id = :jobId) " +
            "AND (:assignedTo IS NULL OR :assignedTo = '' OR a.assignedTo.id = :assignedTo) " +
            "AND (:search IS NULL OR :search = '' OR LOWER(a.candidateName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(a.candidateEmail) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:minMatchScore IS NULL OR a.matchScore >= :minMatchScore) " +
            "AND (:maxMatchScore IS NULL OR a.matchScore <= :maxMatchScore)")
    Page<Application> searchApplications(
            @Param("companyId") String companyId,
            @Param("status") String status,
            @Param("jobId") String jobId,
            @Param("assignedTo") String assignedTo,
            @Param("search") String search,
            @Param("minMatchScore") Integer minMatchScore,
            @Param("maxMatchScore") Integer maxMatchScore,
            Pageable pageable);

    long countByCompany_IdAndAppliedDateAndDeletedAtIsNull(String companyId, LocalDate appliedDate);

    @Query("""
            SELECT new com.jobtracker.jobtracker_app.dto.responses.dashboard.ApplicationsByStatusResponse(
                a.status.id,
                a.status.name,
                a.status.displayName,
                COUNT(a)
            )
            FROM Application a
            WHERE a.company.id = :companyId AND a.deletedAt IS NULL
            GROUP BY a.status.id, a.status.name, a.status.displayName
            """)
    List<ApplicationsByStatusResponse> countByStatusGroupByCompany(@Param("companyId") String companyId);
}

