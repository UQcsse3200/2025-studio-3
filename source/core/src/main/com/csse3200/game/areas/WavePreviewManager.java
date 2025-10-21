package com.csse3200.game.areas;

import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntitySpawn;
import com.csse3200.game.entities.factories.BossFactory;
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
    Map<Integer, List<RobotFactory.RobotType>> plan = spawner.previewAllWaves();

    for (Map.Entry<Integer, List<RobotFactory.RobotType>> entry : plan.entrySet()) {
      int wave = entry.getKey();
      for (RobotFactory.RobotType type : entry.getValue()) {
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
    String currentLevel = area.getCurrentLevelKey();
    BossFactory.BossTypes bossToPreview = null;
    if (currentLevel != null) {
      switch (currentLevel) {
        case "levelTwo":
          bossToPreview = BossFactory.BossTypes.SCRAP_TITAN;
          break;
        case "levelFour":
          bossToPreview = BossFactory.BossTypes.SAMURAI_BOT;
          break;
        case "levelFive":
          bossToPreview = BossFactory.BossTypes.GUN_BOT;
          break;
      }
    }
    if (bossToPreview != null) {
      int waveForPositioning = 1;
      float x = xOffset + (cols * tileSize) + (waveForPositioning * 1.15f * tileSize);

      int bossRow = 0;
      float y = yOffset + bossRow * tileSize;

      Entity preview = BossFactory.createPreviewBoss(bossToPreview);
      preview.setPosition(x, y);
      preview.scaleHeight(tileSize * 3.0f);
      area.spawnEntity(preview);
      previewEntities.add(preview);
    }
  }

  /** Removes all preview entities created earlier. */
  public void clearWavePreview() {
    if (!active) return;
    for (Entity e : new ArrayList<>(previewEntities)) {
      area.removePreviewEntity(e);
    }
    previewEntities.clear();
    active = false;
  }

  public boolean isActive() {
    return active;
  }
}
