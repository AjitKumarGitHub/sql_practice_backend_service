package com.sql.gaming.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sql.gaming.backend.entity.Submission;

public interface LeaderboardRepository extends JpaRepository<Submission, Long> {

	// =========================================================
	// GLOBAL LEADERBOARD
	// =========================================================

	@Query(value = """
			SELECT
			    s.student.userName,
			    s.student.name,
			    s.student.batch,

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
			    END)

			FROM Submission s

			WHERE s.status =
			      com.sql.gaming.backend.enums.SubmissionStatus.ACCEPTED

			GROUP BY
			    s.student.userName,
			    s.student.name,
			    s.student.batch

			ORDER BY
			    COALESCE(SUM(s.score), 0) DESC,

			    COUNT(DISTINCT s.problem.id) DESC,

			    s.student.userName ASC
			""",

			countQuery = """
					SELECT COUNT(DISTINCT s.student.userName)

					FROM Submission s

					WHERE s.status =
					      com.sql.gaming.backend.enums.SubmissionStatus.ACCEPTED
					""")
	Page<Object[]> findGlobalLeaderboard(Pageable pageable);

	// =========================================================
	// BATCH LEADERBOARD
	// =========================================================

	@Query(value = """
			SELECT
			    s.student.userName,
			    s.student.name,
			    s.student.batch,

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
			    END)

			FROM Submission s

			WHERE s.status =
			      com.sql.gaming.backend.enums.SubmissionStatus.ACCEPTED

			  AND s.student.batch = :batch

			GROUP BY
			    s.student.userName,
			    s.student.name,
			    s.student.batch

			ORDER BY
			    COALESCE(SUM(s.score), 0) DESC,

			    COUNT(DISTINCT s.problem.id) DESC,

			    s.student.userName ASC
			""",

			countQuery = """
					SELECT COUNT(DISTINCT s.student.userName)

					FROM Submission s

					WHERE s.status =
					      com.sql.gaming.backend.enums.SubmissionStatus.ACCEPTED

					  AND s.student.batch = :batch
					""")
	Page<Object[]> findBatchLeaderboard(@Param("batch") String batch, Pageable pageable);

	// =========================================================
	// GLOBAL RANK
	// =========================================================

	@Query(value = """
			SELECT ranked.rank
			FROM (
			    SELECT
			        s.student_id AS user_name,

			        ROW_NUMBER() OVER (
			            ORDER BY
			                COALESCE(SUM(s.score), 0) DESC,
			                COUNT(DISTINCT s.problem_id) DESC,
			                s.student_id ASC
			        ) AS rank

			    FROM submissions s

			    JOIN users u
			        ON u.user_name = s.student_id

			    WHERE s.status = 'ACCEPTED'
			      AND u.role = 'ROLE_STUDENT'

			    GROUP BY
			        s.student_id

			) ranked

			WHERE ranked.user_name = :userName
			""", nativeQuery = true)
	Integer findGlobalRank(@Param("userName") String userName);

	// =========================================================
	// BATCH RANK
	// =========================================================

	@Query(value = """
			SELECT ranked.rank
			FROM (
			    SELECT
			        s.student_id AS user_name,

			        ROW_NUMBER() OVER (
			            ORDER BY
			                COALESCE(SUM(s.score), 0) DESC,
			                COUNT(DISTINCT s.problem_id) DESC,
			                s.student_id ASC
			        ) AS rank

			    FROM submissions s

			    JOIN users u
			        ON u.user_name = s.student_id

			    WHERE s.status = 'ACCEPTED'
			      AND u.role = 'ROLE_STUDENT'
			      AND u.batch = :batch

			    GROUP BY
			        s.student_id

			) ranked

			WHERE ranked.user_name = :userName
			""", nativeQuery = true)
	Integer findBatchRank(@Param("userName") String userName, @Param("batch") String batch);
}