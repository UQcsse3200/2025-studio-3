package com.csse3200.game.entities.configs;

/**
 * Defines a basic set of properties stored in entities config files to be loaded by Entity
 * Factories.
 */
public class BaseEntityConfig {
  private String name;
  private String description;
  private int health;
  private String assetPath;
  private String atlasPath;

  /** Creates a new BaseEntityConfig with default values. */
  public BaseEntityConfig() {
    // Default constructor with default field values
  }

  /**
   * Gets the name of the entity.
   *
   * @return the name of the entity
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the description of the entity.
   *
   * @return the description of the entity
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the health of the entity.
   *
   * @return the health of the entity
   */
  public int getHealth() {
    return health;
  }

  /**
   * Gets the asset path of the entity.
   *
   * @return the asset path of the entity
   */
  public String getAssetPath() {  
    return assetPath;
  }

  /**
   * Gets the atlas path of the entity.
   *
   * @return the atlas path of the entity
   */
  public String getAtlasPath() {
    return atlasPath;
  }
}
