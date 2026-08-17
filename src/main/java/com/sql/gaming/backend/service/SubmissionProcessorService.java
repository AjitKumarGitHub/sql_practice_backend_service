package com.sql.gaming.backend.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sql.gaming.backend.entity.Problem;
import com.sql.gaming.backend.entity.Submission;
import com.sql.gaming.backend.enums.Difficulty;
import com.sql.gaming.backend.enums.SubmissionStatus;
import com.sql.gaming.backend.exception.ResourceNotFoundException;
import com.sql.gaming.backend.pojo.SqlExecutionResult;
import com.sql.gaming.backend.repository.SubmissionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionProcessorService {

	private final SubmissionRepository submissionRepository;

	private final SqlExecutionService sqlExecutionService;

	private final ResultComparisonService resultComparisonService;

	/**
	 * Process SQL submission asynchronously.
	 *
	 * This method is executed by the thread pool defined in AsyncConfig.
	 */
	@Async("submissionExecutor")
	@Transactional
	public void processSubmissionAsync(Long submissionId) {

		log.info("Started asynchronous processing. submissionId={}", submissionId);

		try {

			/*
			 * -------------------------------------------------- 1. Mark submission as
			 * PROCESSING --------------------------------------------------
			 */

			markProcessing(submissionId);

			/*
			 * -------------------------------------------------- 2. Fetch submission
			 * --------------------------------------------------
			 */

//			Submission submission = submissionRepository.findById(submissionId)
//					.orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + submissionId));

			Submission submission = submissionRepository.findByIdWithProblem(submissionId)
					.orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + submissionId));

			Problem problem = submission.getProblem();

			/*
			 * -------------------------------------------------- 3. Execute student's SQL
			 * query --------------------------------------------------
			 */

			long startTime = System.currentTimeMillis();

			SqlExecutionResult studentResult = sqlExecutionService.execute(submission.getSubmittedQuery());

			long executionTime = System.currentTimeMillis() - startTime;

			/*
			 * -------------------------------------------------- 4. Student SQL failed
			 * --------------------------------------------------
			 */

			if (!studentResult.isSuccess()) {

				markSqlError(submissionId, executionTime, studentResult.getErrorMessage());

				return;
			}

			/*
			 * -------------------------------------------------- 5. Execute expected query
			 * --------------------------------------------------
			 *
			 * Expected query is created by Faculty/Admin.
			 */

			SqlExecutionResult expectedResult = sqlExecutionService.execute(problem.getExpectedQuery());

			/*
			 * -------------------------------------------------- 6. Expected query failed
			 * --------------------------------------------------
			 *
			 * This is a platform/problem configuration error, not a student's error.
			 */

			if (!expectedResult.isSuccess()) {

				log.error("Expected query failed. " + "problemId={}, submissionId={}", problem.getId(), submissionId);

				markSystemError(submissionId,
						"Unable to evaluate this problem because " + "the reference query failed.");

				return;
			}

			/*
			 * -------------------------------------------------- 7. Compare results
			 * --------------------------------------------------
			 */

			boolean correct = resultComparisonService.compare(studentResult, expectedResult);

			/*
			 * -------------------------------------------------- 8. Calculate score
			 * --------------------------------------------------
			 */

			int score = calculateScore(problem.getDifficulty(), submission.getAttemptNumber(), correct);

			/*
			 * -------------------------------------------------- 9. Update final result
			 * --------------------------------------------------
			 */

			if (correct) {

				markAccepted(submissionId, score, executionTime);

			} else {

				markWrongAnswer(submissionId, score, executionTime);
			}

			log.info("Submission processing completed. " + "submissionId={}, correct={}, score={}", submissionId,
					correct, score);

		} catch (ResourceNotFoundException ex) {

			log.error("Submission {} could not be processed.", submissionId, ex);

			markSystemError(submissionId, "Submission could not be processed.");

		} catch (Exception ex) {

			log.error("Unexpected error processing submission {}", submissionId, ex);

			markSystemError(submissionId, "Internal error while evaluating submission.");
		}
	}

	/**
	 * Mark submission as PROCESSING.
	 */
	@Transactional
	protected void markProcessing(Long submissionId) {

		Submission submission = submissionRepository.findById(submissionId)
				.orElseThrow(() -> new ResourceNotFoundException("Submission not found."));

		submission.setStatus(SubmissionStatus.PROCESSING);

		submissionRepository.save(submission);
	}

	/**
	 * Student SQL generated an error.
	 */
	@Transactional
	protected void markSqlError(Long submissionId, long executionTime, String errorMessage) {

		Submission submission = submissionRepository.findById(submissionId).orElse(null);

		if (submission == null) {
			return;
		}

		submission.setStatus(SubmissionStatus.SQL_ERROR);

		submission.setScore(0);

		submission.setExecutionTimeMs(executionTime);

		submission.setErrorMessage(errorMessage);

		submission.setCompletedAt(LocalDateTime.now());

		submissionRepository.save(submission);
	}

	/**
	 * Mark submission as ACCEPTED.
	 */
	@Transactional
	protected void markAccepted(Long submissionId, int score, long executionTime) {

		Submission submission = submissionRepository.findById(submissionId).orElse(null);

		if (submission == null) {
			return;
		}

		submission.setStatus(SubmissionStatus.ACCEPTED);

		submission.setScore(score);

		submission.setExecutionTimeMs(executionTime);

		submission.setErrorMessage(null);

		submission.setCompletedAt(LocalDateTime.now());

		submissionRepository.save(submission);
	}

	/**
	 * Mark submission as WRONG_ANSWER.
	 */
	@Transactional
	protected void markWrongAnswer(Long submissionId, int score, long executionTime) {

		Submission submission = submissionRepository.findById(submissionId).orElse(null);

		if (submission == null) {
			return;
		}

		submission.setStatus(SubmissionStatus.WRONG_ANSWER);

		submission.setScore(score);

		submission.setExecutionTimeMs(executionTime);

		submission.setErrorMessage(null);

		submission.setCompletedAt(LocalDateTime.now());

		submissionRepository.save(submission);
	}

	/**
	 * Mark submission as SYSTEM_ERROR.
	 */
	@Transactional
	protected void markSystemError(Long submissionId, String errorMessage) {

		Submission submission = submissionRepository.findById(submissionId).orElse(null);

		if (submission == null) {
			return;
		}

		submission.setStatus(SubmissionStatus.SYSTEM_ERROR);

		submission.setScore(0);

		submission.setErrorMessage(errorMessage);

		submission.setCompletedAt(LocalDateTime.now());

		submissionRepository.save(submission);
	}

	/**
	 * Calculate score.
	 *
	 * Base score:
	 *
	 * EASY = 10 MEDIUM = 20 HARD = 30
	 *
	 * Attempt penalty:
	 *
	 * EASY = 2 MEDIUM = 4 HARD = 6
	 *
	 * Example:
	 *
	 * EASY attempt 1 = 10 - 2 = 8 EASY attempt 2 = 10 - 4 = 6 EASY attempt 3 = 10 -
	 * 6 = 4
	 *
	 * Score cannot become negative.
	 */
	private int calculateScore(Difficulty difficulty, int attemptNumber, boolean correct) {

		if (!correct) {
			return 0;
		}

		int baseScore;

		int penaltyPerAttempt;

		switch (difficulty) {

		case EASY:

			baseScore = 10;
			penaltyPerAttempt = 2;

			break;

		case MEDIUM:

			baseScore = 20;
			penaltyPerAttempt = 4;

			break;

		case HARD:

			baseScore = 30;
			penaltyPerAttempt = 6;

			break;

		default:

			throw new IllegalArgumentException("Unknown difficulty: " + difficulty);
		}

		int score = baseScore - (penaltyPerAttempt * (attemptNumber - 1));

		return Math.max(score, 0);
	}
}
