package com.sql.gaming.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sql.gaming.backend.entity.Submission;

public interface StudentStatsRepository
        extends JpaRepository<Submission, Long> {

    @Query("""
        SELECT
            COALESCE(SUM(s.score), 0),
            COUNT(DISTINCT s.problem.id),
            COUNT(DISTINCT CASE
                WHEN s.problem.difficulty =
                    com.sql.gaming.backend.enums.Difficulty.EASY
                THEN s.problem.id
            END),
            COUNT(DISTINCT CASE
                WHEN s.problem.difficulty =
                    com.sql.gaming.backend.enums.Difficulty.MEDIUM
                THEN s.problem.id
            END),
            COUNT(DISTINCT CASE
                WHEN s.problem.difficulty =
                    com.sql.gaming.backend.enums.Difficulty.HARD
                THEN s.problem.id
            END),
            COUNT(s.id),
            COUNT(CASE
                WHEN s.status =
                    com.sql.gaming.backend.enums.SubmissionStatus.ACCEPTED
                THEN 1
            END)
        FROM Submission s
        WHERE s.student.userName = :userName
    """)
    List<Object[]> findStudentStatistics(
            @Param("userName") String userName
    );
}