package com.website.main.repository;

import com.website.main.model.Achievement;
import com.website.main.model.Achievement.AchievementType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AchievementRepository extends JpaRepository<Achievement, Integer> {
    Optional<Achievement> findByType(AchievementType type);
}
