package com.sql.gaming.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sql.gaming.backend.pojo.User;
import com.sql.gaming.backend.enums.Role;
import com.sql.gaming.backend.exception.BadRequestException;
import com.sql.gaming.backend.exception.ResourceNotFoundException;
import com.sql.gaming.backend.pojo.RoleUpdateRequest;
import com.sql.gaming.backend.pojo.StatusUpdateRequest;
import com.sql.gaming.backend.pojo.UserResponse;
import com.sql.gaming.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminService {

	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	public List<UserResponse> getAllUsers() {

		try {

			List<User> users = userRepository.findAll();

			return users.stream().map(this::convertToResponse).collect(Collectors.toList());

		} catch (Exception ex) {

			log.error("Error while fetching all users", ex);

			throw new RuntimeException("Unable to fetch users at this time.");
		}
	}

	@Transactional(readOnly = true)
	public UserResponse getUser(String userName) {

		try {

			User user = getUserEntity(userName);

			return convertToResponse(user);

		} catch (ResourceNotFoundException ex) {

			// Don't convert custom exception
			throw ex;

		} catch (Exception ex) {

			log.error("Error while fetching user: {}", userName, ex);

			throw new RuntimeException("Unable to fetch user at this time.");
		}
	}

	public UserResponse updateRole(String userName, RoleUpdateRequest request) {

		try {

			User user = getUserEntity(userName);

			Role oldRole = user.getRole();
			Role newRole = request.getRole();

			if (oldRole == newRole) {

				throw new BadRequestException("User already has role " + newRole);
			}

			if (oldRole == Role.ROLE_ADMIN && newRole != Role.ROLE_ADMIN) {

				long adminCount = userRepository.countByRole(Role.ROLE_ADMIN);

				if (adminCount <= 1) {

					throw new BadRequestException("Cannot remove the last ADMIN.");
				}
			}

			user.setRole(newRole);

			User savedUser = userRepository.save(user);

			log.info("User role updated: {} | {} -> {}", userName, oldRole, newRole);

			return convertToResponse(savedUser);

		} catch (ResourceNotFoundException | BadRequestException ex) {

			// Preserve our custom exceptions
			throw ex;

		} catch (Exception ex) {

			log.error("Error while updating role for user: {}", userName, ex);

			throw new RuntimeException("Unable to update user role.");
		}
	}

	public UserResponse updateStatus(String userName, StatusUpdateRequest request) {

		try {

			User user = getUserEntity(userName);

			Boolean newStatus = request.getEnabled();

			if (user.getEnabled().equals(newStatus)) {

				throw new BadRequestException("User is already " + (newStatus ? "enabled." : "disabled."));
			}

			if (user.getRole() == Role.ROLE_ADMIN && !newStatus) {

				long adminCount = userRepository.countByRole(Role.ROLE_ADMIN);

				if (adminCount <= 1) {

					throw new BadRequestException("Cannot disable the last ADMIN.");
				}
			}

			user.setEnabled(newStatus);

			User savedUser = userRepository.save(user);

			log.info("User status updated: {} -> {}", userName, newStatus);

			return convertToResponse(savedUser);

		} catch (ResourceNotFoundException | BadRequestException ex) {

			throw ex;

		} catch (Exception ex) {

			log.error("Error while updating status for user: {}", userName, ex);

			throw new RuntimeException("Unable to update user status.");
		}
	}

	public void deleteUser(String userName) {

		try {

			User user = getUserEntity(userName);

			if (user.getRole() == Role.ROLE_ADMIN) {

				long adminCount = userRepository.countByRole(Role.ROLE_ADMIN);

				if (adminCount <= 1) {

					throw new BadRequestException("Cannot delete the last ADMIN.");
				}
			}

			userRepository.delete(user);

			log.info("User deleted successfully: {}", userName);

		} catch (ResourceNotFoundException | BadRequestException ex) {

			throw ex;

		} catch (Exception ex) {

			log.error("Error while deleting user: {}", userName, ex);

			throw new RuntimeException("Unable to delete user.");
		}
	}

	private User getUserEntity(String userName) {

		return userRepository.findByUserName(userName)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + userName));
	}

	private UserResponse convertToResponse(User user) {

		return UserResponse.builder()

				.userName(user.getUserName())

				.name(user.getName())

				.email(user.getEmail())

				.course(user.getCourse())

				.batch(user.getBatch())

				.role(user.getRole())

				.enabled(user.getEnabled())

				.createdAt(user.getCreatedAt())

				.updatedAt(user.getUpdatedAt())

				.build();
	}
}