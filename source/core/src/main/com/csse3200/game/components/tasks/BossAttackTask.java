package com.csse3200.game.components.tasks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import java.util.List;

public class BossAttackTask extends TargetDetectionTasks {
  private static final float FIRE_COOLDOWN = 0.95f; // seconds between shots (tweak as needed)
  private float timeSinceLastFire = 0f;

  public BossAttackTask(float attackRange) {
    super(attackRange, AttackDirection.LEFT);
  }

  @Override
  public void start() {
    super.start();
    this.owner.getEntity().getEvents().trigger("attackStart");
    owner.getEntity().getEvents().trigger("fire");
  }

  @Override
  protected Entity getNearestVisibleTarget() {
    Vector2 from = owner.getEntity().getCenterPosition();
    Entity closestTarget = null;
    float closestDist = Float.MAX_VALUE;
    List<Entity> targets = getAllTargets();

    for (Entity target : targets) {
      Vector2 targetPos = target.getCenterPosition();

      if (targetPos.x >= from.x) {
        continue;
      }

      boolean sameLane = isTargetInSameLane(target);

      if (!sameLane) {
        continue;
      }

      float distance = from.dst(targetPos);

      if (distance <= attackRange && distance < closestDist) {
        closestDist = distance;
        closestTarget = target;
      }
    }

    return closestTarget;
  }

  @Override
  public void update() {
    Entity target = getNearestVisibleTarget();

    if (target == null) {
      return;
    }

    if (!isTargetInSameLane(target)) {
      return;
    }

    if (getDistanceToTarget() <= attackRange) {
      timeSinceLastFire += ServiceLocator.getTimeSource().getDeltaTime();

      if (timeSinceLastFire >= FIRE_COOLDOWN) {
        owner.getEntity().getEvents().trigger("fire");
        timeSinceLastFire = 0f;
      }
    }
  }

  @Override
  protected int getActivePriority(float dst, Entity target) {
    if (target == null) {
      return -1; // stop task if no target
    }
    if (dst > attackRange || !isTargetInSameLane(target)) {
      return -1; // stop task when target not visible, out of range, or not in the same lane
    }
    return 1;
  }

  @Override
  protected List<Entity> getAllTargets() {
    Array<Entity> allEntities = ServiceLocator.getEntityService().getEntities();
    Array<Entity> copy = new Array<>(allEntities);
    List<Entity> targets = new ArrayList<>();

    for (Entity e : copy) {
      HitboxComponent hitbox = e.getComponent(HitboxComponent.class);

      if (hitbox != null && hitbox.getLayer() == PhysicsLayer.NPC) {
        targets.add(e);
      }
    }
    return targets;
  }

  protected int getInactivePriority(float dst, Entity target) {
    if (target == null) {
      return -1;
    }
    if (dst <= attackRange && isTargetInSameLane(target)) {
      return 1; // start task if target is visible, in range, and in the same lane
    }
    return -1;
  }

  private boolean isTargetInSameLane(Entity target) {
    // This call will now work correctly because we registered the GameArea in LevelGameArea.
    if (ServiceLocator.getGameArea() instanceof LevelGameArea) {
      LevelGameArea area = (LevelGameArea) ServiceLocator.getGameArea();
      float tileSize = area.getTileSize();

      // Get the vertical center position of the owner (slingshooter) and the target
      float ownerY = owner.getEntity().getCenterPosition().y;
      float targetY = target.getCenterPosition().y;

      // Check if the absolute vertical distance between the entities' centers
      // is less than half a tile. This confirms they are on the same row.
      return Math.abs(ownerY - targetY) < (tileSize / 2);
    }
    // If we're not in a LevelGameArea, we can't determine lanes, so fallback to false.
    return false;
  }
}
