package com.sql.gaming.backend.service;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sql.gaming.backend.entity.Problem;
import com.sql.gaming.backend.entity.ProblemTable;
import com.sql.gaming.backend.entity.SqlTable;
import com.sql.gaming.backend.pojo.User;
import com.sql.gaming.backend.enums.Role;
import com.sql.gaming.backend.pojo.ProblemCreateRequest;
import com.sql.gaming.backend.pojo.ProblemResponse;
import com.sql.gaming.backend.pojo.SqlTableResponse;
import com.sql.gaming.backend.repository.ProblemRepository;
import com.sql.gaming.backend.repository.SqlTableRepository;
import com.sql.gaming.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProblemService {

	private final ProblemRepository problemRepository;

	private final SqlTableRepository sqlTableRepository;

	private final UserRepository userRepository;

	// =========================================================
	// CREATE PROBLEM
	// =========================================================

	/**
	 * Create a new SQL problem.
	 *
	 * Only FACULTY and ADMIN can create problems.
	 */
	public ProblemResponse createProblem(ProblemCreateRequest request, String creatorUserName) {

		try {

			/*
			 * 1. Find creator
			 */
			User creator = getUser(creatorUserName);

			/*
			 * 2. Validate creator role
			 */
			validateFacultyOrAdmin(creator);

			/*
			 * 3. Validate table IDs
			 */
			List<Long> tableIds = request.getTableIds();

			validateTableIds(tableIds);

			/*
			 * 4. Check duplicate table IDs
			 */
			Set<Long> uniqueTableIds = new HashSet<>(tableIds);

			if (uniqueTableIds.size() != tableIds.size()) {

				throw new RuntimeException("Duplicate SQL table IDs are not allowed.");
			}

			/*
			 * 5. Find selected tables
			 */
			List<SqlTable> sqlTables = tableIds.stream().map(this::getActiveSqlTable).collect(Collectors.toList());

			/*
			 * 6. Generate unique slug
			 */
			String slug = generateUniqueSlug(request.getTitle());

			/*
			 * 7. Create Problem
			 */
			Problem problem = Problem.builder().title(request.getTitle()).slug(slug)
					.description(request.getDescription()).difficulty(request.getDifficulty())
					.expectedQuery(request.getExpectedQuery()).explanation(request.getExplanation()).createdBy(creator)
					.active(true).build();

			/*
			 * 8. Create ProblemTable mappings
			 */
			for (SqlTable sqlTable : sqlTables) {

				ProblemTable problemTable = ProblemTable.builder().problem(problem).sqlTable(sqlTable).build();

				/*
				 * Maintain both sides of relationship.
				 */
				problem.addTable(problemTable);
			}

			/*
			 * 9. Save problem.
			 *
			 * CascadeType.ALL will save ProblemTable.
			 */
			Problem savedProblem = problemRepository.save(problem);

			log.info("SQL problem created successfully. " + "problemId={}, creator={}, tables={}", savedProblem.getId(),
					creatorUserName, sqlTables.size());

			return convertToResponse(savedProblem);

		} catch (RuntimeException ex) {

			log.error("Business error while creating problem. creator={}", creatorUserName, ex);

			throw ex;

		} catch (Exception ex) {

			log.error("Unexpected error while creating SQL problem", ex);

			throw new RuntimeException("Unable to create SQL problem at this time.");
		}
	}

	// =========================================================
	// GET FACULTY PROBLEMS
	// =========================================================

	/**
	 * Get all problems created by the logged-in faculty.
	 */
	@Transactional(readOnly = true)
	public List<ProblemResponse> getFacultyProblems(String userName) {

		try {

			/*
			 * Verify user exists.
			 */
			User user = getUser(userName);

			/*
			 * Only FACULTY / ADMIN can use this service.
			 */
			validateFacultyOrAdmin(user);

			/*
			 * ADMIN can see all problems.
			 *
			 * FACULTY can see only their own problems.
			 */
			List<Problem> problems;

			if (user.getRole() == Role.ROLE_ADMIN) {

				problems = problemRepository.findAll();

			} else {

				problems = problemRepository.findByCreatedByUserName(userName);
			}

			return problems.stream().map(this::convertToResponse).collect(Collectors.toList());

		} catch (RuntimeException ex) {

			log.error("Business error while fetching problems for user: {}", userName, ex);

			throw ex;

		} catch (Exception ex) {

			log.error("Unexpected error while fetching problems for user: {}", userName, ex);

			throw new RuntimeException("Unable to fetch problems at this time.");
		}
	}

	// =========================================================
	// GET SINGLE PROBLEM
	// =========================================================

	/**
	 * Get one problem.
	 *
	 * Faculty can access only their own problems. Admin can access any problem.
	 */
	@Transactional(readOnly = true)
	public ProblemResponse getProblemById(Long problemId, String userName) {

		try {

			User user = getUser(userName);

			validateFacultyOrAdmin(user);

			Problem problem = getProblem(problemId);

			/*
			 * Faculty ownership check.
			 */
			validateProblemAccess(problem, user);

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
	// UPDATE PROBLEM
	// =========================================================

	/**
	 * Update an existing problem.
	 *
	 * Faculty can update only their own problems. Admin can update any problem.
	 */
	public ProblemResponse updateProblem(Long problemId, ProblemCreateRequest request, String userName) {

		try {

			/*
			 * 1. Find logged-in user.
			 */
			User user = getUser(userName);

			validateFacultyOrAdmin(user);

			/*
			 * 2. Find problem.
			 */
			Problem problem = getProblem(problemId);

			/*
			 * 3. Check ownership.
			 */
			validateProblemAccess(problem, user);

			/*
			 * 4. Validate table IDs.
			 */
			List<Long> tableIds = request.getTableIds();

			validateTableIds(tableIds);

			/*
			 * 5. Check duplicate table IDs.
			 */
			Set<Long> uniqueTableIds = new HashSet<>(tableIds);

			if (uniqueTableIds.size() != tableIds.size()) {

				throw new RuntimeException("Duplicate SQL table IDs are not allowed.");
			}

			/*
			 * 6. Get active SQL tables.
			 */
			List<SqlTable> sqlTables = tableIds.stream().map(this::getActiveSqlTable).collect(Collectors.toList());

			/*
			 * 7. Update basic information.
			 */
			problem.setTitle(request.getTitle());

			problem.setDescription(request.getDescription());

			problem.setDifficulty(request.getDifficulty());

			problem.setExpectedQuery(request.getExpectedQuery());

			problem.setExplanation(request.getExplanation());

			/*
			 * We are NOT changing the slug.
			 *
			 * This is better for URL stability.
			 */

			/*
			 * 8. Replace table mappings.
			 */
			problem.getTables().clear();

			for (SqlTable sqlTable : sqlTables) {

				ProblemTable problemTable = ProblemTable.builder().problem(problem).sqlTable(sqlTable).build();

				problem.addTable(problemTable);
			}

			/*
			 * 9. Save.
			 */
			Problem updatedProblem = problemRepository.save(problem);

			log.info("SQL problem updated successfully. " + "problemId={}, updatedBy={}", problemId, userName);

			return convertToResponse(updatedProblem);

		} catch (RuntimeException ex) {

			log.error("Business error while updating problem: {}", problemId, ex);

			throw ex;

		} catch (Exception ex) {

			log.error("Unexpected error while updating problem: {}", problemId, ex);

			throw new RuntimeException("Unable to update SQL problem.");
		}
	}

	// =========================================================
	// DEACTIVATE PROBLEM
	// =========================================================

	/**
	 * Deactivate a problem.
	 *
	 * Recommended approach: Don't immediately delete a problem that may already
	 * have student submissions.
	 */
	public ProblemResponse deactivateProblem(Long problemId, String userName) {

		try {

			User user = getUser(userName);

			validateFacultyOrAdmin(user);

			Problem problem = getProblem(problemId);

			validateProblemAccess(problem, user);

			if (!Boolean.TRUE.equals(problem.getActive())) {

//				throw new RuntimeException("Problem is already inactive.");
				problem.setActive(true);
			} else {
				
				problem.setActive(false);
			}

			 

			Problem savedProblem = problemRepository.save(problem);

			log.info("Problem deactivated. problemId={}, user={}", problemId, userName);

			return convertToResponse(savedProblem);

		} catch (RuntimeException ex) {

			log.error("Business error while deactivating problem: {}", problemId, ex);

			throw ex;

		} catch (Exception ex) {

			log.error("Unexpected error while deactivating problem: {}", problemId, ex);

			throw new RuntimeException("Unable to deactivate problem.");
		}
	}

	// =========================================================
	// DELETE PROBLEM
	// =========================================================

	/**
	 * Permanently delete a problem.
	 *
	 * Because Problem has:
	 *
	 * cascade = CascadeType.ALL orphanRemoval = true
	 *
	 * its ProblemTable mappings will also be removed.
	 */
	public void deleteProblem(Long problemId, String userName) {

		try {

			User user = getUser(userName);

			validateFacultyOrAdmin(user);

			Problem problem = getProblem(problemId);

			validateProblemAccess(problem, user);

			problemRepository.delete(problem);

			log.info("Problem deleted successfully. " + "problemId={}, deletedBy={}", problemId, userName);

		} catch (RuntimeException ex) {

			log.error("Business error while deleting problem: {}", problemId, ex);

			throw ex;

		} catch (Exception ex) {

			log.error("Unexpected error while deleting problem: {}", problemId, ex);

			throw new RuntimeException("Unable to delete problem.");
		}
	}

	// =========================================================
	// HELPER METHODS
	// =========================================================

	/**
	 * Find user by username.
	 */
	private User getUser(String userName) {

		return userRepository.findByUserName(userName)
				.orElseThrow(() -> new RuntimeException("User not found: " + userName));
	}

	/**
	 * Find problem by ID.
	 */
	private Problem getProblem(Long problemId) {

		return problemRepository.findById(problemId)
				.orElseThrow(() -> new RuntimeException("Problem not found: " + problemId));
	}

	/**
	 * Find active SQL table.
	 */
	private SqlTable getActiveSqlTable(Long tableId) {

		SqlTable sqlTable = sqlTableRepository.findById(tableId)
				.orElseThrow(() -> new RuntimeException("SQL table not found: " + tableId));

		if (!Boolean.TRUE.equals(sqlTable.getActive())) {

			throw new RuntimeException("SQL table is inactive: " + sqlTable.getTableName());
		}

		return sqlTable;
	}

	/**
	 * Validate Faculty/Admin.
	 */
	private void validateFacultyOrAdmin(User user) {

		if (user.getRole() != Role.ROLE_FACULTY && user.getRole() != Role.ROLE_ADMIN) {

			throw new RuntimeException("Only FACULTY or ADMIN can manage problems.");
		}
	}

	/**
	 * Validate whether the user can access the given problem.
	 */
	private void validateProblemAccess(Problem problem, User user) {

		/*
		 * ADMIN has access to every problem.
		 */
		if (user.getRole() == Role.ROLE_ADMIN) {
			return;
		}

		/*
		 * Faculty can access only their own problems.
		 */
		if (!problem.getCreatedBy().getUserName().equals(user.getUserName())) {

			throw new RuntimeException("You are not authorized to access this problem.");
		}
	}

	/**
	 * Validate table IDs.
	 */
	private void validateTableIds(List<Long> tableIds) {

		if (tableIds == null || tableIds.isEmpty()) {

			throw new RuntimeException("At least one SQL table is required.");
		}
	}

	/**
	 * Generate a unique URL-friendly slug.
	 */
	private String generateUniqueSlug(String title) {

		String slug = Normalizer.normalize(title, Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase(Locale.ENGLISH)
				.replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");

		String baseSlug = slug;

		int counter = 1;

		while (problemRepository.existsBySlug(slug)) {

			slug = baseSlug + "-" + counter;

			counter++;
		}

		return slug;
	}

	// =========================================================
	// RESPONSE CONVERSION
	// =========================================================

	/**
	 * Convert Problem entity into response DTO.
	 *
	 * IMPORTANT: expectedQuery is NEVER returned here.
	 */
	private ProblemResponse convertToResponse(Problem problem) {

		List<SqlTableResponse> tableResponses = problem.getTables().stream().map(ProblemTable::getSqlTable)
				.map(this::convertTableToResponse).collect(Collectors.toList());

		return ProblemResponse.builder().id(problem.getId()).title(problem.getTitle()).slug(problem.getSlug())
				.description(problem.getDescription()).difficulty(problem.getDifficulty())
				.explanation(problem.getExplanation()).tables(tableResponses)
				.createdBy(problem.getCreatedBy().getUserName()).active(problem.getActive())
				.createdAt(problem.getCreatedAt()).updatedAt(problem.getUpdatedAt()).build();
	}

	/**
	 * Convert SqlTable entity into response DTO.
	 */
	private SqlTableResponse convertTableToResponse(SqlTable sqlTable) {

		return SqlTableResponse.builder().id(sqlTable.getId()).tableName(sqlTable.getTableName())
				.description(sqlTable.getDescription()).active(sqlTable.getActive()).build();
	}
}