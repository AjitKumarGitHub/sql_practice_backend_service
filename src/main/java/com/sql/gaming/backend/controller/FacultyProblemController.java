package com.sql.gaming.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.sql.gaming.backend.pojo.ApiResponse;
import com.sql.gaming.backend.pojo.ProblemCreateRequest;
import com.sql.gaming.backend.pojo.ProblemResponse;
import com.sql.gaming.backend.service.ProblemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/faculty/problems")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
public class FacultyProblemController {

	private final ProblemService problemService;

	// =========================================================
	// CREATE PROBLEM
	// =========================================================

	/**
	 * Create a new SQL problem.
	 *
	 * FACULTY / ADMIN only.
	 *
	 * Username is obtained from the authenticated JWT.
	 */
	@PostMapping("/createproblem")
	public ResponseEntity<ApiResponse<ProblemResponse>> createProblem(

			@Valid @RequestBody ProblemCreateRequest request,

			Authentication authentication) {

		String userName = authentication.getName();
		
        log.info(userName);
		ProblemResponse response = problemService.createProblem(request, userName);

		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<ProblemResponse>builder().success(true)
				.message("SQL problem created successfully.").data(response).build());
	}

	// =========================================================
	// GET MY PROBLEMS
	// =========================================================

	/**
	 * Get problems created by the logged-in Faculty.
	 *
	 * ADMIN can see all problems.
	 *
	 * FACULTY can see only their own problems.
	 */
	@GetMapping("/allproblems")
	public ResponseEntity<ApiResponse<List<ProblemResponse>>> getMyProblems(Authentication authentication) {

		String userName = authentication.getName();

		List<ProblemResponse> problems = problemService.getFacultyProblems(userName);

		return ResponseEntity.ok(ApiResponse.<List<ProblemResponse>>builder().success(true)
				.message("Problems fetched successfully.").data(problems).build());
	}

	// =========================================================
	// GET PROBLEM BY ID
	// =========================================================

	/**
	 * Get a single problem.
	 *
	 * FACULTY: Can access only their own problem.
	 *
	 * ADMIN: Can access any problem.
	 */
	@GetMapping("/{problemId}")
	public ResponseEntity<ApiResponse<ProblemResponse>> getProblemById(

			@PathVariable Long problemId,

			Authentication authentication) {

		String userName = authentication.getName();

		ProblemResponse response = problemService.getProblemById(problemId, userName);

		return ResponseEntity.ok(ApiResponse.<ProblemResponse>builder().success(true)
				.message("Problem fetched successfully.").data(response).build());
	}

	// =========================================================
	// UPDATE PROBLEM
	// =========================================================

	/**
	 * Update an existing SQL problem.
	 *
	 * FACULTY: Can update only their own problem.
	 *
	 * ADMIN: Can update any problem.
	 */
	@PutMapping("/{problemId}")
	public ResponseEntity<ApiResponse<ProblemResponse>> updateProblem(

			@PathVariable Long problemId,

			@Valid @RequestBody ProblemCreateRequest request,

			Authentication authentication) {

		String userName = authentication.getName();

		ProblemResponse response = problemService.updateProblem(problemId, request, userName);

		return ResponseEntity.ok(ApiResponse.<ProblemResponse>builder().success(true)
				.message("Problem updated successfully.").data(response).build());
	}

	// =========================================================
	// DEACTIVATE PROBLEM
	// =========================================================

	/**
	 * Deactivate a problem.
	 *
	 * The problem remains in the database, but students should no longer see it.
	 */
	@PatchMapping("/{problemId}/toggleactivate")
	public ResponseEntity<ApiResponse<ProblemResponse>> deactivateProblem(

			@PathVariable Long problemId,

			Authentication authentication) {

		String userName = authentication.getName();

		ProblemResponse response = problemService.deactivateProblem(problemId, userName);

		return ResponseEntity.ok(ApiResponse.<ProblemResponse>builder().success(true)
				.message("Problem deactivated successfully.").data(response).build());
	}

	// =========================================================
	// DELETE PROBLEM
	// =========================================================

	/**
	 * Permanently delete a problem.
	 *
	 * NOTE: At the service level both Faculty and Admin currently have access.
	 *
	 * Later, we can restrict permanent deletion to ADMIN only.
	 */
	@DeleteMapping("/{problemId}")
	public ResponseEntity<ApiResponse<Long>> deleteProblem(

			@PathVariable Long problemId,

			Authentication authentication) {

		String userName = authentication.getName();

		problemService.deleteProblem(problemId, userName);

		return ResponseEntity.ok(ApiResponse.<Long>builder().success(true).message("Problem deleted successfully.")
				.data(problemId).build());
	}
}