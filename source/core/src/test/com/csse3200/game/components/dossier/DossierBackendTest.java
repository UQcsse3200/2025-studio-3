package com.csse3200.game.components.dossier;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import com.badlogic.gdx.graphics.Texture;
import com.csse3200.game.entities.configs.BaseDefenceConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DossierBackendTest {

  private Texture mockDefaultTexture;
  private Texture mockHumanTexture;

  private DossierManager dossierManager;

  @BeforeEach
  void setUp() {
    // Mock data
    EntityConfigs entityConfigs = new EntityConfigs();
    entityConfigs.standardRobot = new EntityDataConfig();
    entityConfigs.standardRobot.name = "Standard Robot";
    entityConfigs.standardRobot.description = "A standard enemy.";
    entityConfigs.standardRobot.health = 40;
    entityConfigs.standardRobot.attack = 10;

    DefenceConfigs defenceConfigs = new DefenceConfigs();
    defenceConfigs.slingshooter = new BaseDefenceConfig();
    defenceConfigs.slingshooter.name = "Slingshooter";
    defenceConfigs.slingshooter.description = "A basic defense.";
    defenceConfigs.slingshooter.defenceHealth = 50;
    defenceConfigs.slingshooter.defenceAttack = 1;

    mockDefaultTexture = mock(Texture.class);
    mockHumanTexture = mock(Texture.class);
    Texture[] textures = {mockDefaultTexture, mockHumanTexture};

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
