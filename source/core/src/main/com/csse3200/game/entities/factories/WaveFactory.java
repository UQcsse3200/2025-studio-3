package com.csse3200.game.entities.factories;

import com.csse3200.game.entities.WaveManager;
import com.csse3200.game.entities.configs.BaseWaveConfig;
import com.csse3200.game.entities.configs.WaveConfigs;
import com.csse3200.game.files.FileLoader;

/**
 * Factory to create a wave with predefined components.
 *
**/
public class WaveFactory {
    private static final WaveConfigs configs =
            FileLoader.readClass(WaveConfigs.class, "configs/level1.json");
    private final String wave;
    private WaveManager waveManager = new WaveManager();

    public WaveFactory(String wave) {
        this.wave = wave;
    }

    /**
     * Returns the current wave the players at.
     *
     * @return wave number as string
     */
    public int getCurrentWave() {
        return waveManager.getCurrentWave();
    }

    /**
     * Changes the wave after a certain condition is met.
     *
     */
    public void changeWave() {
        // Add logic to change the wave
    }

    /**
     * Returns the current wave weight.
     *
     * @return wave weight
     */
    public int getWaveWeight() {
        return getWave().waveWeight;
    }

    /**
     * Returns the exp gained for the current wave.
     *
     * @return exp gained
     */
    public int getExpGained() {
        return getWave().expGained;
    }

    /**
     * Returns the minimum number of zombies required to spawn for the wave.
     *
     * @return number of zombies
     */
    public int getMinZombiesSpawn() {
        return getWave().minZombiesSpawn;
    }

    /**
     * Helper function to allocate the wave string number to the wave number in the config file
     *
     * @return base wave config with the corresponding wave number
     */
    private BaseWaveConfig getWave() {
        return switch (this.wave) {
            case "wave1" -> configs.wave1;
            case "wave2" -> configs.wave2;
            case "wave3" -> configs.wave3;
            default -> null;
        };
    }
}
