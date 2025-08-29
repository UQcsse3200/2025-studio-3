package com.csse3200.game.entities;

import com.csse3200.game.entities.factories.WaveFactory;

public class EntitySpawn {
    WaveFactory waveFactory = new WaveFactory("wave1");
    Entity[] entities = {};
    public EntitySpawn(String wave) {

    }

    public void spawnEnemies() {
        int weight = waveFactory.getWaveWeight();


    }
}
