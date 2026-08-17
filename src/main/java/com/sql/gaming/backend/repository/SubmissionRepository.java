package com.sql.gaming.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sql.gaming.backend.entity.Submission;
import com.sql.gaming.backend.enums.SubmissionStatus;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

	// =========================================================
	// FIND STUDENT SUBMISSIONS
	// =========================================================

	List<Submission> findByStudent_UserNameOrderByCreatedAtDesc(String userName);

	// =========================================================
	// FIND SUBMISSION OF A STUDENT
	// =========================================================

	Optional<Submission> findByIdAndStudent_UserName(Long submissionId, String userName);

	// =========================================================
	// COUNT ATTEMPTS
	// =========================================================

	long countByStudent_UserNameAndProblem_Id(String userName, Long problemId);

	// =========================================================
	// FIND BY STATUS
	// =========================================================

	List<Submission> findByStatus(SubmissionStatus status);

	/**
	 * Get the latest attempt number for a student/problem.
	 */
	@Query("""
			    SELECT COALESCE(MAX(s.attemptNumber), 0)
			    FROM Submission s
			    WHERE s.student.userName = :userName
			      AND s.problem.id = :problemId
			""")
	Integer findMaxAttemptNumber(@Param("userName") String userName, @Param("problemId") Long problemId);

	/**
	 * Get submissions of a particular student for a particular problem.
	 */
	List<Submission> findByStudentUserNameAndProblemIdOrderByAttemptNumberDesc(String userName, Long problemId);

	/**
	 * Find one submission belonging to a student.
	 */
	Optional<Submission> findByIdAndStudentUserName(Long submissionId, String userName);

	@Query("""
			    SELECT DISTINCT s
			    FROM Submission s
			    JOIN FETCH s.problem p
			    LEFT JOIN FETCH p.tables pt
			    LEFT JOIN FETCH pt.sqlTable st
			    WHERE s.id = :submissionId
			""")
	Optional<Submission> findSubmissionForProcessing(@Param("submissionId") Long submissionId);

	@Query("""
			    SELECT COALESCE(SUM(s.score), 0)
			    FROM Submission s
			    WHERE s.student.userName = :userName
			      AND s.status =
			          com.sql.gaming.backend.enums.SubmissionStatus.ACCEPTED
			""")
	Integer getTotalScore(@Param("userName") String userName);

	@Query("""
			    SELECT COUNT(DISTINCT s.problem.id)
			    FROM Submission s
			    WHERE s.student.userName = :userName
			      AND s.status =
			          com.sql.gaming.backend.enums.SubmissionStatus.ACCEPTED
			""")
	Long getProblemsSolved(@Param("userName") String userName);

	@Query("""
			    SELECT COUNT(DISTINCT s.problem.id)
			    FROM Submission s
			    WHERE s.student.userName = :userName
			      AND s.status =
			          com.sql.gaming.backend.enums.SubmissionStatus.ACCEPTED
			      AND s.problem.difficulty =
			          com.sql.gaming.backend.enums.Difficulty.EASY
			""")
	Long getEasySolved(@Param("userName") String userName);

	@Query("""
			    SELECT COUNT(DISTINCT s.problem.id)
			    FROM Submission s
			    WHERE s.student.userName = :userName
			      AND s.status =
			          com.sql.gaming.backend.enums.SubmissionStatus.ACCEPTED
			      AND s.problem.difficulty =
			          com.sql.gaming.backend.enums.Difficulty.MEDIUM
			""")
	Long getMediumSolved(@Param("userName") String userName);

	@Query("""
			    SELECT COUNT(DISTINCT s.problem.id)
			    FROM Submission s
			    WHERE s.student.userName = :userName
			      AND s.status =
			          com.sql.gaming.backend.enums.SubmissionStatus.ACCEPTED
			      AND s.problem.difficulty =
			          com.sql.gaming.backend.enums.Difficulty.HARD
			""")
	Long getHardSolved(@Param("userName") String userName);

	@Query("""
			    SELECT COUNT(s.id)
			    FROM Submission s
			    WHERE s.student.userName = :userName
			""")
	Long getTotalSubmissions(@Param("userName") String userName);

	@Query("""
			    SELECT COUNT(s.id)
			    FROM Submission s
			    WHERE s.student.userName = :userName
			      AND s.status =
			          com.sql.gaming.backend.enums.SubmissionStatus.ACCEPTED
			""")
	Long getAcceptedSubmissions(@Param("userName") String userName);
	
	@Query("""
		    SELECT s
		    FROM Submission s
		    JOIN FETCH s.problem
		    JOIN FETCH s.student
		    WHERE s.id = :submissionId
		""")
		Optional<Submission> findByIdWithProblem(
		        @Param("submissionId") Long submissionId
		);
}
