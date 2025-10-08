package com.csse3200.game.entities;

import com.csse3200.game.entities.configs.BaseLevelConfig;
import com.csse3200.game.entities.configs.BaseSpawnConfig;
import com.csse3200.game.entities.configs.BaseWaveConfig;
import com.csse3200.game.entities.factories.BossFactory;
import com.csse3200.game.services.ServiceLocator;
import java.util.*;
import java.util.LinkedList; // Import LinkedList for the queue
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the lifecycle of enemy waves and schedules spawns over time.
 * ... (rest of javadoc)
 */
public class WaveManager implements WaveConfigProvider {
    private static final Logger logger = LoggerFactory.getLogger(WaveManager.class);

    private int currentWave = 0;
    private String currentLevelKey = "LevelOne";
    private List<Integer> laneOrder = new ArrayList<>(List.of(0, 1, 2, 3, 4));
    private int enemiesToSpawn = 0;
    private int currentEnemyPos;
    private int enemiesDisposed = 0;
    private float timeSinceLastSpawn;
    private boolean waveActive = false;

    // Preparation phase variables
    private boolean preparationPhaseActive = false;
    private float preparationPhaseDuration = 10.0f;
    private float preparationPhaseTimer = 0.0f;
    private final EntitySpawn entitySpawn;

    // Callback interface for spawning enemies
    public interface EnemySpawnCallback {
        void spawnEnemy(int col, int row, String robotType);
        void spawnBoss(int row, BossFactory.BossTypes bossType);
    }

    // Event listener interface for wave events
    public interface WaveEventListener {
        void onPreparationPhaseStarted(int waveNumber);

        void onWaveChanged(int waveNumber);

        void onWaveStarted(int waveNumber);
    }

    private EnemySpawnCallback enemySpawnCallback;
    private WaveEventListener waveEventListener;

    private List<Integer> waveLaneSequence;
    private int waveLanePointer;

    // Wave configuration management
    private boolean levelComplete = false;
    private BaseLevelConfig levelConfig;

    // --- ADDED FOR BOSS MANAGEMENT ---
    private boolean bossActive = false;
    private final Queue<BossFactory.BossTypes> bossSpawnQueue = new LinkedList<>();

    public WaveManager(String levelKey) {
        this.timeSinceLastSpawn = 0f;
        this.waveLaneSequence = new ArrayList<>();
        this.waveLanePointer = 0;
        this.entitySpawn = new EntitySpawn();
        this.entitySpawn.setWaveConfigProvider(this);
        this.preparationPhaseActive = false;
        this.preparationPhaseTimer = 0.0f;
        this.enemiesDisposed = 0;
        this.currentLevelKey = levelKey != null ? levelKey : "levelOne";
        resetToInitialState();
        Collections.shuffle(laneOrder);
        this.levelConfig = ServiceLocator.getConfigService().getLevelConfig(this.currentLevelKey);
        if (levelConfig == null) {
            logger.warn("Level config not found for level {}", this.currentLevelKey);
            this.levelConfig = ServiceLocator.getConfigService().getLevelConfig("levelOne");
        }
    }

    /**
     * Test-only constructor to inject a preconfigured {@link EntitySpawn}. Useful for unit tests that
     * avoid LibGDX file IO.
     *
     * @param entitySpawn spawn helper used by this manager
     */
    public WaveManager(EntitySpawn entitySpawn) {
        this.timeSinceLastSpawn = 0f;
        this.waveLaneSequence = new ArrayList<>();
        this.waveLanePointer = 0;
        this.entitySpawn = entitySpawn;
        this.entitySpawn.setWaveConfigProvider(this);
        this.preparationPhaseActive = false;
        this.preparationPhaseTimer = 0.0f;
        this.enemiesDisposed = 0;

        Collections.shuffle(laneOrder);
    }

