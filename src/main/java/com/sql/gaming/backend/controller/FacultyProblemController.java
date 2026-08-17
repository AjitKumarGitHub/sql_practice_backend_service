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

	@GetMapping("/allproblems")
	public ResponseEntity<ApiResponse<List<ProblemResponse>>> getMyProblems(Authentication authentication) {

		String userName = authentication.getName();

		List<ProblemResponse> problems = problemService.getFacultyProblems(userName);

		return ResponseEntity.ok(ApiResponse.<List<ProblemResponse>>builder().success(true)
				.message("Problems fetched successfully.").data(problems).build());
	}

	@GetMapping("/{problemId}")
	public ResponseEntity<ApiResponse<ProblemResponse>> getProblemById(

			@PathVariable Long problemId,

			Authentication authentication) {

		String userName = authentication.getName();

		ProblemResponse response = problemService.getProblemById(problemId, userName);

		return ResponseEntity.ok(ApiResponse.<ProblemResponse>builder().success(true)
				.message("Problem fetched successfully.").data(response).build());
	}

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

	@PatchMapping("/{problemId}/toggleactivate")
	public ResponseEntity<ApiResponse<ProblemResponse>> deactivateProblem(

			@PathVariable Long problemId,

			Authentication authentication) {

		String userName = authentication.getName();

		ProblemResponse response = problemService.deactivateProblem(problemId, userName);

		return ResponseEntity.ok(ApiResponse.<ProblemResponse>builder().success(true)
				.message("Problem deactivated successfully.").data(response).build());
	}

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