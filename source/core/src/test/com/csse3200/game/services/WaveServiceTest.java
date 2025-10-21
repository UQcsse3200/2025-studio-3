package com.csse3200.game.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.csse3200.game.entities.configs.BaseLevelConfig;
import com.csse3200.game.entities.configs.BaseSpawnConfig;
import com.csse3200.game.entities.configs.BaseWaveConfig;
import com.csse3200.game.entities.factories.BossFactory;
import com.csse3200.game.entities.factories.RobotFactory.RobotType;
import com.csse3200.game.extensions.GameExtension;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

/** Tests for WaveService state transitions, boss queue, fallbacks, and level switching. */
@ExtendWith(GameExtension.class)
class WaveServiceTest {

  private ConfigService mockConfigService;
  private BaseLevelConfig mockLevelConfig;

  @BeforeEach
  void setUp() {
    mockConfigService = mock(ConfigService.class);
    ServiceLocator.registerConfigService(mockConfigService);

    mockLevelConfig = mock(BaseLevelConfig.class);
    // WaveService constructor "levelOne" and "LevelOne" configs
    when(mockConfigService.getLevelConfig("levelOne")).thenReturn(mockLevelConfig);
    when(mockConfigService.getLevelConfig("LevelOne")).thenReturn(mockLevelConfig);
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear(); // Clean up after each test
  }

  // --- helpers ---

  // Mock wave config with given weight, min enemies, and spawn settings
  private static BaseWaveConfig wave(int weight, int min, Map<String, BaseSpawnConfig> cfg) {
    BaseWaveConfig w = mock(BaseWaveConfig.class);
    when(w.getWaveWeight()).thenReturn(weight);
    when(w.getMinZombiesSpawn()).thenReturn(min);
    when(w.getSpawnConfigs()).thenReturn(cfg);
    return w;
  }

  // Creates a simple spawn config with one valid enemy
  private static Map<String, BaseSpawnConfig> simpleSpawnCfg() {
    BaseSpawnConfig std = mock(BaseSpawnConfig.class);
    when(std.getCost()).thenReturn(2); // Valid cost
    when(std.getChance()).thenReturn(1.0f); // Ensures spawning
    return Map.of("standard", std);
  }

  // Assigns given waves to the mock level
  private static void setWaves(BaseLevelConfig level, BaseWaveConfig... waves) {
    when(level.getWaves()).thenReturn(Arrays.asList(waves));
  }

  // --- tests ---

  @Test
  void initialiseNewWave_entersPreparation_and_firesEvents() {
    setWaves(mockLevelConfig, wave(10, 2, simpleSpawnCfg()));

    WaveService svc = new WaveService(); // uses real EntitySpawn internally

    final boolean[] prep = {false};
    final boolean[] changed = {false};
    svc.setWaveEventListener(
        new WaveService.WaveEventListener() {
          @Override
          public void onPreparationPhaseStarted(int waveNumber) {
            if (waveNumber == 1) prep[0] = true;
          }

          @Override
          public void onWaveChanged(int waveNumber) {
            if (waveNumber == 1) changed[0] = true;
          }

          @Override
          public void onWaveStarted(int waveNumber) {}
        });

    svc.initialiseNewWave(); // Start wave 1

    assertEquals(1, svc.getCurrentWave());
    assertTrue(svc.isPreparationPhaseActive(), "Preparation should be active immediately");
    assertTrue(prep[0], "onPreparationPhaseStarted should fire"); // reparation event fired
    assertTrue(changed[0], "onWaveChanged should fire"); // Wave changed event fired
    assertTrue(svc.getEnemiesRemaining() >= 0);
  }

  @Test
  void update_transitionsFromPreparationToActive_and_firesStartEvent() {
    setWaves(mockLevelConfig, wave(10, 2, simpleSpawnCfg()));

    WaveService svc = new WaveService();

    final boolean[] started = {false};

    // Checks when the wave has started
    svc.setWaveEventListener(
        new WaveService.WaveEventListener() {
          @Override
          public void onPreparationPhaseStarted(int waveNumber) {}

          @Override
          public void onWaveChanged(int waveNumber) {}

          @Override
          public void onWaveStarted(int waveNumber) {
            started[0] = (waveNumber == 1);
          }
        });

    svc.initialiseNewWave();
    svc.update(2.5f); // Prep time (pre-wave)
    assertTrue(svc.isPreparationPhaseActive());
    svc.update(2.6f); // Wave start
    assertFalse(svc.isPreparationPhaseActive(), "Should exit preparation after 5 seconds total");
    assertTrue(started[0], "onWaveStarted must fire when wave begins");
  }

  @Test
  void bossQueue_spawnsBossOnUpdate_forFirstThreeWaves() {
    // 3 waves to test boss spawning order
    setWaves(
        mockLevelConfig,
        wave(10, 1, simpleSpawnCfg()),
        wave(10, 1, simpleSpawnCfg()),
        wave(10, 1, simpleSpawnCfg()));

    WaveService svc = new WaveService();
    List<BossFactory.BossTypes> bosses = new ArrayList<>();

    // Track spawned bosses
    svc.setEnemySpawnCallback(
        new WaveService.EnemySpawnCallback() {
          @Override
          public void spawnEnemy(int col, int row, RobotType robotType) {}

          @Override
          public void spawnBoss(int row, BossFactory.BossTypes bossType) {
            bosses.add(bossType);
          }
        });

    // Wave 1
    svc.initialiseNewWave();
    svc.update(0.0f);

    // Wave 2
    svc.endWave();
    svc.update(0.0f);

    // Wave 3
    svc.endWave();
    svc.update(0.0f);

    // Expected boss order
    assertEquals(
        List.of(
            BossFactory.BossTypes.SCRAP_TITAN,
            BossFactory.BossTypes.SAMURAI_BOT,
            BossFactory.BossTypes.GUN_BOT),
        bosses);
  }

