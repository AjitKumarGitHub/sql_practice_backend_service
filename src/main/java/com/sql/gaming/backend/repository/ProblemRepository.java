package com.sql.gaming.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sql.gaming.backend.enums.Difficulty;
import com.sql.gaming.backend.entity.Problem;
import com.sql.gaming.backend.pojo.User;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    /**
     * Find problem using its unique slug.
     */
    Optional<Problem> findBySlug(String slug);

    /**
     * Check whether a slug already exists.
     */
    boolean existsBySlug(String slug);

    /**
     * Get only active problems.
     */
    List<Problem> findByActiveTrue();

    /**
     * Get active problems of a particular difficulty.
     *
     * Example:
     * EASY
     * MEDIUM
     * HARD
     */
    List<Problem> findByDifficultyAndActiveTrue(
            Difficulty difficulty
    );

    /**
     * Get all problems created by a particular
     * faculty/admin user.
     */
    List<Problem> findByCreatedBy(User createdBy);

    /**
     * Get active problems created by a particular
     * faculty/admin user.
     */
    List<Problem> findByCreatedByAndActiveTrue(
            User createdBy
    );

    /**
     * Get problems by creator username.
     */
    List<Problem> findByCreatedByUserName(
            String userName
    );

    /**
     * Get active problems by creator username.
     */
    List<Problem> findByCreatedByUserNameAndActiveTrue(
            String userName
    );

}
