package com.csse3200.game.entities.configs;

import java.util.List;

/**
 * Configuration for waves across different levels.
 * Each level can have a different number of waves with different configurations.
 */
public class LevelWaveConfigs {
    public Level1Waves level1 = new Level1Waves();
    public Level2Waves level2 = new Level2Waves();
    public Level3Waves level3 = new Level3Waves();
    
    /**
     * Get wave configurations for a specific level
     * @param levelNumber the level number (1, 2, 3, etc.)
     * @return the wave configurations for that level
     */
    public BaseWaveConfig[] getWavesForLevel(int levelNumber) {
        return switch (levelNumber) {
            case 1 -> level1.waves;
            case 2 -> level2.waves;
            case 3 -> level3.waves;
            default -> level1.waves; // Default to level 1
        };
    }
    
    /**
     * Get the number of waves for a specific level
     * @param levelNumber the level number
     * @return number of waves for that level
     */
    public int getWaveCountForLevel(int levelNumber) {
        return getWavesForLevel(levelNumber).length;
    }
    
    /**
     * Level 1 wave configurations
     */
    public static class Level1Waves {
        public BaseWaveConfig[] waves = {
            new BaseWaveConfig(10, 10, 5),   // Wave 1: weight=10, exp=10, min=5
            new BaseWaveConfig(50, 20, 10),  // Wave 2: weight=50, exp=20, min=10  
            new BaseWaveConfig(75, 30, 15)   // Wave 3: weight=75, exp=30, min=15
        };
    }
    
    /**
     * Level 2 wave configurations
     */
    public static class Level2Waves {
        public BaseWaveConfig[] waves = {
            new BaseWaveConfig(20, 15, 8),   // Wave 1: weight=20, exp=15, min=8
            new BaseWaveConfig(60, 25, 12),  // Wave 2: weight=60, exp=25, min=12
            new BaseWaveConfig(100, 40, 20)  // Wave 3: weight=100, exp=40, min=20
        };
    }
    
    /**
     * Level 3 wave configurations
     */
    public static class Level3Waves {
        public BaseWaveConfig[] waves = {
            new BaseWaveConfig(30, 20, 10),  // Wave 1: weight=30, exp=20, min=10
            new BaseWaveConfig(80, 35, 15),  // Wave 2: weight=80, exp=35, min=15
            new BaseWaveConfig(120, 50, 25)  // Wave 3: weight=120, exp=50, min=25
        };
    }
}
