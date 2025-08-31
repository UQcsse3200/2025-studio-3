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
    private WaveManager waveManager;
    private int wave;

    /**
     * Default constructor
     */
    public WaveFactory() {
        this.waveManager = new WaveManager();
        this.wave = waveManager.getCurrentWave();
    }

    /**
     * Constructor with game entity for UI integration
     * @param gameEntity the main game entity for triggering UI events
     */
    public WaveFactory(Entity gameEntity) {
        this.waveManager = new WaveManager();
        this.waveManager.setGameEntity(gameEntity);
        this.wave = waveManager.getCurrentWave();
    }

    /**
     * Changes the wave after a certain condition is met.
     */
    public void changeWave() {
        this.wave = waveManager.getCurrentWave();
    }

    /**
     * Advances to the next wave using the WaveManager
     */
    public void advanceToNextWave() {
        // Create dummy enemies array for now - this will be replaced with actual enemy creation
        Entity[] dummyEnemies = new Entity[1];
        waveManager.startNewWave(dummyEnemies);
        this.wave = waveManager.getCurrentWave();
    }

    /**
     * Gets the WaveManager instance
     * @return the WaveManager
     */
    public WaveManager getWaveManager() {
        return waveManager;
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
            case 1 -> configs.wave1;
            case 2 -> configs.wave2;
            case 3 -> configs.wave3;
            default -> null;
        };
    }
}
