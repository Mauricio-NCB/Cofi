package com.website.main.service;

import com.website.main.model.Achievement;
import com.website.main.model.Achievement.AchievementType;
import com.website.main.model.User;
import com.website.main.model.UserAchievement;
import com.website.main.repository.AchievementRepository;
import com.website.main.repository.UserAchievementRepository;
import com.website.main.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;

    public AchievementService(AchievementRepository achievementRepository,
                              UserAchievementRepository userAchievementRepository,
                              UserRepository userRepository) {
        this.achievementRepository = achievementRepository;
        this.userAchievementRepository = userAchievementRepository;
        this.userRepository = userRepository;
    }

    // Desbloquear logro al usuario
    public void unlockAchievement(Integer userId, AchievementType type) {
        Achievement achievement = achievementRepository.findByType(type).orElse(null);
        if (achievement == null) return;

        // Verificar si ya está desbloqueado
        Optional<UserAchievement> existing = userAchievementRepository.findByUserIdAndAchievementId(userId, achievement.getId());
        if (existing.isPresent()) return; // Ya desbloqueado

        User user = userRepository.getReferenceById(userId);
        UserAchievement ua = new UserAchievement();
        ua.setUser(user);
        ua.setAchievement(achievement);
        ua.setUnlockedAt(LocalDateTime.now());
        userAchievementRepository.save(ua);
    }

    // Verificar si usuario tiene desbloqueado un logro
    public boolean isAchievementUnlocked(Integer userId, Integer achievementId) {
        Optional<UserAchievement> ua = userAchievementRepository.findByUserIdAndAchievementId(userId, achievementId);
        return ua.isPresent();
    }

    // Obtener IDs de reacciones desbloqueadas del usuario
    public List<Integer> getUnlockedReactionIds(Integer userId) {
        return userAchievementRepository.findByUserId(userId).stream()
                .filter(ua -> ua.getAchievement().getUnlockedReaction() != null)
                .map(ua -> ua.getAchievement().getUnlockedReaction().getId())
                .toList();
    }
}
