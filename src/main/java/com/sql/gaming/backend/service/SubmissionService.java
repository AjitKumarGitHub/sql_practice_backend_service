package com.sql.gaming.backend.service;

import java.time.LocalDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sql.gaming.backend.entity.Problem;
import com.sql.gaming.backend.entity.Submission;
import com.sql.gaming.backend.pojo.User;
import com.sql.gaming.backend.enums.Difficulty;
import com.sql.gaming.backend.enums.Role;
import com.sql.gaming.backend.enums.SubmissionStatus;
import com.sql.gaming.backend.exception.BadRequestException;
import com.sql.gaming.backend.exception.ResourceNotFoundException;
import com.sql.gaming.backend.pojo.SubmissionRequest;
import com.sql.gaming.backend.pojo.SubmissionResponse;
import com.sql.gaming.backend.repository.ProblemRepository;
import com.sql.gaming.backend.repository.SubmissionRepository;
import com.sql.gaming.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubmissionService {

	private final SubmissionRepository submissionRepository;

	private final UserRepository userRepository;

	private final ProblemRepository problemRepository;

	private final SubmissionProcessorService submissionProcessorService;

	/**
	 * Create a new submission.
	 *
	 * This method only:
	 *
	 * 1. Validates student 2. Validates problem 3. Checks attempt limit 4. Creates
	 * PENDING submission 5. Sends submission for asynchronous processing
	 *
	 * SQL execution is NOT performed here.
	 */
	@Transactional
	public SubmissionResponse submit(Long problemId, String userName, SubmissionRequest request) {

		try {

			/*
			 * -------------------------------------------------- 1. Find student
			 * --------------------------------------------------
			 */

			User student = userRepository.findByUserName(userName)
					.orElseThrow(() -> new ResourceNotFoundException("Student not found: " + userName));

			/*
			 * -------------------------------------------------- 2. Validate student role
			 * --------------------------------------------------
			 */

			if (student.getRole() != Role.ROLE_STUDENT) {

				throw new BadRequestException("Only students can submit solutions.");
			}

			/*
			 * -------------------------------------------------- 3. Check whether account
			 * is enabled --------------------------------------------------
			 */

			if (!Boolean.TRUE.equals(student.getEnabled())) {

				throw new BadRequestException("Your account is disabled.");
			}

			/*
			 * -------------------------------------------------- 4. Find problem
			 * --------------------------------------------------
			 */

			Problem problem = problemRepository.findById(problemId)
					.orElseThrow(() -> new ResourceNotFoundException("Problem not found: " + problemId));

			/*
			 * -------------------------------------------------- 5. Check problem status
			 * --------------------------------------------------
			 */

			if (!Boolean.TRUE.equals(problem.getActive())) {

				throw new BadRequestException("This problem is no longer active.");
			}

			/*
			 * -------------------------------------------------- 6. Find previous maximum
			 * attempt --------------------------------------------------
			 */

			Integer maxAttempt = submissionRepository.findMaxAttemptNumber(userName, problemId);

			/*
			 * If no previous submission exists, maxAttempt can be null.
			 */

			if (maxAttempt == null) {

				maxAttempt = 0;
			}

			int attemptNumber = maxAttempt + 1;

			/*
			 * -------------------------------------------------- 7. Maximum 5 attempts
			 * --------------------------------------------------
			 */

			if (attemptNumber > 5) {

				throw new BadRequestException("Maximum 5 attempts allowed for this problem.");
			}

			/*
			 * -------------------------------------------------- 8. Validate query
			 * --------------------------------------------------
			 */

			if (request == null || request.getQuery() == null || request.getQuery().trim().isEmpty()) {

				throw new BadRequestException("SQL query cannot be empty.");
			}

			/*
			 * -------------------------------------------------- 9. Create submission
			 * --------------------------------------------------
			 */

			Submission submission = Submission.builder()

					.student(student)

					.problem(problem)

					.submittedQuery(request.getQuery().trim())

					.attemptNumber(attemptNumber)

					.status(SubmissionStatus.QUEUED)

					.score(0)

					.createdAt(LocalDateTime.now())

					.build();

			/*
			 * -------------------------------------------------- 10. Save submission
			 * --------------------------------------------------
			 */

			Submission saved = submissionRepository.save(submission);

			/*
			 * -------------------------------------------------- 11. Log
			 * --------------------------------------------------
			 */

			log.info("Submission created. id={}, student={}, problem={}, attempt={}", saved.getId(), userName,
					problemId, attemptNumber);

			/*
			 * -------------------------------------------------- 12. Send for asynchronous
			 * processing --------------------------------------------------
			 *
			 * IMPORTANT:
			 *
			 * SubmissionProcessorService is a different Spring bean, so @Async will
			 * actually work.
			 */

			submissionProcessorService.processSubmissionAsync(saved.getId());

			/*
			 * -------------------------------------------------- 13. Return immediately
			 * --------------------------------------------------
			 */

			return convertToResponse(saved);

		} catch (ResourceNotFoundException | BadRequestException ex) {

			/*
			 * Preserve business exceptions.
			 */

			throw ex;

		} catch (DataIntegrityViolationException ex) {

			/*
			 * Handles database-level concurrency violations.
			 */

			log.warn("Concurrent submission detected. student={}, problem={}", userName, problemId);

			throw new BadRequestException("Another submission was created simultaneously. " + "Please try again.");

		} catch (Exception ex) {

			/*
			 * Unexpected error.
			 */

			log.error("Unexpected error creating submission. " + "student={}, problem={}", userName, problemId, ex);

			throw new RuntimeException("Unable to submit solution at this time.");
		}
	}

	/**
	 * Get a student's submission.
	 */
	@Transactional(readOnly = true)
	public SubmissionResponse getSubmission(Long submissionId, String userName) {

		try {

			Submission submission = submissionRepository.findById(submissionId)
					.orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + submissionId));

			/*
			 * Students can only see their own submissions.
			 */

			if (!submission.getStudent().getUserName().equals(userName)) {

				throw new BadRequestException("You are not allowed to view this submission.");
			}

			return convertToResponse(submission);

		} catch (ResourceNotFoundException | BadRequestException ex) {

			throw ex;

		} catch (Exception ex) {

			log.error("Error fetching submission {} for student {}", submissionId, userName, ex);

			throw new RuntimeException("Unable to fetch submission.");
		}
	}

	/**
	 * Convert Submission entity to response DTO.
	 *
	 * Expected query is NEVER returned.
	 */