  @Test
  void onBossDefeated_progressesWaves_andCompletesLevelOnLastWave() {
    setWaves(mockLevelConfig, wave(10, 1, simpleSpawnCfg()), wave(10, 1, simpleSpawnCfg()));

    WaveService svc = new WaveService();

    // Wave 1
    svc.initialiseNewWave();
    svc.onBossDefeated(); // Defeat boss in wave 1
    assertFalse(svc.isLevelComplete(), "Not complete after defeating boss in wave 1");
    assertEquals(2, svc.getCurrentWave(), "Should move to wave 2");

    // Wave 2
    svc.onBossDefeated(); // Defeat final boss
    assertTrue(svc.isLevelComplete(), "Level should complete after last boss");
  }

  @Test
  void getLane_alwaysWithinBounds_andCycles() {
    setWaves(mockLevelConfig, wave(10, 1, simpleSpawnCfg()));
    WaveService svc = new WaveService();

    svc.initialiseNewWave();

    // Pull enough lanes to force reshuffle path
    for (int i = 0; i < 12; i++) {
      int lane = svc.getLane();
      assertTrue(lane >= 0 && lane <= 4, "lane index must be in [0,4]");
    }
  }

  @Test
  void setCurrentLevel_loadsConfig_andResetsState() {
    // default levelOne (1 wave)
    setWaves(mockLevelConfig, wave(10, 1, simpleSpawnCfg()));

    // New level with 3 waves
    BaseLevelConfig newLevel = mock(BaseLevelConfig.class);
    setWaves(
        newLevel,
        wave(7, 1, simpleSpawnCfg()),
        wave(8, 2, simpleSpawnCfg()),
        wave(9, 3, simpleSpawnCfg()));
    when(mockConfigService.getLevelConfig("newLevel")).thenReturn(newLevel);

    WaveService svc = new WaveService();

    svc.setCurrentLevel("newLevel"); // Switch level

    assertEquals("newLevel", svc.getCurrentLevelKey());
    assertEquals(0, svc.getCurrentWave(), "Reset to pre-wave state");
    assertEquals(3, svc.getCurrentLevelWaveCount(), "Loaded new wave count");
    assertFalse(svc.isLevelComplete());
  }

  @Test
  void providerFallbacks_whenNoWaveConfigAvailable() {
    // If there's no waves, use default
    when(mockLevelConfig.getWaves()).thenReturn(Collections.emptyList());

    WaveService svc = new WaveService();
    svc.setCurrentWave(1);

    assertEquals(0, svc.getWaveWeight(), "Default new BaseWaveConfig returns 0 weight");
    assertEquals(0, svc.getMinZombiesSpawn(), "Default new BaseWaveConfig returns 0 min spawn");
    assertTrue(svc.getEnemyConfigs().isEmpty(), "Enemy configs fallback to empty map");
  }

  @Test
  void spawnEnemy_triggersCallback_whenWaveActive() {
    setWaves(mockLevelConfig, wave(10, 2, simpleSpawnCfg()));
    WaveService svc = new WaveService();

    final int[] spawned = {0};

    // Count number of enemies spawned via callback
    svc.setEnemySpawnCallback(
        new WaveService.EnemySpawnCallback() {
          @Override
          public void spawnEnemy(int col, int row, RobotType robotType) {
            spawned[0]++;
          }

          @Override
          public void spawnBoss(int row, BossFactory.BossTypes bossType) {}
        });

    svc.initialiseNewWave();
    svc.update(5.1f); // Start the wave
    svc.spawnEnemy(0);

    // Verify it calls the callback at least once.
    assertTrue(spawned[0] >= 1, "Expected at least one enemy spawn via callback");
    assertTrue(svc.getEnemiesSpawned() >= 1, "Enemies spawned counter should advance");
  }

  @Test
  void endWave_startsNextPreparationPhase() {
    setWaves(mockLevelConfig, wave(10, 1, simpleSpawnCfg()), wave(10, 1, simpleSpawnCfg()));

    WaveService svc = new WaveService();

    svc.initialiseNewWave();
    assertEquals(1, svc.getCurrentWave());

    svc.endWave(); // Move to next wave

    assertEquals(2, svc.getCurrentWave(), "Should increment wave");
    assertTrue(svc.isPreparationPhaseActive(), "Should be back in preparation for next wave");
  }

  @Test
  void resets_clearRuntimeState() {
    setWaves(mockLevelConfig, wave(10, 1, simpleSpawnCfg()));
    WaveService svc = new WaveService();

    svc.initialiseNewWave();
    svc.update(5.2f); // start wave
    svc.spawnEnemy(0);

    svc.resetLevel(); // Reset progress and counters

    assertEquals(0, svc.getCurrentWave());
    assertEquals(0, svc.getEnemiesSpawned());
    assertEquals(0, svc.getEnemiesDisposed());
    assertFalse(svc.isPreparationPhaseActive());
    assertFalse(svc.isLevelComplete());

    // Full reset-to-initial should also be clean
    svc.resetToInitialState();
    assertEquals(0, svc.getCurrentWave());
    assertEquals(0, svc.getEnemiesSpawned());
    assertEquals(0, svc.getEnemiesDisposed());
    assertFalse(svc.isPreparationPhaseActive());
    assertFalse(svc.isLevelComplete());
  }
}
