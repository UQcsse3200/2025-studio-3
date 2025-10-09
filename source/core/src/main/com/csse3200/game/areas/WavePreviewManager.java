package com.csse3200.game.areas;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntitySpawn;
import com.csse3200.game.entities.factories.RobotFactory;
import com.csse3200.game.services.ServiceLocator;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Handles creation and removal of symbolic robots shown before a wave begins. These entities are
 * visual only and not part of gameplay.
 */
public class WavePreviewManager {
  private final LevelGameArea area;
  private final List<Entity> previewEntities = new ArrayList<>();
  private boolean active = false;

  public WavePreviewManager(LevelGameArea area) {
    this.area = area;
  }

  /** Creates placeholder robots for upcoming waves. */
  public void createWavePreview() {
    if (active) return;
    active = true;

    int rows = area.getLevelRows();
    int cols = area.getLevelCols();
    float tileSize = area.getTileSize();
    float xOffset = area.getXOffset();
    float yOffset = area.getYOffset();

    EntitySpawn spawner = new EntitySpawn();
    spawner.setWaveConfigProvider(ServiceLocator.getWaveService());
    Map<Integer, List<String>> plan = spawner.previewAllWaves();

    for (Map.Entry<Integer, List<String>> entry : plan.entrySet()) {
      int wave = entry.getKey();
      for (String type : entry.getValue()) {
        float xSpread = ThreadLocalRandom.current().nextFloat(tileSize);
        float x = xOffset + (cols * tileSize) + (wave * 1.15f * tileSize) + xSpread;
        int row = ThreadLocalRandom.current().nextInt(rows);
        float y = yOffset + row * tileSize;

        Entity preview = RobotFactory.createPreviewRobot(type);
        preview.setPosition(x, y);
        preview.scaleHeight(tileSize);
        area.spawnEntity(preview);
        previewEntities.add(preview);
      }
    }
  }

  /** Removes all preview entities created earlier. */
  public void clearWavePreview() {
    if (!active) return;
    for (Entity e : new ArrayList<>(previewEntities)) {
      area.requestDespawn(e);
    }
    previewEntities.clear();
    active = false;
  }

  public boolean isActive() {
    return active;
  }
}