//	private SubmissionResponse convertToResponse(Submission submission) {
//
//		return SubmissionResponse.builder()
//
//				.submissionId(submission.getId())
//
//				.problemId(submission.getProblem().getId())
//
//				.problemTitle(submission.getProblem().getTitle())
//
//				.submittedQuery(submission.getSubmittedQuery())
//
//				.attemptNumber(submission.getAttemptNumber())
//
//				.status(submission.getStatus())
//
//				.score(submission.getScore())
//				
//				.difficulty(submission.getProblem().getDifficulty())
//
//				.executionTimeMs(submission.getExecutionTimeMs())
//
//				.message(submission.getErrorMessage())
//
//				.createdAt(submission.getCreatedAt())
//
//				.completedAt(submission.getCompletedAt())
//
//				.build();
//	}

	private SubmissionResponse convertToResponse(Submission submission) {

		return SubmissionResponse.builder()

				.submissionId(submission.getId())

				.problemId(submission.getProblem().getId())

				.problemTitle(submission.getProblem().getTitle())

				.difficulty(submission.getProblem().getDifficulty())

				.status(submission.getStatus())

				.score(submission.getScore())

				.attemptNumber(submission.getAttemptNumber())

				.executionTimeMs(submission.getExecutionTimeMs())

				.message(submission.getErrorMessage())

				.createdAt(submission.getCreatedAt())

				.completedAt(submission.getCompletedAt())

				.build();
	}
}