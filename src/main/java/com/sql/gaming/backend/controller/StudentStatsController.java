package com.sql.gaming.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.sql.gaming.backend.pojo.ApiResponse;
import com.sql.gaming.backend.pojo.StudentStatsResponse;
import com.sql.gaming.backend.service.StudentStatsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/student/stats")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentStatsController {

	private final StudentStatsService studentStatsService;

	@GetMapping
	public ResponseEntity<ApiResponse<StudentStatsResponse>> getMyStats(Authentication authentication) {

		String userName = authentication.getName();

		StudentStatsResponse stats = studentStatsService.getStudentStats(userName);

		return ResponseEntity.ok(

				ApiResponse.<StudentStatsResponse>builder()

						.success(true)

						.message("Student statistics fetched successfully.")

						.data(stats)

						.build());
	}
}