    /**
     * Advances to the next wave, resets internal state and lane sequence, and computes the number of
     * enemies to spawn for this wave. Starts with a preparation phase.
     */
    public void initialiseNewWave() {
        // Don't start new waves if level is complete
        if (levelComplete) {
            logger.info("Level complete - no more waves will spawn");
            return;
        }

        setCurrentWave(currentWave + 1);

        // --- MODIFIED BOSS SPAWNING LOGIC ---
        // Instead of spawning directly, queue the boss and set a flag.
        if (currentWave == 1) {
            logger.info("Queuing boss spawn for wave 1: SCRAP_TITAN");
            bossSpawnQueue.add(BossFactory.BossTypes.SCRAP_TITAN);
            bossActive = true;
        } else if (currentWave == 2) {
            logger.info("Queuing boss spawn for wave 2: SAMURAI_BOT");
            bossSpawnQueue.add(BossFactory.BossTypes.SAMURAI_BOT);
            bossActive = true;
        } else if (currentWave == 3) {
            logger.info("Queuing boss spawn for wave 3: GUN_BOT");
            bossSpawnQueue.add(BossFactory.BossTypes.GUN_BOT);
            bossActive = true;
        }

        waveActive = false; // Wave not active during preparation
        preparationPhaseActive = true;
        preparationPhaseTimer = 0.0f;
        currentEnemyPos = 0;
        enemiesDisposed = 0; // Reset disposed counter for new wave
        int maxLanes = Math.min(currentWave + 1, 5);

        entitySpawn.spawnEnemiesFromConfig();
        enemiesToSpawn = entitySpawn.getSpawnCount();
        waveLaneSequence = new ArrayList<>(laneOrder.subList(0, maxLanes));
        Collections.shuffle(waveLaneSequence);
        waveLanePointer = 0;
        if (waveEventListener != null) {
            waveEventListener.onPreparationPhaseStarted(currentWave);
            waveEventListener.onWaveChanged(currentWave);
        }
    }

    /**
     * Ends the current wave and immediately begins the next one. External systems listen to wave
     * change events for UI updates.
     */
    public void endWave() {
        waveActive = false;
        initialiseNewWave();
    }

    /** Starts the actual wave after preparation phase ends. */
    private void startWave() {
        waveActive = true;
        preparationPhaseActive = false;
        timeSinceLastSpawn = 0.0f; // Reset spawn timer for new wave

        if (waveEventListener != null) {
            waveEventListener.onWaveStarted(currentWave);
        }
    }

    public void setEnemySpawnCallback(EnemySpawnCallback callback) {
        this.enemySpawnCallback = callback;
    }

