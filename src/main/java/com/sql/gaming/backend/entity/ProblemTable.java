package com.sql.gaming.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "problem_tables", uniqueConstraints = {
		@UniqueConstraint(name = "uk_problem_sql_table", columnNames = { "problem_id", "sql_table_id" }) }, indexes = {
				@Index(name = "idx_problem_table_problem", columnList = "problem_id"),
				@Index(name = "idx_problem_table_sql_table", columnList = "sql_table_id") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemTable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * The SQL problem.
	 *
	 * Example: Problem #101
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "problem_id", nullable = false)
	private Problem problem;

	/**
	 * Existing SQL table used by the problem.
	 *
	 * Example: employees departments
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sql_table_id", nullable = false)
	private SqlTable sqlTable;

}
