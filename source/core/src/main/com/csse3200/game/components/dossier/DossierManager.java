package com.csse3200.game.components.dossier;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.persistence.FileLoader;
import com.csse3200.game.services.ResourceService;

public class DossierManager {

  private final EntityConfigs entityData;
  private final Texture defaultTexture;
  private final DefenceConfigs defenceData;
  private final Texture humanTexture;

  private boolean enemyMode = true;

  public DossierManager(EntityConfigs entityConfigs, DefenceConfigs defenceConfigs, Texture[] textures) {
    this.defaultTexture = textures[0];
    this.humanTexture = textures[1];
    this.entityData = entityConfigs;
    this.defenceData = defenceConfigs;
  }

  public void changeMode() {
    enemyMode = !enemyMode;
  }

  public String getName(String entityName) {
    if (enemyMode) {
      return getEnemy(entityName).name;
    }
    return getDefence(entityName).name;
  }

  public Image getSprite(String entityName) {
    if (enemyMode) {
      return new Image(defaultTexture);
    }
    return new Image(humanTexture);
  }

  /** Retrieves info directly from the loaded config object. */
  public String getInfo(String entityName) {
    if (enemyMode) {
      EntityDataConfig config = getEnemy(entityName);
      return " "
          + config.description
          + "\n Attack: "
          + config.attack
          + "\n Health: "
          + config.health;
    } else {
      DefenceDataConfig config = getDefence(entityName);
      return " "
          + config.description
          + "\n Attack: "
          + config.baseAttack
          + "\n Health: "
          + config.health;
    }
  }

  /**
   * Retrieves the specific enemy data object based on its name. Expanded to include all enemy
   * types.
   */
  private EntityDataConfig getEnemy(String entityName) {
    return switch (entityName) {
      case "standardRobot" -> entityData.standardRobot;
      case "fastRobot" -> entityData.fastRobot;
      case "tankyRobot" -> entityData.tankyRobot;
      case "bungeeRobot" -> entityData.bungeeRobot;
      default -> entityData.standardRobot;
    };
  }

  private DefenceDataConfig getDefence(String entityName) {
    return switch (entityName) {
      case "slingshooter" -> defenceData.slingshooter;
      default -> defenceData.slingshooter;
    };
  }

  public void dispose() {
    defaultTexture.dispose();
  }
}
