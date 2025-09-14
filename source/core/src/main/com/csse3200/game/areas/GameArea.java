package com.csse3200.game.areas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.csse3200.game.areas.terrain.TerrainComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.WaveManager;
import com.csse3200.game.entities.factories.RobotFactory;
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
  protected TerrainComponent terrain;
  protected List<Entity> areaEntities;
  protected WaveManager waveManager;

  protected GameArea() {
    areaEntities = new ArrayList<>();
  }

  /** Create the game area in the world. */
  public abstract void create();

  /** Dispose of all internal entities in the area */
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
    
    // Notify WaveManager of entity disposal (for wave tracking)
    if (waveManager != null) {
      waveManager.onEnemyDisposed();
    }
  }

  /**
   * Spawn entity on a given tile. Requires the terrain to be set first.
   *
   * @param entity Entity (not yet registered)
   * @param tilePos tile position to spawn at
   * @param centerX true to center entity X on the tile, false to align the bottom left corner
   * @param centerY true to center entity Y on the tile, false to align the bottom left corner
   */
  protected void spawnEntityAt(
      Entity entity, GridPoint2 tilePos, boolean centerX, boolean centerY) {
    Vector2 worldPos = terrain.tileToWorldPosition(tilePos);
    float tileSize = terrain.getTileSize();

    if (centerX) {
      worldPos.x += (tileSize / 2) - entity.getCenterPosition().x;
    }
    if (centerY) {
      worldPos.y += (tileSize / 2) - entity.getCenterPosition().y;
    }

    entity.setPosition(worldPos);
    spawnEntity(entity);
  }

  public void requestDespawn(Entity entity) {
    if (entity == null) return;
    Gdx.app.postRunnable(() -> despawnEntity(entity));
  }

  //    public Entity spawnRobotAtTile(GridPoint2 tilePos, boolean centerX, boolean centerY) {
  //        BaseEntityConfig cfg = new BaseEntityConfig();
  //        cfg.health = 10;
  //        cfg.baseAttack = 2;
  //
  //        Entity robot = RobotFactory.createRobot(cfg);
  //        spawnEntityAt(robot, tilePos, centerX, centerY);
  //        return robot;
  //    }

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

    Entity robot = RobotFactory.createRobotType(robotType);

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

    Entity robot = RobotFactory.createRobotType(robotType);
    robot.setPosition(x, y);
    spawnEntity(robot);
    return robot;
  }

  /**
   * Sets the WaveManager reference for disposal tracking
   * @param waveManager the WaveManager instance
   */
  public void setWaveManager(WaveManager waveManager) {
    this.waveManager = waveManager;
  }
}
