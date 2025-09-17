package com.csse3200.game.components.dossier;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.entities.configs.BaseDefenceConfig;

public class DossierManager {

  private final EntityConfigs entityData;
  private final Texture defaultTexture;
  private final Texture blueTexture;
  private final Texture redTexture;
  private final DefenceConfigs defenceData;
  private final Texture humanTexture;

  private boolean enemyMode = true;

  public DossierManager(
      EntityConfigs entityConfigs, DefenceConfigs defenceConfigs, Texture[] textures) {
    this.defaultTexture = textures[0];
    this.redTexture = textures[1];
    this.blueTexture = textures[2];
    this.humanTexture = textures[3];
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
      return switch (entityName) {
        case "standardRobot" -> new Image(defaultTexture);
        case "fastRobot" -> new Image(blueTexture);
        case "tankyRobot" -> new Image(redTexture);
        case "bungeeRobot" -> new Image(blueTexture);
        case "teleportRobot" -> new Image(redTexture);
        default -> new Image(defaultTexture);
      };
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
      BaseDefenceConfig config = getDefence(entityName);
      return " "
          + config.description
          + "\n Attack: "
          + config.defenceAttack
          + "\n Health: "
          + config.defenceHealth;
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

  private BaseDefenceConfig getDefence(String entityName) {
    return switch (entityName) {
      case "slingshooter" -> defenceData.slingshooter;
      // Currently this is bad style, but more defences will be added later
      default -> defenceData.slingshooter;
    };
  }

  public void dispose() {
    defaultTexture.dispose();
  }
}
