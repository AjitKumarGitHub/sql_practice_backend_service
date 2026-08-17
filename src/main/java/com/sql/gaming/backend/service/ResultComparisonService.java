package com.sql.gaming.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sql.gaming.backend.pojo.SqlExecutionResult;

@Service
public class ResultComparisonService {

	/**
	 * Compare student's result with expected result.
	 *
	 * Currently comparison is order-sensitive.
	 */
	public boolean compare(SqlExecutionResult studentResult, SqlExecutionResult expectedResult) {

		if (studentResult == null || expectedResult == null) {

			return false;
		}

		if (!studentResult.isSuccess() || !expectedResult.isSuccess()) {

			return false;
		}

		/*
		 * Number of columns must match.
		 */
		if (studentResult.getColumns().size() != expectedResult.getColumns().size()) {

			return false;
		}

		/*
		 * Compare column names.
		 */
		if (!normalizeColumns(studentResult.getColumns()).equals(normalizeColumns(expectedResult.getColumns()))) {

			return false;
		}

		/*
		 * Compare number of rows.
		 */
		if (studentResult.getRows().size() != expectedResult.getRows().size()) {

			return false;
		}

		/*
		 * Compare row values.
		 */
		return compareRows(studentResult.getRows(), expectedResult.getRows());
	}

	private List<String> normalizeColumns(List<String> columns) {

		List<String> normalized = new ArrayList<>();

		for (String column : columns) {

			normalized.add(column == null ? null : column.toLowerCase());
		}

		return normalized;
	}

	private boolean compareRows(List<Map<String, Object>> studentRows, List<Map<String, Object>> expectedRows) {

		for (int i = 0; i < studentRows.size(); i++) {

			Map<String, Object> studentRow = studentRows.get(i);

			Map<String, Object> expectedRow = expectedRows.get(i);

			if (!rowsEqual(studentRow, expectedRow)) {

				return false;
			}
		}

		return true;
	}

	private boolean rowsEqual(Map<String, Object> studentRow, Map<String, Object> expectedRow) {

		if (studentRow.size() != expectedRow.size()) {

			return false;
		}

		for (String column : expectedRow.keySet()) {

			Object expectedValue = expectedRow.get(column);

			Object studentValue = studentRow.get(column);

			if (!valuesEqual(studentValue, expectedValue)) {

				return false;
			}
		}

		return true;
	}

	private boolean valuesEqual(Object studentValue, Object expectedValue) {

		if (studentValue == null && expectedValue == null) {

			return true;
		}

		if (studentValue == null || expectedValue == null) {

			return false;
		}

		/*
		 * Handles numeric differences such as:
		 *
		 * Integer 10 Long 10 BigDecimal 10
		 *
		 * without treating them as different.
		 */
		if (studentValue instanceof Number && expectedValue instanceof Number) {

			try {

				java.math.BigDecimal student = new java.math.BigDecimal(studentValue.toString());

				java.math.BigDecimal expected = new java.math.BigDecimal(expectedValue.toString());

				return student.compareTo(expected) == 0;

			} catch (NumberFormatException ignored) {

				return studentValue.toString().equals(expectedValue.toString());
			}
		}

		return studentValue.toString().equals(expectedValue.toString());
	}
}
