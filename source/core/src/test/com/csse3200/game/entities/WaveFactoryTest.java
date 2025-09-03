package com.csse3200.game.entities;

import com.csse3200.game.entities.factories.WaveFactory;
import com.csse3200.game.extensions.GameExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(GameExtension.class)
public class WaveFactoryTest {

    @Test
    void getWaveWeightTest() {
        WaveFactory waveFactory = new WaveFactory();
        WaveManager waveManager = new WaveManager();
        waveManager.initialiseNewWave();
        int wave1Weight = 10;
        assertEquals(wave1Weight, waveFactory.getWaveWeight());
    }

    @Test
    void getMinZombiesTest() {
        WaveFactory waveFactory = new WaveFactory();
        WaveManager waveManager = new WaveManager();
        waveManager.initialiseNewWave();
        int wave1MinZombies = 5;
        assertEquals(wave1MinZombies, waveFactory.getMinZombiesSpawn());
    }

    @Test
    void getExpGainedTest() {
        WaveFactory waveFactory = new WaveFactory();
        WaveManager waveManager = new WaveManager();
        waveManager.initialiseNewWave();
        int wave1ExpGained = 10;
        assertEquals(wave1ExpGained, waveFactory.getExpGained());
    }

    @Test
    void getWaveWeightTest2() {
        WaveFactory waveFactory = new WaveFactory();
        WaveManager waveManager = new WaveManager();
        waveManager.initialiseNewWave();
        waveManager.initialiseNewWave();
        int wave2WaveWeight = 50;
        assertEquals(wave2WaveWeight, waveFactory.getWaveWeight());
    }

    @Test
    void getMinZombiesTest2() {
        WaveFactory waveFactory = new WaveFactory();
        WaveManager waveManager = new WaveManager();
        waveManager.initialiseNewWave();
        waveManager.initialiseNewWave();
        int wave2MinZombies = 10;
        assertEquals(wave2MinZombies, waveFactory.getWaveWeight());
    }

    @Test
    void getExpGainedTest2() {
        WaveFactory waveFactory = new WaveFactory();
        WaveManager waveManager = new WaveManager();
        waveManager.initialiseNewWave();
        waveManager.initialiseNewWave();
        int wave2ExpGained = 20;
        assertEquals(wave2ExpGained, waveFactory.getExpGained());
    }

    @Test
    void getWaveWeightTest3() {
        WaveFactory waveFactory = new WaveFactory();
        WaveManager waveManager = new WaveManager();
        waveManager.initialiseNewWave();
        waveManager.initialiseNewWave();
        waveManager.initialiseNewWave();
        int wave3WaveWeight = 75;
        assertEquals(wave3WaveWeight, waveFactory.getWaveWeight());
    }

    @Test
    void getMinZombiesTest3() {
        WaveFactory waveFactory = new WaveFactory();
        WaveManager waveManager = new WaveManager();
        waveManager.initialiseNewWave();
        waveManager.initialiseNewWave();
        waveManager.initialiseNewWave();
        int wave3MinZombies = 15;
        assertEquals(wave3MinZombies, waveFactory.getMinZombiesSpawn());
    }

    @Test
    void getExpGainedTest3() {
        WaveFactory waveFactory = new WaveFactory();
        WaveManager waveManager = new WaveManager();
        waveManager.initialiseNewWave();
        waveManager.initialiseNewWave();
        waveManager.initialiseNewWave();
        int wave3ExpGained = 30;
        assertEquals(wave3ExpGained, waveFactory.getExpGained());
    }



}
