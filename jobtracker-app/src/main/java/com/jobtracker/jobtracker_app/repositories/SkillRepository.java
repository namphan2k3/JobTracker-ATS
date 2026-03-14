package com.jobtracker.jobtracker_app.repositories;

import com.jobtracker.jobtracker_app.entities.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, String> {
    Optional<Skill> findByIdAndDeletedAtIsNull(@Param("id") String id);

    @Query("""
            SELECT s FROM Skill s
            WHERE s.deletedAt IS NULL
              AND (:name IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%')))
              AND (:category IS NULL OR LOWER(s.category) = LOWER(:category))
            """)
    Page<Skill> searchByNameAndCategory(@Param("name") String name,
                                        @Param("category") String category,
                                        Pageable pageable);

    Optional<Skill> findByNameIgnoreCase(String name);
}




