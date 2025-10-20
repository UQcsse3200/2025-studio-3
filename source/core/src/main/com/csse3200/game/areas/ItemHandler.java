package com.csse3200.game.areas;

import static com.csse3200.game.services.ItemEffectsService.spawnEffect;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.csse3200.game.components.DefenderStatsComponent;
import com.csse3200.game.components.GeneratorStatsComponent;
import com.csse3200.game.components.items.ItemComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseItemConfig;
import com.csse3200.game.progression.inventory.Inventory;
import com.csse3200.game.services.ServiceLocator;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles all item-related game logic for LevelGameArea: - inventory consumption - damage radius
 * effects - temporary buffs/debuffs
 */
public class ItemHandler {
  private static final Logger logger = LoggerFactory.getLogger(ItemHandler.class);

  private final LevelGameArea area;

  public ItemHandler(LevelGameArea area) {
    this.area = area;
  }

  /** Main entry point for spawning and resolving an item's effects. */
  public void handleItemUse(ItemComponent item, Vector2 entityPos) {
    String key = item.getType().toString().toLowerCase(Locale.ROOT);
    Inventory inv = ServiceLocator.getProfileService().getProfile().getInventory();
    inv.removeItem(key);
    logger.info("One {} item used", key);

    // play UI/FX
    ServiceLocator.getItemEffectsService()
        .playEffect(
            key,
            entityPos,
            (int) area.getTileSize(),
            new Vector2(
                (float) (area.getXOffset() * 0.25 + area.getLevelCols() * area.getTileSize()),
                (float) (area.getTileSize() * -0.75)));

    if (isDamagingItem(item)) {
      applyAreaDamage(entityPos);
    } else {
      applyBuff(item);
    }
  }

  private boolean isDamagingItem(ItemComponent item) {
    Set<String> damaging = Set.of("GRENADE", "EMP", "NUKE", "DOOMHACK");
    return damaging.contains(item.getType().toString());
  }

  /** Damages all robots in a small radius around entityPos. */
  private void applyAreaDamage(Vector2 entityPos) {
    float radius = 2.5f * area.getTileSize();
    List<Entity> toRemove =
        area.getRobots().stream()
            .filter(
                r -> {
                  Vector2 p = r.getPosition();
                  return Math.abs(entityPos.x - p.x) <= radius
                      && Math.abs(entityPos.y - p.y) <= radius;
                })
            .collect(Collectors.toList());

    for (Entity r : toRemove) {
      area.requestDespawn(r);
      area.getRobots().remove(r);
    }
    logger.info("Area damage applied to {} robots", toRemove.size());
  }

  /** Applies temporary buffs (coffee, buff, etc.) to all placed defences. */
  private void applyBuff(ItemComponent item) {
    String key = item.getType().toString().toLowerCase(Locale.ROOT);
    BaseItemConfig config = ServiceLocator.getConfigService().getItemConfig(key);
    if (config == null) return;
    String trigger = config.getTrigger();

    int total = area.getGrid().getRows() * area.getGrid().getCols();
    for (int i = 0; i < total; i++) {
      Entity entity = area.getGrid().getOccupantIndex(i);
      if (entity == null) continue;

      if (entity.getComponent(DefenderStatsComponent.class) != null
          || entity.getComponent(GeneratorStatsComponent.class) != null) {

        entity.getEvents().trigger(trigger);
        addAnimationOntoDefence(trigger, entity);
        logger.info("Start {} on {}", trigger, entity);

        Timer.schedule(
            new Timer.Task() {
              @Override
              public void run() {
                entity.getEvents().trigger(trigger + "Stop");
                logger.info("Stop {}", trigger);
              }
            },
            30f);
      }
    }
  }

  /**
   * Adds the visual cue animation onto each defender being buffed
   *
   * @param trigger the buff being activated
   * @param entity the entity to spawn the effect above
   */
  private void addAnimationOntoDefence(String trigger, Entity entity) {
    Vector2 pos = entity.getPosition();

    // spawn trigger visual cue on entity
    String name = "";
    if (trigger.equals("doubleDamage")) {
      name = "attack-up";
    } else if (trigger.equals("doubleFireRate")) {
      name = "speed-up";
    }

    // failsafe
    if (name.equals("")) {
      return;
    }

    // only apply the animations to damage dealing entities
    if (entity.getComponent(DefenderStatsComponent.class) != null) {
      if (entity.getComponent(DefenderStatsComponent.class).getBaseAttack() == 0) {
        return;
      }
    } else if (entity.getComponent(GeneratorStatsComponent.class) != null) {
      return;
    }

    spawnEffect(
        ServiceLocator.getResourceService()
            .getAsset("images/effects/" + name + ".atlas", TextureAtlas.class),
        name,
        (new Vector2[] {pos, pos}),
        (int) area.getTileSize(),
        (new float[] {0.1f, 5f}),
        Animation.PlayMode.NORMAL,
        false,
        false);
  }
}
