package com.csse3200.game.components.dossier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.csse3200.game.GdxGame;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.entities.configs.BaseDefenderConfig;
import com.csse3200.game.entities.configs.BaseEnemyConfig;
import com.csse3200.game.entities.configs.BaseGeneratorConfig;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class DossierBackendTest {
  private DossierDisplay dossierDisplay;
  private GdxGame mockGame;

  @BeforeEach
  void setUp() {
    // Create mock config maps
    Map<String, BaseEnemyConfig> enemyConfigs = new HashMap<>();
    Map<String, BaseDefenderConfig> defenderConfigs = new HashMap<>();
    Map<String, BaseGeneratorConfig> generatorConfigs = new HashMap<>();

    // Create mock configs - since config fields are private, we'll use mocks
    BaseEnemyConfig enemyConfig = mock(BaseEnemyConfig.class);
    when(enemyConfig.getName()).thenReturn("Standard Robot");
    when(enemyConfig.getDescription()).thenReturn("A standard enemy.");
    when(enemyConfig.getHealth()).thenReturn(40);
    when(enemyConfig.getAttack()).thenReturn(10);
    enemyConfigs.put("standardRobot", enemyConfig);

    BaseDefenderConfig defenderConfig = mock(BaseDefenderConfig.class);
    when(defenderConfig.getName()).thenReturn("Slingshooter");
    when(defenderConfig.getDescription()).thenReturn("A basic defense.");
    when(defenderConfig.getHealth()).thenReturn(50);
    when(defenderConfig.getAttack()).thenReturn(1);
    defenderConfigs.put("slingshooter", defenderConfig);

    mockGame = mock(GdxGame.class);
    dossierDisplay = new DossierDisplay(mockGame, enemyConfigs, defenderConfigs, generatorConfigs);
  }

  @Test
  void dossierDisplayShouldInitializeWithConfigs() {
    // Test that DossierDisplay can be created with mock configs
    assertDoesNotThrow(() -> new DossierDisplay(mockGame, 
        new HashMap<>(), new HashMap<>(), new HashMap<>()));
  }

  @Test
  void dossierDisplayShouldInitializeWithNullConfigs() {
    // Test that DossierDisplay handles null configs gracefully
    assertDoesNotThrow(() -> new DossierDisplay(mockGame, null, null, null));
  }

  @Test
  void disposeShouldNotThrowException() {
    assertDoesNotThrow(() -> dossierDisplay.dispose());
  }
}
