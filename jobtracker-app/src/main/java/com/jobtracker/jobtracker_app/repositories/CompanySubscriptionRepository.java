package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.CompanySubscription;
import com.jobtracker.jobtracker_app.enums.SubscriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CompanySubscriptionRepository extends JpaRepository<CompanySubscription, String>, JpaSpecificationExecutor<CompanySubscription> {


    @Query(value = "SELECT cs " +
            "FROM CompanySubscription cs " +
            "WHERE cs.company.id = :companyId " +
            "AND cs.status = :status " +
            "ORDER BY cs.startDate DESC")
    Optional<CompanySubscription> findLatestSubscription(@Param("companyId") String companyId,
                                                         @Param("status") SubscriptionStatus status);

    List<CompanySubscription> findByCompany_IdAndStatus(String companyId, SubscriptionStatus status);

    Page<CompanySubscription> findByCompany_Id(String companyId, Pageable pageable);

    Optional<CompanySubscription> findByIdAndCompany_Id(String id, String companyId);

    List<CompanySubscription> findByStatusAndEndDateIsNotNullAndEndDateBefore(
            SubscriptionStatus status, LocalDateTime date);
}


