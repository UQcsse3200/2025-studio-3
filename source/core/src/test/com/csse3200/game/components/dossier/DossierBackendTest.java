package com.csse3200.game.components.dossier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.badlogic.gdx.graphics.Texture;
import com.csse3200.game.entities.configs.BaseDefenderConfig;
import com.csse3200.game.entities.configs.BaseEnemyConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DossierBackendTest {

  private Texture mockDefaultTexture;
  private Texture mockHumanTexture;
  private Texture mockRedTexture;
  private Texture mockBlueTexture;

  private DossierManager dossierManager;

  @BeforeEach
  void setUp() {
    // Mock data
    NPCConfigs entityConfigs = new NPCConfigs();
    entityConfigs.standardRobot = new BaseEnemyConfig();
    entityConfigs.standardRobot.name = "Standard Robot";
    entityConfigs.standardRobot.description = "A standard enemy.";
    entityConfigs.standardRobot.health = 40;
    entityConfigs.standardRobot.attack = 10;

    NPCConfigs defenceConfigs = new NPCConfigs();
    defenceConfigs.slingshooter = new BaseDefenderConfig();
    defenceConfigs.slingshooter.name = "Slingshooter";
    defenceConfigs.slingshooter.description = "A basic defense.";
    defenceConfigs.slingshooter.defenceHealth = 50;
    defenceConfigs.slingshooter.defenceAttack = 1;

    mockDefaultTexture = mock(Texture.class);
    mockRedTexture = mock(Texture.class);
    mockBlueTexture = mock(Texture.class);
    mockHumanTexture = mock(Texture.class);
    Texture[] textures = {mockDefaultTexture, mockRedTexture, mockBlueTexture, mockHumanTexture};

    dossierManager = new DossierManager(entityConfigs, defenceConfigs, textures);
  }

  @Test
  void getNameTest() {
    assertEquals("Standard Robot", dossierManager.getName("standardRobot"));
    dossierManager.changeMode();
    assertEquals("Slingshooter", dossierManager.getName("slingshooter"));
    // test fallback behaviour
    assertEquals("Slingshooter", dossierManager.getName("standardRobot"));
  }

  @Test
  void getInfoInEnemyMode() {
    String expectedInfo = " A standard enemy.\n Attack: 10\n Health: 40";
    assertEquals(expectedInfo, dossierManager.getInfo("standardRobot"));
  }

  @Test
  void getInfoInDefenceMode() {
    dossierManager.changeMode();
    dossierManager.changeMode();
    dossierManager.changeMode();
    String expectedInfo = " A basic defense.\n Attack: 1\n Health: 50";
    assertEquals(expectedInfo, dossierManager.getInfo("slingshooter"));
  }

  @Test
  void changeModeShouldToggleEnemyMode() {
    dossierManager.changeMode();
    assertEquals("Slingshooter", dossierManager.getName("slingshooter"));
    dossierManager.changeMode();
    assertEquals("Standard Robot", dossierManager.getName("standardRobot"));
  }

  @Test
  void disposeShouldNotThrowException() {
    assertDoesNotThrow(() -> dossierManager.dispose());
  }
}
