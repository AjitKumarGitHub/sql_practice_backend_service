package com.sql.gaming.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.sql.gaming.backend.enums.Role;
import com.sql.gaming.backend.pojo.User;

@Component
public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findByEmail(String email);

	Optional<User> findByUserName(String userName);

	boolean existsByEmail(String email);

	boolean existsByUserName(String userName);

	long countByRole(Role role);
}
