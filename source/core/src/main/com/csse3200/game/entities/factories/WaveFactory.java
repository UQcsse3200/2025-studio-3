package com.csse3200.game.entities.factories;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.WaveManager;
import com.csse3200.game.entities.configs.BaseWaveConfig;
import com.csse3200.game.entities.configs.WaveConfigs;
import com.csse3200.game.files.FileLoader;

/**
 * Factory to create a wave with predefined components.
 */
public class WaveFactory {
    private static final WaveConfigs configs =
            FileLoader.readClass(WaveConfigs.class, "configs/level1.json");

    /**
     * Default constructor
     */
    public WaveFactory() {
    }

    /**
     * Constructor with game entity for UI integration
     * @param gameEntity the main game entity for triggering UI events
     */
    public WaveFactory(Entity gameEntity) {
        WaveManager.setGameEntity(gameEntity);
    }

    public int getWaveWeight() {
        return getWave().waveWeight;
    }

    public int getMinZombiesSpawn() {
        return getWave().minZombiesSpawn;
    }

    public int getExpGained() {
        return getWave().expGained;
    }

    /**
     * Helper function to allocate the wave number to the correct index in the config file
     *
     * @return base wave config with the corresponding wave number
     */
    private BaseWaveConfig getWave() {
        return switch (WaveManager.getCurrentWave()) {
            case 1 -> configs.wave1;
            case 2 -> configs.wave2;
            case 3 -> configs.wave3;
            default -> configs.wave1;
        };
    }
}