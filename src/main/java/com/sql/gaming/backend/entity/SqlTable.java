package com.sql.gaming.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sql_tables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SqlTable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Actual table name in the sql_data schema.
	 *
	 * Example: employees departments orders
	 */
	@Column(name = "table_name", nullable = false, unique = true, length = 100)
	private String tableName;

	/**
	 * Description of the table.
	 *
	 * Example: Contains employee information such as name, salary and department.
	 */
	@Column(columnDefinition = "TEXT")
	private String description;

	/**
	 * Whether this table is currently available for creating problems.
	 */
	@Builder.Default
	@Column(nullable = false)
	private Boolean active = true;
}