    public void setWaveEventListener(WaveEventListener listener) {
        this.waveEventListener = listener;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    private void setCurrentWave(int wave) {
        currentWave = wave;
    }

    public boolean isPreparationPhaseActive() {
        return preparationPhaseActive;
    }

    public float getPreparationPhaseRemainingTime() {
        if (!preparationPhaseActive) {
            return 0.0f;
        }
        return Math.max(0.0f, preparationPhaseDuration - preparationPhaseTimer);
    }

    public float getPreparationPhaseDuration() {
        return preparationPhaseDuration;
    }

    /**
     * Called when an enemy is disposed/destroyed. Updates the disposed counter and checks if the wave
     * should end.
     */
    public void onEnemyDisposed() {
        enemiesDisposed++;
        logger.debug(
                "Enemy disposed. Count: {}/{} (spawned: {})",
                enemiesDisposed,
                enemiesToSpawn,
                currentEnemyPos);

        // --- MODIFIED WAVE COMPLETION LOGIC ---
        // Wave can only end if all enemies are spawned AND disposed AND no boss is active.
        if (enemiesDisposed >= enemiesToSpawn && currentEnemyPos >= enemiesToSpawn && waveActive && !bossActive) {
            logger.info("Wave {} completed! All enemies spawned and disposed.", currentWave);

            int maxWaves = getCurrentLevelWaveCount();
            if (currentWave >= maxWaves) {
                logger.info("All waves completed for level {}! Level complete!", currentLevelKey);
                levelComplete = true;
                waveActive = false;
            } else {
                endWave(); // Only call endWave() for non-final waves
            }
        }
    }

    /**
     * Called when a boss is defeated. This is the new progression gate.
     */
    public void onBossDefeated() {
        logger.info("Boss defeated! Ending wave {} early.", currentWave);
        bossActive = false; // The gate is now open.

        // Check if the rest of the wave was already cleared. If so, end the wave now.
        if (enemiesDisposed >= enemiesToSpawn && currentEnemyPos >= enemiesToSpawn && waveActive) {
            logger.info("All minions were already cleared. Proceeding to next wave.");
            endWave();
        }
    }

    public int getEnemiesDisposed() {
        return enemiesDisposed;
    }

    public int getEnemiesSpawned() {
        return currentEnemyPos;
    }

    public boolean isLevelComplete() {
        return levelComplete;
    }

    public void resetLevel() {
        levelComplete = false;
        setCurrentWave(0);
        enemiesDisposed = 0;
        waveActive = false;
        preparationPhaseActive = false;
        preparationPhaseTimer = 0.0f;
        currentEnemyPos = 0;
        waveLaneSequence.clear();
        waveLanePointer = 0;
        // --- ADDED ---
        bossActive = false;
        bossSpawnQueue.clear();
        logger.info("Level reset - ready for new level");
    }

    public void resetToInitialState() {
        currentWave = 0;
        levelComplete = false;
        enemiesDisposed = 0;
        waveActive = false;
        preparationPhaseActive = false;
        preparationPhaseTimer = 0.0f;
        currentEnemyPos = 0;
        enemiesToSpawn = 0;
        timeSinceLastSpawn = 0f;
        waveLaneSequence.clear();
        waveLanePointer = 0;
        Collections.shuffle(laneOrder);
        // --- ADDED ---
        bossActive = false;
        bossSpawnQueue.clear();
        logger.info("WaveManager reset to initial state - ready for new game");
    }

    public int getEnemiesRemaining() {
        return Math.max(0, enemiesToSpawn - enemiesDisposed);
    }

    public String getCurrentLevelKey() {
        return currentLevelKey;
    }

    public void setCurrentLevel(String levelKey) {
        this.currentLevelKey = levelKey;
        this.levelConfig = ServiceLocator.getConfigService().getLevelConfig(this.currentLevelKey);
        if (levelConfig == null) {
            logger.warn("Level config not found for level {}", this.currentLevelKey);
            this.levelConfig = ServiceLocator.getConfigService().getLevelConfig("LevelOne");
        }
        resetLevel();
        logger.info("Level set to {}", levelKey);
    }

    /**
     * Update function to be called by main game loop. Handles preparation phase timer and enemy
     * spawning.
     *
     * @param deltaTime time elapsed since last update in seconds
     */
    public void update(float deltaTime) {
        // --- ADDED BOSS SPAWNING LOGIC ---
        // Safely spawn bosses from the queue, outside the physics step.
        if (!bossSpawnQueue.isEmpty()) {
            BossFactory.BossTypes bossToSpawn = bossSpawnQueue.poll();
            if (enemySpawnCallback != null) {
                enemySpawnCallback.spawnBoss(2, bossToSpawn);
            }
        }

        // Handle preparation phase
        if (preparationPhaseActive) {
            preparationPhaseTimer += deltaTime;
            if (preparationPhaseTimer >= preparationPhaseDuration) {
                startWave();
            }
            return; // Don't spawn enemies during preparation phase
        }

        // Handle wave spawning
        if (waveActive) {
            timeSinceLastSpawn += deltaTime;
            float spawnInterval = 5.0f;
            if (timeSinceLastSpawn >= spawnInterval) {
                spawnEnemy(getLane());
                timeSinceLastSpawn -= spawnInterval;
            }
        }
    }

    public int getLane() {
        if (waveLaneSequence.isEmpty()) {
            waveLaneSequence = new ArrayList<>(List.of(0, 1, 2, 3, 4));
        }

        if (waveLanePointer >= waveLaneSequence.size()) {
            Collections.shuffle(waveLaneSequence);
            waveLanePointer = 0;
        }
        int lane = waveLaneSequence.get(waveLanePointer);
        waveLanePointer++;
        return lane;
    }

    public void spawnEnemy(int laneNumber) {
        if (currentEnemyPos >= enemiesToSpawn) {
            return;
        }
        if (enemySpawnCallback == null) {
            logger.warn("No enemy spawn callback set - cannot spawn enemy");
            return;
        }
        String robotType = entitySpawn.getNextRobotType();
        enemySpawnCallback.spawnEnemy(9, laneNumber, robotType);
        currentEnemyPos++;
    }

    public int getWaveCountForLevel(String levelKey) {
        BaseLevelConfig config = ServiceLocator.getConfigService().getLevelConfig(levelKey);
        return config != null ? config.getWaves().size() : 0;
    }

    public int getCurrentLevelWaveCount() {
        return levelConfig != null ? levelConfig.getWaves().size() : 0;
    }

    public int getWaveWeight() {
        BaseWaveConfig waveConfig = getCurrentWaveConfig();
        return waveConfig != null ? waveConfig.getWaveWeight() : 20;
    }

    public int getMinZombiesSpawn() {
        BaseWaveConfig waveConfig = getCurrentWaveConfig();
        return waveConfig != null ? waveConfig.getMinZombiesSpawn() : 5;
    }

    public Map<String, BaseSpawnConfig> getEnemyConfigs() {
        BaseWaveConfig wave = getCurrentWaveConfig();

        if (wave == null || wave.getSpawnConfigs() == null) {
            return new java.util.HashMap<>();
        }
        return wave.getSpawnConfigs();
    }

    private BaseWaveConfig getCurrentWaveConfig() {
        int waveNumber = getCurrentWave();
        int waveIndex = waveNumber - 1;

        if (levelConfig != null
                && levelConfig.getWaves() != null
                && waveIndex >= 0
                && waveIndex < levelConfig.getWaves().size()) {
            return levelConfig.getWaves().get(waveIndex);
        }
        return new BaseWaveConfig();
    }
}