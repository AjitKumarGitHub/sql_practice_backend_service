
package com.sql.gaming.backend.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sql.gaming.backend.pojo.ApiResponse;
import com.sql.gaming.backend.pojo.LeaderboardResponse;
import com.sql.gaming.backend.service.LeaderboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

	private final LeaderboardService leaderboardService;

 
	@GetMapping("/global")
	public ResponseEntity<ApiResponse<Page<LeaderboardResponse>>> getGlobalLeaderboard(

			@RequestParam(defaultValue = "0") int page,

			@RequestParam(defaultValue = "20") int size) {

		validatePagination(page, size);

		Pageable pageable = PageRequest.of(page, size);

		Page<LeaderboardResponse> leaderboard = leaderboardService.getGlobalLeaderboard(pageable);

		return ResponseEntity.ok(ApiResponse.<Page<LeaderboardResponse>>builder().success(true)
				.message("Global leaderboard fetched successfully.").data(leaderboard).build());
	}

 
	@GetMapping("/batch/{batch}")
	public ResponseEntity<ApiResponse<Page<LeaderboardResponse>>> getBatchLeaderboard(

			@PathVariable String batch,

			@RequestParam(defaultValue = "0") int page,

			@RequestParam(defaultValue = "20") int size) {

		validatePagination(page, size);

		Pageable pageable = PageRequest.of(page, size);

		Page<LeaderboardResponse> leaderboard = leaderboardService.getBatchLeaderboard(batch, pageable);

		return ResponseEntity.ok(ApiResponse.<Page<LeaderboardResponse>>builder().success(true)
				.message("Batch leaderboard fetched successfully.").data(leaderboard).build());
	}

	// =========================================================
	// PAGINATION VALIDATION
	// =========================================================

	private void validatePagination(int page, int size) {

		if (page < 0) {

			throw new IllegalArgumentException("Page number cannot be negative.");
		}

		if (size <= 0) {

			throw new IllegalArgumentException("Page size must be greater than zero.");
		}

	 
		if (size > 100) {

			throw new IllegalArgumentException("Page size cannot exceed 100.");
		}
	}
}
