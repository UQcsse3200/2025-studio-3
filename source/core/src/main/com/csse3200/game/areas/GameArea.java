package com.csse3200.game.areas;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Disposable;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.factories.RobotFactory;
import com.csse3200.game.entities.factories.RobotFactory.RobotType;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an area in the game, such as a level, indoor area, etc. An area has a terrain and
 * other entities to spawn on that terrain.
 *
 * <p>Support for enabling/disabling game areas could be added by making this a Component instead.
 */
public abstract class GameArea implements Disposable {
  protected List<Entity> areaEntities;

  protected GameArea() {
    areaEntities = new ArrayList<>();
  }

  /** Create the game area in the world. */
  public abstract void create();

  /**
   * Dispose of all internal entities in the area. We have had issues where disposing during physics
   * steps caused crashes. However, this will presumably only be called to dispose the entire area,
   * so no more physics steps should be caused.
   */
  public void dispose() {
    for (Entity entity : areaEntities) {
      entity.dispose();
    }
  }

  /**
   * Spawn entity at its current position
   *
   * @param entity Entity (not yet registered)
   */
  protected void spawnEntity(Entity entity) {
    areaEntities.add(entity);
    ServiceLocator.getEntityService().register(entity);
  }

  protected void despawnEntity(Entity entity) {
    if (entity == null) {
      return;
    }
    ServiceLocator.getEntityService().unregister(entity);
    entity.dispose();
    areaEntities.remove(entity);
  }

  public void requestDespawn(Entity entity) {
    if (entity == null) return;
    entity.setDeathFlag();
  }

  /**
   * Gets all entities in this area.
   *
   * @return list of entities in this area
   */
  public List<Entity> getEntities() {
    return areaEntities;
  }

  // If no robot type given, spawns a standard robot
  public Entity spawnRobotAtTile(GridPoint2 cell, boolean centerX, boolean centerY) {
    return spawnRobotAtTile(cell, centerX, centerY, "standard");
  }

  // uses LevelGameArea (Level One) grid params
  public Entity spawnRobotAtTile(
      GridPoint2 cell, boolean centerX, boolean centerY, String robotType) {
    // grid params copied from LevelGameArea (Level One)
    final float xOffset = 2.9f;
    final float yOffset = 1.45f;
    final int rows = 5; // levelOneRows
    final int cols = 10; // levelOneCols
    final float gridHeight = 7f;
    final float cellScale = gridHeight / rows; // same as LevelGameArea

    // Bounds check on overlay coordinates
    if (cell.x < 0 || cell.y < 0 || cell.x >= cols || cell.y >= rows) {
      throw new IllegalArgumentException(
          "Overlay cell out of bounds: " + cell + " within (" + rows + "x" + cols + ")");
    }

    Entity robot = RobotFactory.createRobotType(RobotType.valueOf(robotType.toUpperCase()));

    // Register first so getCenterPosition() is valid
    spawnEntity(robot);

    float tileX = xOffset + cellScale * cell.x;
    float tileY = yOffset + cellScale * cell.y;

    float worldX = tileX;
    float worldY = tileY;

    if (centerX) {
      worldX += (cellScale / 2f) - robot.getCenterPosition().x;
    }
    if (centerY) {
      worldY += (cellScale / 2f) - robot.getCenterPosition().y;
    }

    robot.setPosition(worldX, worldY);
    return robot;
  }

  // Spawns a standard robot if no type is specified
  public Entity spawnRobotAtFloat(float x, float y) {
    return spawnRobotAtFloat(x, y, "standard");
  }

  public Entity spawnRobotAtFloat(float x, float y, String robotType) {
    Entity robot = RobotFactory.createRobotType(RobotType.valueOf(robotType.toUpperCase()));
    robot.setPosition(x, y);
    spawnEntity(robot);
    return robot;
  }
}
