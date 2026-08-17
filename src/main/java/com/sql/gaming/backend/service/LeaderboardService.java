package com.sql.gaming.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sql.gaming.backend.pojo.LeaderboardResponse;
import com.sql.gaming.backend.repository.LeaderboardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LeaderboardService {

	private final LeaderboardRepository leaderboardRepository;

	// =========================================================
	// GLOBAL LEADERBOARD
	// =========================================================

	public Page<LeaderboardResponse> getGlobalLeaderboard(Pageable pageable) {

		Page<Object[]> results = leaderboardRepository.findGlobalLeaderboard(pageable);

		return convertToLeaderboard(results);
	}

	// =========================================================
	// BATCH LEADERBOARD
	// =========================================================

	public Page<LeaderboardResponse> getBatchLeaderboard(String batch, Pageable pageable) {

		Page<Object[]> results = leaderboardRepository.findBatchLeaderboard(batch, pageable);

		return convertToLeaderboard(results);
	}

	private Page<LeaderboardResponse> convertToLeaderboard(Page<Object[]> results) {

		List<LeaderboardResponse> leaderboard = new ArrayList<>();

		 

		int startingRank = results.getNumber() * results.getSize() + 1;
 
		List<Object[]> rows = results.getContent();

		for (int i = 0; i < rows.size(); i++) {

			Object[] row = rows.get(i);

			 

			String userName = (String) row[0];

			String name = (String) row[1];

			String batch = (String) row[2];

			 

			Integer totalScore = row[3] == null ? 0 : ((Number) row[3]).intValue();

			 

			Integer problemsSolved = row[4] == null ? 0 : ((Number) row[4]).intValue();

			 
			Integer easySolved = row[5] == null ? 0 : ((Number) row[5]).intValue();

			 

			Integer mediumSolved = row[6] == null ? 0 : ((Number) row[6]).intValue();

			 

			Integer hardSolved = row[7] == null ? 0 : ((Number) row[7]).intValue();

		 

			int rank = startingRank + i;

			 

			LeaderboardResponse response = LeaderboardResponse.builder()

					.rank(rank)

					.userName(userName)

					.name(name)

					.batch(batch)

					.totalScore(totalScore)

					.problemsSolved(problemsSolved)

					.easySolved(easySolved)

					.mediumSolved(mediumSolved)

					.hardSolved(hardSolved)

					.build();

			leaderboard.add(response);
		}
 
		return new PageImpl<>(leaderboard, results.getPageable(), results.getTotalElements());
	}
}