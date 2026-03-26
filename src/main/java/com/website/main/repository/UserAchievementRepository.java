package com.website.main.repository;

import com.website.main.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserAchievementRepository extends JpaRepository<UserAchievement, Integer> {
    Optional<UserAchievement> findByUserIdAndAchievementId(Integer userId, Integer achievementId);
    List<UserAchievement> findByUserId(Integer userId);
}
