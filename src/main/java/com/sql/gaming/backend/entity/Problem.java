package com.sql.gaming.backend.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.sql.gaming.backend.enums.Difficulty;
import com.sql.gaming.backend.pojo.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problems", indexes = { @Index(name = "idx_problem_slug", columnList = "slug"),
		@Index(name = "idx_problem_difficulty", columnList = "difficulty"),
		@Index(name = "idx_problem_created_by", columnList = "created_by"),
		@Index(name = "idx_problem_active", columnList = "active") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Problem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Problem title.
	 *
	 * Example: Employees With Highest Salary
	 */
	@Column(nullable = false, length = 200)
	private String title;

	/**
	 * Unique URL-friendly identifier.
	 *
	 * Example: employees-with-highest-salary
	 */
	@Column(nullable = false, unique = true, length = 250)
	private String slug;

	/**
	 * SQL problem statement.
	 */
	@Column(nullable = false, columnDefinition = "TEXT")
	private String description;

	/**
	 * EASY / MEDIUM / HARD
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Difficulty difficulty;

	/**
	 * Expected SQL query provided by Faculty/Admin.
	 *
	 * IMPORTANT: Never expose this directly to students.
	 */
	@Column(name = "expected_query", nullable = false, columnDefinition = "TEXT")
	private String expectedQuery;

	/**
	 * Optional explanation.
	 */
	@Column(columnDefinition = "TEXT")
	private String explanation;

	/**
	 * Faculty/Admin who created the problem.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;

	/**
	 * Existing SQL tables used by this problem.
	 *
	 * Example:
	 *
	 * Problem | +--- employees | +--- departments
	 */
	@OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<ProblemTable> tables = new ArrayList<>();

	/**
	 * Whether the problem is available to students.
	 */
	@Builder.Default
	@Column(nullable = false)
	private Boolean active = true;

	/**
	 * Created timestamp.
	 */
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	/**
	 * Updated timestamp.
	 */
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	public void prePersist() {

		LocalDateTime now = LocalDateTime.now();

		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	public void preUpdate() {

		updatedAt = LocalDateTime.now();
	}

	/**
	 * Add an SQL table to this problem.
	 */
	public void addTable(ProblemTable problemTable) {

		tables.add(problemTable);

		problemTable.setProblem(this);
	}

	/**
	 * Remove an SQL table from this problem.
	 */
	public void removeTable(ProblemTable problemTable) {

		tables.remove(problemTable);

		problemTable.setProblem(null);
	}
}