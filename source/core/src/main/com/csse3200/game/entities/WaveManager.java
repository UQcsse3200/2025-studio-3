package com.csse3200.game.entities;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.entities.factories.NPCFactory;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;

public class WaveManager {

    private int currentWave;
    private int maxLanes;
    private Entity[] enemies = {new Entity()};
    private int currentEnemy;
    private int[] laneWeights = {1, 1, 1, 1, 1};

    private final GameTime gameTime;
    private float timeSinceLastSpawn;

    private final float spawnInterval = 5.0f;

    private LevelGameArea levelGameArea;

    public WaveManager() {
        this.gameTime = new GameTime();
        this.currentWave = 0;
        this.timeSinceLastSpawn = 0f;
    }

    public void initialiseNewWave() {
        currentWave++;
        currentEnemy = enemies[0].getId();
        maxLanes = Math.min(currentWave + 1, 5);
    }

    public void setGameArea(LevelGameArea levelGameArea) {
        this.levelGameArea = levelGameArea;
    }
    public int getCurrentWave() {
        return currentWave;
    }

    public void update() {
        timeSinceLastSpawn += gameTime.getDeltaTime();
        if (timeSinceLastSpawn >= spawnInterval) {
            spawnEnemy(getLane());
            timeSinceLastSpawn -= spawnInterval;
        }
    }

    public int getLane() {
        //will be expanded to intelligently pick lanes soon
        return ThreadLocalRandom.current().nextInt(0, maxLanes);
    }

    public void spawnEnemy(int laneNumber) {
        System.out.print("Spawned enemy in lane " + laneNumber);
        levelGameArea.spawnInLane(NPCFactory.createGhost(new Entity()), laneNumber);
    }

}
