package com.sql.gaming.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sql.gaming.backend.enums.Difficulty;
import com.sql.gaming.backend.entity.Problem;
import com.sql.gaming.backend.pojo.User;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

     
    Optional<Problem> findBySlug(String slug);

     
    boolean existsBySlug(String slug);

     
    List<Problem> findByActiveTrue();

    
    List<Problem> findByDifficultyAndActiveTrue(
            Difficulty difficulty
    );

     
    List<Problem> findByCreatedBy(User createdBy);

     
    List<Problem> findByCreatedByAndActiveTrue(
            User createdBy
    );

    
    List<Problem> findByCreatedByUserName(
            String userName
    );
 
    List<Problem> findByCreatedByUserNameAndActiveTrue(
            String userName
    );

}
