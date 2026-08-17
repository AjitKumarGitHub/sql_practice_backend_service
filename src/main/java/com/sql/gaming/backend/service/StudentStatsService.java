package com.sql.gaming.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sql.gaming.backend.exception.ResourceNotFoundException;
import com.sql.gaming.backend.pojo.StudentStatsResponse;
import com.sql.gaming.backend.pojo.User;
import com.sql.gaming.backend.repository.LeaderboardRepository;
import com.sql.gaming.backend.repository.StudentStatsRepository;
import com.sql.gaming.backend.repository.SubmissionRepository;
import com.sql.gaming.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentStatsService {

	private final LeaderboardRepository leaderboardRepository;

	private final UserRepository userRepository;

	private final SubmissionRepository submissionRepository;

	public StudentStatsResponse getStudentStats(String userName) {

		User student = userRepository.findByUserName(userName)
				.orElseThrow(() -> new ResourceNotFoundException("Student not found."));

		Integer totalScore = submissionRepository.getTotalScore(userName);

		Long problemsSolved = submissionRepository.getProblemsSolved(userName);

		Long easySolved = submissionRepository.getEasySolved(userName);

		Long mediumSolved = submissionRepository.getMediumSolved(userName);

		Long hardSolved = submissionRepository.getHardSolved(userName);

		Long totalSubmissions = submissionRepository.getTotalSubmissions(userName);

		Long acceptedSubmissions = submissionRepository.getAcceptedSubmissions(userName);

		Integer globalRank = leaderboardRepository.findGlobalRank(userName);

		Integer batchRank = leaderboardRepository.findBatchRank(userName, student.getBatch());

		return StudentStatsResponse.builder()

				.userName(student.getUserName())

				.name(student.getName())

				.batch(student.getBatch())

				.totalScore(totalScore != null ? totalScore : 0)

				.problemsSolved(problemsSolved != null ? problemsSolved.intValue() : 0)

				.easySolved(easySolved != null ? easySolved.intValue() : 0)

				.mediumSolved(mediumSolved != null ? mediumSolved.intValue() : 0)

				.hardSolved(hardSolved != null ? hardSolved.intValue() : 0)

				.totalSubmissions(totalSubmissions != null ? totalSubmissions.intValue() : 0)

				.acceptedSubmissions(acceptedSubmissions != null ? acceptedSubmissions.intValue() : 0)
				
				.globalRank(globalRank)
				
				.batchRank(batchRank)

				.build();
	}
}
