package com.csse3200.game.entities;

import com.csse3200.game.entities.factories.WaveFactory;

public class EntitySpawn {
    private final WaveFactory waveFactory;

    // For now, only one robot type exists.
    // Set this to the weight defined for that robot.
    private final int robotWeight;
    private Entity[] entities = new Entity[0];

    public EntitySpawn(int wave) {
        this(wave, /* robotWeight */ 2); // TODO: replace 2 with the actual robot weight
    }

    public EntitySpawn(int wave, int robotWeight) {
        this.waveFactory = new WaveFactory(wave);
        this.robotWeight = robotWeight;
    }

    public Entity[] getEntities() {
        return entities;
    }

    public void spawnEnemies() {
        int waveWeight = waveFactory.getWaveWeight();
        int minCount   = waveFactory.getMinZombiesSpawn();

        if (robotWeight <= 0 || waveWeight <= 0) {
            entities = new Entity[0];
            return;
        }

        // If not divisible, add 1 toe the waveWeight
        if (waveWeight % robotWeight != 0) {
            waveWeight += 1;
        }

        // If the exact robotSpawn is still below the minimum, we can also bump robotSpawn up
        int robotSpawn = waveWeight / robotWeight;
        if (robotSpawn < minCount) {
            robotSpawn = minCount;
            waveWeight = robotSpawn * robotWeight;
        }

        // Builds the array of entities
        Entity[] result = new Entity[robotSpawn];
        for (int i = 0; i < robotSpawn; i++) {
            // TODO: replace with the actual factory method from the robot team
            result[i] = new Entity();
        }
        entities = result;
    }
}
