package com.csse3200.game.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.csse3200.game.entities.configs.BaseLevelConfig;
import com.csse3200.game.entities.configs.BaseSpawnConfig;
import com.csse3200.game.entities.factories.RobotFactory.RobotType;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.WaveService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class EntitySpawnTest {

  @BeforeEach
  void setUp() {
    // Mock ConfigService
    ConfigService mockConfigService = mock(ConfigService.class);
    ServiceLocator.registerConfigService(mockConfigService);

    // Mock level config
    BaseLevelConfig mockLevelConfig = mock(BaseLevelConfig.class);
    when(mockConfigService.getLevelConfig("levelOne")).thenReturn(mockLevelConfig);
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear();
  }

  @Test
  void spawnEnemiesCheckWeightAndMinimum() {
    // Create a mock WaveService for testing
    WaveService mockWaveService =
        new WaveService() {
          @Override
          public int getWaveWeight() {
            return 10;
          }

          @Override
          public int getMinZombiesSpawn() {
            return 5;
          }

          @Override
          public Map<String, BaseSpawnConfig> getEnemyConfigs() {
            Map<String, BaseSpawnConfig> configs = new HashMap<>();

            // Create mock configs since BaseSpawnConfig fields are private
            BaseSpawnConfig standardConfig = mock(BaseSpawnConfig.class);
            when(standardConfig.getCost()).thenReturn(2);
            when(standardConfig.getChance()).thenReturn(1.0f);
            configs.put("standard", standardConfig);

            BaseSpawnConfig fastConfig = mock(BaseSpawnConfig.class);
            when(fastConfig.getCost()).thenReturn(2);
            when(fastConfig.getChance()).thenReturn(0.0f);
            configs.put("fast", fastConfig);

            BaseSpawnConfig tankyConfig = mock(BaseSpawnConfig.class);
            when(tankyConfig.getCost()).thenReturn(2);
            when(tankyConfig.getChance()).thenReturn(0.0f);
            configs.put("tanky", tankyConfig);

            BaseSpawnConfig bungeeConfig = mock(BaseSpawnConfig.class);
            when(bungeeConfig.getCost()).thenReturn(2);
            when(bungeeConfig.getChance()).thenReturn(0.0f);
            configs.put("bungee", bungeeConfig);

            BaseSpawnConfig gunnerRobotConfig = mock(BaseSpawnConfig.class);
            when(gunnerRobotConfig.getCost()).thenReturn(2);
            when(gunnerRobotConfig.getChance()).thenReturn(0.0f);
            configs.put("gunnerRobot", gunnerRobotConfig);

            return configs;
          }
        };

    EntitySpawn spawner = new EntitySpawn(mockWaveService, 2); // robotWeight = 2
    // Use the new method that works with the level-based system
    spawner.spawnEnemiesFromConfig();
    assertEquals(5, spawner.getSpawnCount());
  }

  @Test
  void chanceProportionsRespectedDeterministically() {
    // Costs equal; chances sum to 1.0 -> expect counts ~ proportional exactly
    // waveWeight=20, cost=2 => 10 spawns total
    // standard 0.5 => 5, fast 0.3 => 3, tanky 0.2 => 2
    WaveService ws =
        new WaveService() {
          @Override
          public int getWaveWeight() {
            return 20;
          }

          @Override
          public int getMinZombiesSpawn() {
            return 0;
          }

          @Override
          public Map<String, BaseSpawnConfig> getEnemyConfigs() {
            Map<String, BaseSpawnConfig> m = new HashMap<>();
            m.put("standard", mockCfg(2, 0.5f));
            m.put("fast", mockCfg(2, 0.3f));
            m.put("tanky", mockCfg(2, 0.2f));
            return m;
          }
        };

    EntitySpawn spawner = new EntitySpawn(ws, 2);
    spawner.spawnEnemiesFromConfig();

    assertEquals(10, spawner.getSpawnCount());
    Map<RobotType, Integer> counts = drainCounts(spawner);
    assertEquals(5, counts.getOrDefault(RobotType.STANDARD, 0));
    assertEquals(3, counts.getOrDefault(RobotType.FAST, 0));
    assertEquals(2, counts.getOrDefault(RobotType.TANKY, 0));
  }

  @Test
  void zeroChanceTypesDoNotAppearWhenOthersPositive() {
    // High chance for 'fast', zero for others -> only fast should appear
    // waveWeight=14, cost=2 => 7 spawns
    WaveService ws =
        new WaveService() {
          @Override
          public int getWaveWeight() {
            return 14;
          }

          @Override
          public int getMinZombiesSpawn() {
            return 0;
          }

          @Override
          public Map<String, BaseSpawnConfig> getEnemyConfigs() {
            Map<String, BaseSpawnConfig> m = new HashMap<>();
            m.put("standard", mockCfg(2, 0.0f));
            m.put("fast", mockCfg(2, 1.0f));
            m.put("tanky", mockCfg(2, 0.0f));
            m.put("bungee", mockCfg(2, 0.0f));
            return m;
          }
        };

    EntitySpawn spawner = new EntitySpawn(ws, 2);
    spawner.spawnEnemiesFromConfig();

    assertEquals(7, spawner.getSpawnCount());
    Map<RobotType, Integer> counts = drainCounts(spawner);
    assertEquals(7, counts.getOrDefault(RobotType.FAST, 0));
    assertEquals(0, counts.getOrDefault(RobotType.STANDARD, 0));
    assertEquals(0, counts.getOrDefault(RobotType.TANKY, 0));
    assertEquals(0, counts.getOrDefault(RobotType.BUNGEE, 0));
  }

  @Test
  void allZeroChancesBecomeEqualWeights() {
    // All chances = 0 -> treat as equal weights; with 3 types, budget for 6 spawns -> expect 2 each
    // (deterministic SWRR)
    WaveService ws =
        new WaveService() {
          @Override
          public int getWaveWeight() {
            return 12;
          } // cost=2 -> 6 spawns

          @Override
          public int getMinZombiesSpawn() {
            return 0;
          }

          @Override
          public Map<String, BaseSpawnConfig> getEnemyConfigs() {
            Map<String, BaseSpawnConfig> m = new HashMap<>();
            m.put("fast", mockCfg(2, 0.0f));
            m.put("tanky", mockCfg(2, 0.0f));
            m.put("bungee", mockCfg(2, 0.0f));
            return m;
          }
        };

    EntitySpawn spawner = new EntitySpawn(ws, 2);
    spawner.spawnEnemiesFromConfig();

    assertEquals(6, spawner.getSpawnCount());
    Map<RobotType, Integer> counts = drainCounts(spawner);
    assertEquals(2, counts.getOrDefault(RobotType.FAST, 0));
    assertEquals(2, counts.getOrDefault(RobotType.TANKY, 0));
    assertEquals(2, counts.getOrDefault(RobotType.BUNGEE, 0));
  }

  @Test
  void enforcesMinimumViaCheapestCostWhenBudgetTooSmall() {
    // waveWeight too small to reach min; effectiveBudget must be lifted to min * cheapest
    // cheapest cost=2, min=4 -> expect 4 spawns even if waveWeight=1
    WaveService ws =
        new WaveService() {
          @Override
          public int getWaveWeight() {
            return 1;
          } // insufficient

          @Override
          public int getMinZombiesSpawn() {
            return 4;
          }

          @Override
          public Map<String, BaseSpawnConfig> getEnemyConfigs() {
            Map<String, BaseSpawnConfig> m = new HashMap<>();
            m.put("standard", mockCfg(2, 1.0f));
            return m;
          }
        };

    EntitySpawn spawner = new EntitySpawn(ws, 2);
    spawner.spawnEnemiesFromConfig();

    assertEquals(4, spawner.getSpawnCount());
    Map<RobotType, Integer> counts = drainCounts(spawner);
    assertEquals(4, counts.getOrDefault(RobotType.STANDARD, 0));
  }

  @Test
  void expensiveTypesAreSkippedWhenUnaffordable() {
    // Expensive cost=6 cannot ever fit into waveWeight=10 if we already consume with cheaper ones?
    // Simpler: make waveWeight=5 and cheapest=2 -> expensive 6 never affordable at all.
    WaveService ws =
        new WaveService() {
          @Override
          public int getWaveWeight() {
            return 5;
          }

          @Override
          public int getMinZombiesSpawn() {
            return 0;
          }

          @Override
          public Map<String, BaseSpawnConfig> getEnemyConfigs() {
            Map<String, BaseSpawnConfig> m = new HashMap<>();
            m.put("fast", mockCfg(2, 0.1f));
            m.put("tanky", mockCfg(6, 0.9f));
            return m;
          }
        };

    EntitySpawn spawner = new EntitySpawn(ws, 2);
    spawner.spawnEnemiesFromConfig();

    // 5 budget, cheapest=2 -> expect 2 spawns (2+2=4) and remaining 1 can't afford anything else
    assertEquals(2, spawner.getSpawnCount());
    Map<RobotType, Integer> counts = drainCounts(spawner);
    assertEquals(2, counts.getOrDefault(RobotType.FAST, 0));
    assertEquals(0, counts.getOrDefault(RobotType.TANKY, 0));
  }

  @Test
  void deterministicOrderWithTiesAlternatesAlphabetically() {
    // Equal cost and equal chance; SWRR with stable alphabetical ordering -> near alternation
    WaveService ws =
        new WaveService() {
          @Override
          public int getWaveWeight() {
            return 8;
          } // cost=2 -> 4 spawns

          @Override
          public int getMinZombiesSpawn() {
            return 0;
          }

          @Override
          public Map<String, BaseSpawnConfig> getEnemyConfigs() {
            Map<String, BaseSpawnConfig> m = new HashMap<>();
            m.put("fast", mockCfg(2, 1.0f));
            m.put("tanky", mockCfg(2, 1.0f));
            return m;
          }
        };

    EntitySpawn spawner = new EntitySpawn(ws, 2);
    spawner.spawnEnemiesFromConfig();

    assertEquals(4, spawner.getSpawnCount());
    List<RobotType> order = drainOrder(spawner);
    // Expected deterministic alternation start: fast, tanky, fast, tanky
    assertEquals(List.of(RobotType.FAST, RobotType.TANKY, RobotType.FAST, RobotType.TANKY), order);
  }

  @Test
  void zeroCostTypesAreSkippedToAvoidInfiniteLoops() {
    // A zero-cost type must be ignored; only the valid-cost type should appear.
    WaveService ws =
        new WaveService() {
          @Override
          public int getWaveWeight() {
            return 6;
          } // cost 2 -> 3 spawns

          @Override
          public int getMinZombiesSpawn() {
            return 0;
          }

          @Override
          public Map<String, BaseSpawnConfig> getEnemyConfigs() {
            Map<String, BaseSpawnConfig> m = new HashMap<>();
            m.put("fast", mockCfg(0, 1.0f)); // invalid, should be skipped
            m.put("tanky", mockCfg(2, 1.0f));
            return m;
          }
        };

    EntitySpawn spawner = new EntitySpawn(ws, 2);
    spawner.spawnEnemiesFromConfig();

    assertEquals(3, spawner.getSpawnCount());
    Map<RobotType, Integer> counts = drainCounts(spawner);
    // Fast is free, so should be skipped
    assertEquals(3, counts.getOrDefault(RobotType.TANKY, 0));
    assertEquals(0, counts.getOrDefault(RobotType.FAST, 0));
  }

  // --- helpers ---

  private static BaseSpawnConfig mockCfg(int cost, float chance) {
    BaseSpawnConfig cfg = mock(BaseSpawnConfig.class);
    when(cfg.getCost()).thenReturn(cost);
    when(cfg.getChance()).thenReturn(chance);
    return cfg;
  }

  private static Map<RobotType, Integer> drainCounts(EntitySpawn spawner) {
    Map<RobotType, Integer> counts = new HashMap<>();
    int n = spawner.getSpawnCount();
    for (int i = 0; i < n; i++) {
      RobotType type = spawner.getNextRobotType();
      counts.merge(type, 1, Integer::sum);
    }
    return counts;
  }

  private static List<RobotType> drainOrder(EntitySpawn spawner) {
    List<RobotType> out = new ArrayList<>();
    int n = spawner.getSpawnCount();
    for (int i = 0; i < n; i++) {
      out.add(spawner.getNextRobotType());
    }
    return out;
  }

  // Removed random type test: spawning is now fully budget-driven via configs.
}
