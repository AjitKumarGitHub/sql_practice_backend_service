package com.sql.gaming.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sql.gaming.backend.entity.ProblemTable;

@Repository
public interface ProblemTableRepository extends JpaRepository<ProblemTable, Long> {

	/**
	 * Find all table mappings for a problem.
	 */
	List<ProblemTable> findByProblem_Id(Long problemId);

	/**
	 * Find all problem mappings for a SQL table.
	 */
	List<ProblemTable> findBySqlTable_Id(Long sqlTableId);

	/**
	 * Check whether a particular table is already associated with a problem.
	 */
	boolean existsByProblem_IdAndSqlTable_Id(Long problemId, Long sqlTableId);

	/**
	 * Delete all mappings belonging to a problem.
	 */
	void deleteByProblem_Id(Long problemId);
}
