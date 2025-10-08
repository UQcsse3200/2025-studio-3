package com.csse3200.game.entities;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.csse3200.game.entities.configs.BaseLevelConfig;
import com.csse3200.game.entities.configs.BaseSpawnConfig;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.WaveService;
import java.util.HashMap;
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

  // Removed random type test: spawning is now fully budget-driven via configs.
}
