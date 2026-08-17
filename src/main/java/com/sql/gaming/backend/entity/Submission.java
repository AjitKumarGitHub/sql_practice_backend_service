package com.sql.gaming.backend.entity;

import java.time.LocalDateTime;

import com.sql.gaming.backend.enums.SubmissionStatus;
import com.sql.gaming.backend.pojo.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "submissions", uniqueConstraints = {
		@UniqueConstraint(name = "uk_student_problem_attempt", columnNames = { "student_id", "problem_id",
				"attempt_number" }) }, indexes = { @Index(name = "idx_submission_student", columnList = "student_id"),
						@Index(name = "idx_submission_problem", columnList = "problem_id"),
						@Index(name = "idx_submission_status", columnList = "status") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Submission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "student_id", nullable = false)
	private User student;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "problem_id", nullable = false)
	private Problem problem;

	@Column(name = "submitted_query", nullable = false, columnDefinition = "TEXT")
	private String submittedQuery;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private SubmissionStatus status;

	@Column(name = "attempt_number", nullable = false)
	private Integer attemptNumber;

	private Integer score;

	private Long executionTimeMs;

	@Column(columnDefinition = "TEXT")
	private String errorMessage;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	private LocalDateTime completedAt;

	@PrePersist
	public void prePersist() {

		createdAt = LocalDateTime.now();

		if (status == null) {
			status = SubmissionStatus.QUEUED;
		}

		if (score == null) {
			score = 0;
		}
	}
}