package com.sql.gaming.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.sql.gaming.backend.pojo.ApiResponse;
import com.sql.gaming.backend.pojo.SubmissionRequest;
import com.sql.gaming.backend.pojo.SubmissionResponse;
import com.sql.gaming.backend.service.SubmissionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/v1/student/problems")
@RequiredArgsConstructor
@Validated
@PreAuthorize("hasAnyRole('STUDENT', 'FACULTY')")
public class StudentSubmissionController {

	private final SubmissionService submissionService;

	/**
	 * Submit SQL solution.
	 */
	@PostMapping("/{problemId}/submit")
	public ResponseEntity<ApiResponse<SubmissionResponse>> submit(

			@PathVariable Long problemId,

			@Valid @RequestBody SubmissionRequest request,

			Authentication authentication) {

		String userName = authentication.getName();

		SubmissionResponse response = submissionService.submit(problemId, userName, request);

		return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.<SubmissionResponse>builder()

				.success(true)

				.message("SQL submission received and queued for evaluation.")

				.data(response)

				.build());
	}

	/**
	 * Get submission result.
	 */
	@GetMapping("/submissions/{submissionId}")
	public ResponseEntity<ApiResponse<SubmissionResponse>> getSubmission(

			@PathVariable Long submissionId,

			Authentication authentication) {

		String userName = authentication.getName();

		SubmissionResponse response = submissionService.getSubmission(submissionId, userName);

		return ResponseEntity.ok(

				ApiResponse.<SubmissionResponse>builder()

						.success(true)

						.message("Submission fetched successfully.")

						.data(response)

						.build());
	}
}
