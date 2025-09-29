package com.csse3200.game.services;

import com.csse3200.game.entities.WaveManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaveService {

    private static final Logger logger = LoggerFactory.getLogger(WaveService.class);
    private final WaveManager waveManager;

    public WaveService() {
        waveManager = new WaveManager("levelOne");
    }

    public void update(float delta) {
        waveManager.update(delta);
    }

    public int getCurrentWave() {
        return waveManager.getCurrentWave();
    }

    public void initialiseNewWave() {
        waveManager.initialiseNewWave();
    }

    public void setEnemySpawnCallback(WaveManager.EnemySpawnCallback callback) {
        waveManager.setEnemySpawnCallback(callback);
    }

    public void setWaveEventListener(WaveManager.WaveEventListener listener) {
        waveManager.setWaveEventListener(listener);
    }

    public void onEnemyDispose() {
        waveManager.onEnemyDisposed();
    }

}