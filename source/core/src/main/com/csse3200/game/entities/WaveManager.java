package com.csse3200.game.entities;

import java.util.concurrent.ThreadLocalRandom;
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
    }

}
