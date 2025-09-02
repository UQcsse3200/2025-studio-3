package com.csse3200.game.areas;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.csse3200.game.areas.terrain.TerrainComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.entities.configs.BaseEntityConfig;
import com.csse3200.game.entities.factories.RobotFactory;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Gdx;

/**
 * Represents an area in the game, such as a level, indoor area, etc. An area has a terrain and
 * other entities to spawn on that terrain.
 *
 * <p>Support for enabling/disabling game areas could be added by making this a Component instead.
 */
public abstract class GameArea implements Disposable {
  protected TerrainComponent terrain;
  protected List<Entity> areaEntities;

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
      if(entity == null) {
          return;
      }
      ServiceLocator.getEntityService().unregister(entity);
      entity.dispose();
      areaEntities.remove(entity);
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

    public Entity spawnRobotAtFloat(float x, float y) {
        BaseEntityConfig cfg = new BaseEntityConfig();
        cfg.health = 10;
        cfg.baseAttack = 2;

        Entity robot = RobotFactory.createRobot(cfg);
        spawnEntity(robot);
        robot.setPosition(x, y);
        return robot;
    }

}
