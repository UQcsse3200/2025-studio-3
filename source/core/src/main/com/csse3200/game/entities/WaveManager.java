package com.csse3200.game.entities;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Manages wave progression and enemy spawning in the game.
 * Integrates with the UI display system through events.
 */
public class WaveManager {

    private int currentWave = 0;
    private int maxLanes;
    private Entity[] enemies = {};
    private int currentEnemy;
    private int[] laneWeights = {1,1,1,1,1};
    
    // Reference to the game entity for triggering UI events
    private Entity gameEntity;

    public WaveManager() {
        // Default constructor
    }

    /**
     * Constructor with game entity reference for UI integration
     * @param gameEntity the main game entity that can trigger UI events
     */
    public WaveManager(Entity gameEntity) {
        this.gameEntity = gameEntity;
    }

    /**
     * Sets the game entity reference for UI integration
     * @param gameEntity the main game entity
     */
    public void setGameEntity(Entity gameEntity) {
        this.gameEntity = gameEntity;
    }

    /**
     * Starts a new wave and triggers UI update events
     * @param enemies array of enemies for the new wave
     */
    public void startNewWave(Entity[] enemies) {
        currentWave++;
        if (enemies != null && enemies.length > 0) {
            currentEnemy = enemies[0].getId();
        }
        maxLanes = Math.min(currentWave + 1, 5);
        
        // Trigger UI update event
        if (gameEntity != null && gameEntity.getEvents() != null) {
            gameEntity.getEvents().trigger("newWaveStarted", currentWave);
            gameEntity.getEvents().trigger("waveChanged", currentWave);
        }
        
        laneManager();
    }

    /**
     * Gets the current wave number
     * @return current wave number
     */
    public int getCurrentWave() {
        return currentWave;
    }

    /**
     * Manually sets the current wave (useful for testing or level loading)
     * @param waveNumber the wave number to set
     */
    public void setCurrentWave(int waveNumber) {
        this.currentWave = waveNumber;
        
        // Trigger UI update event
        if (gameEntity != null) {
            gameEntity.getEvents().trigger("waveChanged", currentWave);
        }
    }

    /**
     * Manages lane spawning for the current wave
     */
    public void laneManager() {
        int i = ThreadLocalRandom.current().nextInt(0, maxLanes);
        spawnEnemy(i);
    }

    /**
     * Spawns an enemy in the specified lane
     * @param laneNumber the lane number to spawn in
     */
    public void spawnEnemy(int laneNumber) {
        System.out.print("Spawned enemy in lane " + laneNumber);
    }

    /**
     * Gets the maximum number of lanes for the current wave
     * @return maximum lanes
     */
    public int getMaxLanes() {
        return maxLanes;
    }

    /**
     * Gets the current enemy ID
     * @return current enemy ID
     */
    public int getCurrentEnemy() {
        return currentEnemy;
    }

    /**
     * Resets the wave manager to initial state
     */
    public void reset() {
        currentWave = 0;
        maxLanes = 0;
        currentEnemy = 0;
        
        // Trigger UI update event
        if (gameEntity != null) {
            gameEntity.getEvents().trigger("waveChanged", currentWave);
        }
    }
}
