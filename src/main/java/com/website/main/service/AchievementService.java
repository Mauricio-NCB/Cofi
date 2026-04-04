package com.website.main.service;

import com.website.main.model.Achievement;
import com.website.main.model.Achievement.AchievementType;
import com.website.main.model.User;
import com.website.main.repository.AchievementRepository;
import com.website.main.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final UserRepository userRepository;

    public AchievementService(AchievementRepository achievementRepository,
                              UserRepository userRepository) {
        this.achievementRepository = achievementRepository;
        this.userRepository = userRepository;
    }

    // Desbloquear logro al usuario
    public void unlockAchievement(Integer userId, AchievementType type) {
        Achievement achievement = achievementRepository.findByType(type).orElse(null);
        if (achievement == null) return;

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return;

        // Verificar si ya está desbloqueado
        if (user.getAchievements().contains(achievement)) return;

        // Agregar achievement a la lista del usuario
        user.getAchievements().add(achievement);
        userRepository.save(user);
    }

    // Verificar si usuario tiene desbloqueado un logro
    public boolean isAchievementUnlocked(Integer userId, Integer achievementId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return false;

        return user.getAchievements().stream()
                .anyMatch(a -> a.getId().equals(achievementId));
    }

    // Obtener IDs de reacciones desbloqueadas del usuario
    public List<Integer> getUnlockedReactionIds(Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return List.of();

        return user.getAchievements().stream()
                .filter(a -> a.getUnlockedReaction() != null)
                .map(a -> a.getUnlockedReaction().getId())
                .toList();
    }
}
