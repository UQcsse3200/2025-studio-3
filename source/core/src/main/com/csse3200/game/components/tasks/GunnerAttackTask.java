package com.csse3200.game.components.tasks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.csse3200.game.components.DefenderStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * GunnerAttackTask: Handles the gunner robot attacking tasks
 */
public class GunnerAttackTask extends RobotTargetDetectionTasks {
    private static final Logger logger = LoggerFactory.getLogger(GunnerAttackTask.class);

    private static final int GUNNER_TASK_PRIORITY = 10;
    private static final float FIRE_COOLDOWN = 0.95f;

    private Entity currentTarget;
    private float timeSinceLastFire = 0f;

    /**
     * Constructor for GunnerAttackTask
     * @param attackRange - the attack range for the gunner to shoot
     * @param targetLayer - target type
     */
    public GunnerAttackTask(float attackRange, short targetLayer) {
        super(attackRange, targetLayer);

    }

    @Override
    public void start() {
        super.start();
        logger.info("GunnerAttackTask started for {}", owner.getEntity());
    }

    @Override
    public void update() {
        //find nearest visible defense
        currentTarget = getNearestVisibleTarget();
        if (currentTarget == null) {
            logger.info("No visible defense for {}", owner.getEntity());
            return;
        }
        // check if the target is in range
        Vector2 myPos = owner.getEntity().getPosition();
        Vector2 targetPos = currentTarget.getPosition();
        float distance = myPos.dst(targetPos);
        // if the target is in range, fire
        if (distance <= attackRange) {
            timeSinceLastFire += ServiceLocator.getTimeSource().getDeltaTime();
            // if the time since last fire is greater than the cooldown, fire
            if (timeSinceLastFire >= FIRE_COOLDOWN) {
                logger.info("Gunner firing at {} from {}", targetPos, myPos);
                owner.getEntity().getEvents().trigger("fire");
                timeSinceLastFire = 0f;
            }
        }
    }

    @Override
    public int getPriority() {
        Entity target = getNearestVisibleTarget();
        // check if the target is range to begin task
        if (target != null) {
            return GUNNER_TASK_PRIORITY;
        }
        else {
            return -1;
        }
    }

    /**
     * Get all defense entities in the scene.
     * @return List of all defense entities
     */
    protected List<Entity> getAllTargets() {
        Array<Entity> allEntities = ServiceLocator.getEntityService().getEntities();
        List<Entity> defenses = new ArrayList<>();
        for (Entity entity : allEntities) {
            if (entity.getComponent(DefenderStatsComponent.class) != null) {
                defenses.add(entity);
            }
        }
        logger.info("Found {} possible defense targets", defenses.size());
        return defenses;
    }

    /**
     * Find the nearest visible defense to the left in the same lane.
     * @return the nearest visible defense to the left in the same lane
     */
    @Override
    protected Entity getNearestVisibleTarget() {
        List<Entity> defenses = getAllTargets();
        Entity nearest = null;
        float nearestDistance = Float.MAX_VALUE;
        Vector2 myPos = owner.getEntity().getPosition();

        for (Entity entity : defenses) {
            Vector2 targetPos = entity.getPosition();
            float dist = myPos.dst(targetPos);
            // check if the target is in front of the gunner
            boolean sameLane = Math.abs(myPos.y - targetPos.y) < 1f;
            boolean inFront = targetPos.x < myPos.x;
            // check if the target is in range and is the nearest
            if (sameLane && inFront && dist < nearestDistance && dist <= attackRange) {
                nearest = entity;
                nearestDistance = dist;
            }
        }
        // log the nearest visible defense
        if (nearest != null) {
            logger.info("Nearest visible defense: {} at {} (dist = {})",
                    nearest, nearest.getPosition(), nearestDistance);
        }

        return nearest;
    }

    /**
     * Get the current target of the gunner
     * @return the current target of the gunner
     */
    public Entity getCurrentTarget() {
        return currentTarget;
    }
}
