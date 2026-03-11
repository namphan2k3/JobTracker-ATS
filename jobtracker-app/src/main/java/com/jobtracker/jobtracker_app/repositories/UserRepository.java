package com.jobtracker.jobtracker_app.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jobtracker.jobtracker_app.entities.User;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {
    // N+1 QUERY
//    @Query(value = "SELECT * FROM users u " +
//            "WHERE u.deleted_at IS NULL " +
//            "AND (:keyword IS NULL OR " +
//            "u.email LIKE CONCAT('%', :keyword, '%') OR " +
//            "u.first_name LIKE CONCAT('%', :keyword, '%') OR " +
//            "u.last_name LIKE CONCAT('%', :keyword, '%') OR " +
//            "u.phone LIKE CONCAT('%', :keyword, '%')" +
//            ") " +
//            "AND (:roleId IS NULL OR u.role_id = :roleId) " +
//            "AND (:isActive IS NULL OR u.is_active = :isActive) " +
//            "AND (:emailVerified IS NULL OR u.email_verified = :emailVerified) " +
//            "ORDER BY u.created_at DESC", nativeQuery = true)

    @Query(value = "SELECT u FROM User u " +
            "LEFT JOIN FETCH u.role r " +
            "WHERE u.deletedAt IS NULL " +
            "AND u.company.id = :companyId " +
            "AND (:keyword IS NULL OR " +
            "u.email LIKE CONCAT('%', :keyword, '%') OR " +
            "u.firstName LIKE CONCAT('%', :keyword, '%') OR " +
            "u.lastName LIKE CONCAT('%', :keyword, '%') OR " +
            "u.phone LIKE CONCAT('%', :keyword, '%')" +
            ") " +
            "AND (:roleId IS NULL OR r.id = :roleId) " +
            "AND (:isActive IS NULL OR u.isActive = :isActive) " +
            "AND (:emailVerified IS NULL OR u.emailVerified = :emailVerified) " +
            "ORDER BY u.createdAt DESC")
    Page<User> findAllAndSearch(
            @Param("companyId") String companyId,
            @Param("keyword") String keyword,
            @Param("roleId") String roleId,
            @Param("isActive") Boolean isActive,
            @Param("emailVerified") Boolean emailVerified,
            Pageable pageable
    );

    boolean existsByEmail(String email);

    boolean existsByEmailAndCompany_Id(String email, String companyId);

    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndCompany_IdAndDeletedAtIsNull(String id, String companyId);

    Optional<User> findByIdAndDeletedAtIsNull(String id);

    Optional<User> findByCompany_Id(String companyId);

    long countByCompany_IdAndIsBillableTrueAndDeletedAtIsNull(String companyId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select u from User u
    where u.id in :ids
      and u.company.id = :companyId
      and u.deletedAt is null
    """)
    List<User> findForUpdate(Set<String> ids, String companyId);
}
