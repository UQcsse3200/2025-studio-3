package com.csse3200.game.entities;

import java.util.*;

import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.entities.factories.NPCFactory;
import com.csse3200.game.services.GameTime;

public class WaveManager {

    private int currentWave;
    private List<Integer> laneOrder = new ArrayList<>(List.of(0, 1, 2, 3, 4));
    private Entity[] enemies = {new Entity()};
    private int currentEnemy;

    private final GameTime gameTime;
    private float timeSinceLastSpawn;

    private LevelGameArea levelGameArea;

    private List<Integer> waveLaneSequence;
    private int waveLanePointer;

    public WaveManager() {
        this.gameTime = new GameTime();
        this.currentWave = 0;
        this.timeSinceLastSpawn = 0f;
        this.waveLaneSequence = new ArrayList<>();
        this.waveLanePointer = 0;

        Collections.shuffle(laneOrder);
    }

    public void initialiseNewWave() {
        currentWave++;
        currentEnemy = enemies[0].getId();
        int maxLanes = Math.min(currentWave + 1, 5);

        waveLaneSequence = new ArrayList<>(laneOrder.subList(0, maxLanes));
        Collections.shuffle(waveLaneSequence);
        waveLanePointer = 0;
    }

    public void setGameArea(LevelGameArea levelGameArea) {
        this.levelGameArea = levelGameArea;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    /**
     * Update function to be called by main game loop
     * Checks if a time interval has passed to spawn the next enemy
     */
    public void update() {
        timeSinceLastSpawn += gameTime.getDeltaTime();
        float spawnInterval = 5.0f;
        if (timeSinceLastSpawn >= spawnInterval) {
            spawnEnemy(getLane());
            timeSinceLastSpawn -= spawnInterval;
        }
    }

    /**
     * Gets the next lane from a pre-shuffled sequence of available lanes
     * This prevents long strings of the same lane being chosen
     * @return The next lane number to spawn an enemy in.
     */
    public int getLane() {
        //If all the lanes in the sequence have been used up
        if (waveLanePointer >= waveLaneSequence.size()) {
            Collections.shuffle(waveLaneSequence);
            waveLanePointer = 0;
        }
        //Get the next wave from the sequence
        int lane = waveLaneSequence.get(waveLanePointer);
        waveLanePointer++;
        return lane;
    }

    public void spawnEnemy(int laneNumber) {
        System.out.print("Spawned enemy in lane " + laneNumber);
        levelGameArea.spawnInLane(NPCFactory.createGhost(new Entity()), laneNumber);
    }
}