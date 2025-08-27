package com.csse3200.game.Achievements;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class AchievementManager {
    private final Map<String, Achievement> achievements = new HashMap<>();

    public void addAchievement(Achievement a) {
        achievements.put(a.getName(), a);
    }

    public void unlock(String name) {
        Achievement a = achievements.get(name);
        if (a != null) {
            a.unlock();
        }
    }

    public boolean isUnlocked(String name) {
        Achievement a = achievements.get(name);
        return a != null && a.isUnlocked();
    }

    public Collection<Achievement> getAllAchievements() {
        return achievements.values();
    }
}
