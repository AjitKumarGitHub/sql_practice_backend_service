package com.sql.gaming.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sql.gaming.backend.entity.Problem;
import com.sql.gaming.backend.entity.ProblemTable;
import com.sql.gaming.backend.entity.SqlTable;
import com.sql.gaming.backend.pojo.User;
import com.sql.gaming.backend.enums.Role;
import com.sql.gaming.backend.pojo.ProblemResponse;
import com.sql.gaming.backend.pojo.SqlTableResponse;
import com.sql.gaming.backend.repository.ProblemRepository;
import com.sql.gaming.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StudentProblemService {

	private final ProblemRepository problemRepository;

	private final UserRepository userRepository;

	// =========================================================
	// GET ALL ACTIVE PROBLEMS
	// =========================================================

	/**
	 * Return all active problems.
	 *
	 * Students should never receive inactive problems.
	 */
	public List<ProblemResponse> getAllActiveProblems() {

		try {

			List<Problem> problems = problemRepository.findByActiveTrue();

			return problems.stream().map(this::convertToResponse).collect(Collectors.toList());

		} catch (Exception ex) {

			log.error("Error while fetching active SQL problems", ex);

			throw new RuntimeException("Unable to fetch SQL problems at this time.");
		}
	}

	// =========================================================
	// GET SINGLE PROBLEM
	// =========================================================

	/**
	 * Get a single active problem.
	 *
	 * The userName is used to verify that the requesting user is actually a
	 * STUDENT.
	 */
	public ProblemResponse getProblemById(Long problemId, String userName) {

		try {

			/*
			 * 1. Verify student.
			 */
			User student = userRepository.findByUserName(userName)
					.orElseThrow(() -> new RuntimeException("Student not found: " + userName));

			/*
			 * 2. Verify role.
			 */
			if (student.getRole() != Role.ROLE_STUDENT) {

				throw new RuntimeException("Only students can access student problems.");
			}

			/*
			 * 3. Find problem.
			 */
			Problem problem = problemRepository.findById(problemId)
					.orElseThrow(() -> new RuntimeException("Problem not found: " + problemId));

			/*
			 * 4. Do not expose inactive problems.
			 */
			if (!Boolean.TRUE.equals(problem.getActive())) {

				throw new RuntimeException("This problem is no longer active.");
			}

			/*
			 * 5. Convert to response.
			 *
			 * expectedQuery is NOT included.
			 */
			return convertToResponse(problem);

		} catch (RuntimeException ex) {

			log.error("Business error while fetching problem: {}", problemId, ex);

			throw ex;

		} catch (Exception ex) {

			log.error("Unexpected error while fetching problem: {}", problemId, ex);

			throw new RuntimeException("Unable to fetch problem at this time.");
		}
	}

	// =========================================================
	// RESPONSE CONVERSION
	// =========================================================

	/**
	 * Convert Problem entity to student response.
	 *
	 * IMPORTANT:
	 *
	 * expectedQuery is intentionally NOT returned.
	 */
	private ProblemResponse convertToResponse(Problem problem) {

		List<SqlTableResponse> tableResponses = problem.getTables().stream().map(ProblemTable::getSqlTable)
				.map(this::convertTableToResponse).collect(Collectors.toList());

		return ProblemResponse.builder()

				.id(problem.getId())

				.title(problem.getTitle())

				.slug(problem.getSlug())

				.description(problem.getDescription())

				.difficulty(problem.getDifficulty())

				.explanation(problem.getExplanation())

				.tables(tableResponses)

				.createdBy(problem.getCreatedBy().getUserName())

				.active(problem.getActive())

				.createdAt(problem.getCreatedAt())

				.updatedAt(problem.getUpdatedAt())

				.build();
	}

	/**
	 * Convert SQL table entity to response.
	 */
	private SqlTableResponse convertTableToResponse(SqlTable sqlTable) {

		return SqlTableResponse.builder()

				.id(sqlTable.getId())

				.tableName(sqlTable.getTableName())

				.description(sqlTable.getDescription())

				.active(sqlTable.getActive())

				.build();
	}
}
