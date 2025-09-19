package com.csse3200.game.components.dossier;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.csse3200.game.entities.configs.BaseDefenderConfig;
import com.csse3200.game.entities.configs.BaseEnemyConfig;
import com.csse3200.game.entities.configs.BaseGeneratorConfig;
import com.csse3200.game.services.ServiceLocator;
import java.util.Map;

/**
 * DossierManager is a class that manages the dossier of the game.
 */
public class DossierManager {
  private static final String HEALTH_LABEL = "\nHealth: ";
  private static final String ATTACK_LABEL = "\nAttack: ";
  private static final String COST_LABEL = "\nCost: ";
  private final Map<String, BaseEnemyConfig> enemyConfigs;
  private final Map<String, BaseDefenderConfig> defenderConfigs;
  private final Map<String, BaseGeneratorConfig> generatorConfigs;
  private boolean enemyMode = true;

  /**
   * Constructor for the DossierManager class.
   * @param enemyConfigs the enemy configs
   * @param defenderConfigs the defender configs
   * @param generatorConfigs the generator configs
   */
  public DossierManager(Map<String, BaseEnemyConfig> enemyConfigs, 
                       Map<String, BaseDefenderConfig> defenderConfigs,
                       Map<String, BaseGeneratorConfig> generatorConfigs) {
    this.enemyConfigs = enemyConfigs;
    this.defenderConfigs = defenderConfigs;
    this.generatorConfigs = generatorConfigs;
  }

  /**
   * Changes the mode of the dossier.
   */
  public void changeMode() {
    enemyMode = !enemyMode;
  }

  /**
   * Gets the name of the entity.
   * 
   * @param entityName the name of the entity
   * @return the name of the entity
   */
  public String getName(String entityName) {
    if (enemyMode) {
      BaseEnemyConfig config = enemyConfigs.get(entityName);
      return config != null ? config.getName() : "Unknown Enemy";
    } else {
      // Check defenders first, then generators
      BaseDefenderConfig defenderConfig = defenderConfigs.get(entityName);
      if (defenderConfig != null) {
        return defenderConfig.getName();
      }
      BaseGeneratorConfig generatorConfig = generatorConfigs.get(entityName);
      return generatorConfig != null ? generatorConfig.getName() : "Unknown Entity";
    }
  }

  /**
   * Gets the sprite of the entity.
   * 
   * @param entityName the name of the entity
   * @return the sprite of the entity
   */
  public Image getSprite(String entityName) {
    if (enemyMode) {
      BaseEnemyConfig config = enemyConfigs.get(entityName);
      if (config != null && config.getAssetPath() != null) {
        Texture texture = ServiceLocator.getResourceService().getAsset(config.getAssetPath(), Texture.class);
        return new Image(texture);
      }
      // Fallback to placeholder if no asset
      return new Image(ServiceLocator.getResourceService().getAsset("images/entities/placeholder.png", Texture.class));
    } else {
      // Check defenders first, then generators
      BaseDefenderConfig defenderConfig = defenderConfigs.get(entityName);
      if (defenderConfig != null && defenderConfig.getAssetPath() != null) {
        Texture texture = ServiceLocator.getResourceService().getAsset(defenderConfig.getAssetPath(), Texture.class);
        return new Image(texture);
      }
      
      BaseGeneratorConfig generatorConfig = generatorConfigs.get(entityName);
      if (generatorConfig != null && generatorConfig.getAssetPath() != null) {
        Texture texture = ServiceLocator.getResourceService().getAsset(generatorConfig.getAssetPath(), Texture.class);
        return new Image(texture);
      }
      
      // Fallback to placeholder if no asset
      return new Image(ServiceLocator.getResourceService().getAsset("images/entities/placeholder.png", Texture.class));
    }
  }

  /**
   * Gets the info of the entity.
   * 
   * @param entityName the name of the entity
   * @return the info of the entity
   */
  public String getInfo(String entityName) {
    if (enemyMode) {
      BaseEnemyConfig config = enemyConfigs.get(entityName);
      if (config != null) {
        return config.getDescription()
            + ATTACK_LABEL + config.getAttack()
            + HEALTH_LABEL + config.getHealth()
            + "\nMovement Speed: " + config.getMovementSpeed();
      }
      return "No information available";
    } else {
      // Check defenders first, then generators
      BaseDefenderConfig defenderConfig = defenderConfigs.get(entityName);
      if (defenderConfig != null) {
        return defenderConfig.getDescription()
            + ATTACK_LABEL + defenderConfig.getAttack()
            + HEALTH_LABEL + defenderConfig.getHealth()
            + COST_LABEL + defenderConfig.getCost()
            + "\nRange: " + defenderConfig.getRange();
      }
      
      BaseGeneratorConfig generatorConfig = generatorConfigs.get(entityName);
      if (generatorConfig != null) {
        return generatorConfig.getDescription()
            + HEALTH_LABEL + generatorConfig.getHealth()
            + COST_LABEL + generatorConfig.getCost()
            + "\nScrap Value: " + generatorConfig.getScrapValue()
            + "\nInterval: " + generatorConfig.getInterval() + "s";
      }
      
      return "No information available";
    }
  }
}
