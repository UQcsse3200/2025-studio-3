package com.csse3200.game.entities;

import java.util.concurrent.ThreadLocalRandom;

public class WaveManager {

    private int currentWave = 0;
    private int maxLanes;
    private Entity[] enemies = {};
    private int currentEnemy;
    private int[] laneWeights = {1,1,1,1,1};

    public void startNewWave(Entity[] enemies) {
        currentWave++;
        currentEnemy = enemies[0].getId();
        maxLanes = Math.min(currentWave + 1, 5);
        laneManager();
    }

    public int getCurrentWave() {
        return currentWave;
    }


    public void laneManager() {
        int i = ThreadLocalRandom.current().nextInt(0, maxLanes);
        spawnEnemy(i);
    }

    public void spawnEnemy(int laneNumber) {
        System.out.print("Spawned enemy in lane " + laneNumber);
    }

}
