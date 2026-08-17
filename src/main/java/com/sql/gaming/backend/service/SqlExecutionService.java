package com.sql.gaming.backend.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import com.sql.gaming.backend.pojo.SqlExecutionResult;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SqlExecutionService {

	private final DataSource dataSource;

	/*
	 * Maximum SQL execution time.
	 *
	 * 5 seconds is a reasonable starting point.
	 */
	private static final int QUERY_TIMEOUT_SECONDS = 2;

	/**
	 * Execute a SQL query using a read-only database connection.
	 */
	public SqlExecutionResult execute(String sql) {

		long startTime = System.currentTimeMillis();

		try {

			// -------------------------------------------------
			// 1. Validate query
			// -------------------------------------------------

			validateQuery(sql);

			// -------------------------------------------------
			// 2. Get database connection
			// -------------------------------------------------

			try (Connection connection = dataSource.getConnection()) {

				// -------------------------------------------------
				// 3. Make connection read-only
				// -------------------------------------------------

				connection.setReadOnly(true);

				// -------------------------------------------------
				// 4. Start transaction
				// -------------------------------------------------

				connection.setAutoCommit(false);

				try {

					/*
					 * PostgreSQL transaction-level read-only protection.
					 *
					 * This is an additional safety layer.
					 */
					try (Statement transactionStatement = connection.createStatement()) {

						transactionStatement.execute("SET TRANSACTION READ ONLY");
					}

					// -------------------------------------------------
					// 5. Create SQL statement
					// -------------------------------------------------

					try (Statement statement = connection.createStatement()) {

						/*
						 * Prevent queries from running forever.
						 */
						statement.setQueryTimeout(QUERY_TIMEOUT_SECONDS);

						// -------------------------------------------------
						// 6. Execute query
						// -------------------------------------------------

						try (ResultSet resultSet = statement.executeQuery(sql)) {

							// -------------------------------------------------
							// 7. Extract result
							// -------------------------------------------------

							List<String> columns = extractColumns(resultSet);

							List<Map<String, Object>> rows = extractRows(resultSet);

							long executionTime = System.currentTimeMillis() - startTime;

							connection.rollback();

							return SqlExecutionResult.builder()

									.success(true)

									.columns(columns)

									.rows(rows)

									.executionTimeMs(executionTime)

									.build();
						}
					}

				} catch (Exception ex) {

					try {
						connection.rollback();
					} catch (SQLException rollbackException) {
						log.warn("Rollback failed", rollbackException);
					}

					throw ex;
				}
			}

		} catch (Exception ex) {

			long executionTime = System.currentTimeMillis() - startTime;

			log.warn("SQL execution failed: {}", ex.getMessage());

			return SqlExecutionResult.builder()

					.success(false)

					.executionTimeMs(executionTime)

					.errorMessage(sanitizeErrorMessage(ex.getMessage()))

					.build();
		}
	}

	/**
	 * Basic validation before sending SQL to PostgreSQL.
	 */
	private void validateQuery(String sql) {

		if (sql == null || sql.isBlank()) {

			throw new IllegalArgumentException("SQL query cannot be empty.");
		}

		String normalized = sql.trim().toLowerCase();

		/*
		 * Remove trailing semicolon.
		 */
		normalized = normalized.replaceAll(";+$", "").trim();

		/*
		 * For the SQL learning platform, students are currently expected to solve
		 * SELECT problems.
		 */
		if (!normalized.startsWith("select") && !normalized.startsWith("with")) {

			throw new IllegalArgumentException("Only SELECT queries are allowed.");
		}

		/*
		 * Reject multiple statements.
		 *
		 * This is an additional protection layer.
		 */
		String withoutTrailingSemicolon = sql.trim().replaceAll(";+$", "");

		if (withoutTrailingSemicolon.contains(";")) {

			throw new IllegalArgumentException("Multiple SQL statements are not allowed.");
		}
	}

	/**
	 * Extract column names.
	 */
	private List<String> extractColumns(ResultSet resultSet) throws SQLException {

		ResultSetMetaData metadata = resultSet.getMetaData();

		int columnCount = metadata.getColumnCount();

		List<String> columns = new ArrayList<>();

		for (int i = 1; i <= columnCount; i++) {

			columns.add(metadata.getColumnLabel(i));
		}

		return columns;
	}

	/**
	 * Convert ResultSet into a list of rows.
	 */
	private List<Map<String, Object>> extractRows(ResultSet resultSet) throws SQLException {

		ResultSetMetaData metadata = resultSet.getMetaData();

		int columnCount = metadata.getColumnCount();

		List<Map<String, Object>> rows = new ArrayList<>();

		while (resultSet.next()) {

			Map<String, Object> row = new LinkedHashMap<>();

			for (int i = 1; i <= columnCount; i++) {

				String columnName = metadata.getColumnLabel(i);

				Object value = resultSet.getObject(i);

				row.put(columnName, value);
			}

			rows.add(row);
		}

		return rows;
	}

	/**
	 * Don't expose internal database details directly to students.
	 */
	private String sanitizeErrorMessage(String message) {

		if (message == null) {
			return "SQL execution failed.";
		}

		/*
		 * For now return a controlled message.
		 *
		 * Later we can classify errors into: SQL_ERROR TIMEOUT etc.
		 */
		return "SQL execution failed.";
	}
}
