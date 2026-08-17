package com.sql.gaming.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sql.gaming.backend.entity.SqlTable;

@Repository
public interface SqlTableRepository extends JpaRepository<SqlTable, Long> {

    /**
     * Find a table by its actual table name.
     *
     * Example:
     * employees
     * departments
     */
    Optional<SqlTable> findByTableName(String tableName);

    /**
     * Check whether a table already exists.
     */
    boolean existsByTableName(String tableName);

    /**
     * Get all active SQL tables.
     */
    List<SqlTable> findByActiveTrue();

}
