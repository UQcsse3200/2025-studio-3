package com.csse3200.game.areas.terrain;

import com.csse3200.game.entities.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MapFactory simplifies creating maps as single-image entities. Internally, it uses TerrainFactory
 * and TerrainComponent to stay compatible with the rendering system. Supports multiple levels.
 */
public class MapFactory {
  private static final Logger logger = LoggerFactory.getLogger(MapFactory.class);

  private final TerrainFactory terrainFactory;

  public MapFactory(TerrainFactory terrainFactory) {
    this.terrainFactory = terrainFactory;
  }

  /**
   * Creates a map entity for the specified level.
   *
   * @param level the game level (1, 2, etc.)
   * @return Entity containing the map, or null if creation fails
   */
  public Entity createLevelMap(int level) {
    TerrainFactory.TerrainType terrainType;

    switch (level) {
      case 1:
        terrainType = TerrainFactory.TerrainType.LEVEL_ONE_MAP;
        break;
      case 2:
        terrainType = TerrainFactory.TerrainType.LEVEL_TWO_MAP;
        break;
      default:
        logger.error("Unsupported level: {}", level);
        return null;
    }

    TerrainComponent terrain = terrainFactory.createTerrain(terrainType);
    if (terrain == null) {
      logger.error("Failed to create map terrain for level {}", level);
      return null;
    }

    Entity mapEntity = new Entity().addComponent(terrain);
    logger.debug("Map entity for level {} created successfully", level);
    return mapEntity;
  }
}
