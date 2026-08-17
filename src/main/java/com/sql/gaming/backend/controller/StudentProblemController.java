package com.sql.gaming.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.sql.gaming.backend.pojo.ApiResponse;
import com.sql.gaming.backend.pojo.ProblemResponse;
import com.sql.gaming.backend.service.StudentProblemService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student/problems")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasRole('STUDENT')")
public class StudentProblemController {

	private final StudentProblemService studentProblemService;

	// =========================================================
	// GET ALL ACTIVE PROBLEMS
	// =========================================================

	/**
	 * Get all active SQL problems.
	 *
	 * Only STUDENT can access this endpoint.
	 */
	@GetMapping("/allactive")
	public ResponseEntity<ApiResponse<List<ProblemResponse>>> getAllProblems() {

		List<ProblemResponse> problems = studentProblemService.getAllActiveProblems();

		return ResponseEntity.ok(ApiResponse.<List<ProblemResponse>>builder().success(true)
				.message("Problems fetched successfully.").data(problems).build());
	}

	// =========================================================
	// GET PROBLEM BY ID
	// =========================================================

	/**
	 * Get a single active SQL problem.
	 *
	 * expectedQuery is NOT returned.
	 */
	@GetMapping("/{problemId}")
	public ResponseEntity<ApiResponse<ProblemResponse>> getProblemById(@PathVariable Long problemId,
			Authentication authentication) {

		String userName = authentication.getName();

		ProblemResponse response = studentProblemService.getProblemById(problemId, userName);

		return ResponseEntity.ok(ApiResponse.<ProblemResponse>builder().success(true)
				.message("Problem fetched successfully.").data(response).build());
	}
}
