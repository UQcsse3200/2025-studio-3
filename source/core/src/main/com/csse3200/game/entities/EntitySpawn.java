package com.csse3200.game.entities;

import com.csse3200.game.entities.factories.WaveFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EntitySpawn {
    private final WaveFactory waveFactory;
    private final Random random;

    public EntitySpawn(String wave, Random random) {
        this.waveFactory = new WaveFactory(wave);
        this.random = random;
    }

    public List<Entity> spawnEnemies() {
        int waveWeight = waveFactory.getWaveWeight();
        Map<String, Integer> robotWeights = waveFactory.getEnemyWeights();

        List<Entity> spawned = new ArrayList<>();

        int currentWeight = 0;
        List<String> robotTypes = new ArrayList<>(robotWeights.keySet());

        while (currentWeight < waveWeight) {
            String robotType = robotTypes.get(random.nextInt(robotTypes.size()));
            int robotWeight = robotWeights.get(robotType);

            if (currentWeight + robotWeight <= waveWeight) {
                // Entity enemy = waveFactory.createRobot(robotType);
                // spawned.add(enemy);
                currentWeight += robotWeight;
            }
        }
        return spawned;
    }
}
