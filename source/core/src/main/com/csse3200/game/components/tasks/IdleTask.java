package com.csse3200.game.components.tasks;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.ai.tasks.DefaultTask;
import com.csse3200.game.ai.tasks.PriorityTask;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsEngine;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.raycast.RaycastHit;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.services.ServiceLocator;

import java.util.List;

/**
 * Keeps an entity idle until a target entity is detected within the attack range.
 *
 * A list of available target entities is monitored to determine whether the current entity should remain idle
 * or prepare for an attack, based on the target's distance and visibility. The closest visible target is found and
 * if the target is outside the entity's range of attack, entity remains idle. Otherwise, this task becomes inactive and
 * another task (attack task) takes over.
 */
public class IdleTask extends DefaultTask implements PriorityTask {
    private final float attackRange;
    private final List<Entity> targets;
    private final PhysicsEngine physics;
    private final DebugRenderer debugRenderer;
    private final RaycastHit hit = new RaycastHit();

    /**
     * Creates an IdleTask
     * @param attackRange the maximum distance at which the entity can attack a target
     * @param targets a list of potential targets
     */
    public IdleTask(float attackRange, List<Entity> targets) {
        this.attackRange = attackRange;
        this.targets = targets;
        physics = ServiceLocator.getPhysicsService().getPhysics();
        debugRenderer = ServiceLocator.getRenderService().getDebug();
    }

//    @Override
//    public void update() {
//        System.out.println("IdleTask priority: " + getPriority());
//    }

    /**
     * The task is active when the target is in the entity's range of attach
     * @return task's priority
     */
    public int getPriority() {
        if (status == Status.ACTIVE) {
            return getActivePriority();
        }

        return getInactivePriority();
    }

    /**
     * Gets the distance to the nearest visible target
     * @return the distance to the nearest target or a MAX VALUE if there is no target
     */
    private float getDistanceToTarget() {
        Entity target = getNearestVisibleTarget();
        if (target == null) { // no target in range or visible
            return Float.MAX_VALUE;
        }
        return owner.getEntity().getPosition().dst(target.getPosition());
    }

    /**
     * If task is currently running, determine whether to stay running
     * @return {@code -1} if an enemy is within attack range (deactivate),
     *          otherwise {@code 1} (stay idle)
     */
    private int getActivePriority() {
        float dst = getDistanceToTarget();
        if (dst <= attackRange) {
            return -1; // stop task when target in range
        }
        return 1; // keep idle; active
    }

    /**
     * If task is not currently running, determine if it should run
     * @return {@code -1} if an enemy is within attack range
     *          otherwise {@code 1} (start task)
     */
    private int getInactivePriority() {
        float dst = getDistanceToTarget();
        if (dst <= attackRange) {
            return -1; // if target is in range, don't start task
        }
        return 1;
    }

    /**
     * Determines if a target is visible by checking for obstacles in the current entities line of sight
     * @param target the target to check
     * @return {@code true} if the target is visible, {@code false} otherwise
     */
    private boolean isTargetVisible(Entity target) {
        Vector2 from = owner.getEntity().getCenterPosition();
        Vector2 to = target.getCenterPosition();

        // If there is an obstacle in the path to the player, not visible.
        if (physics.raycast(from, to, PhysicsLayer.OBSTACLE, hit)) {
            debugRenderer.drawLine(from, hit.point);
            return false;
        }
        debugRenderer.drawLine(from, to);
        return true;
    }

    /**
     * Finds the nearest visible target within attack range.
     *
     * @return the closest visible target within range, or {@code null} if none
     */
    private Entity getNearestVisibleTarget() {
        Vector2 from = owner.getEntity().getCenterPosition();
        Entity closestTarget = null;
        float closestDist = Float.MAX_VALUE;

        for (Entity target : targets) {
            Vector2 targetPos = target.getCenterPosition();
            float distance = from.dst(targetPos);
            if (isTargetVisible(target) && distance <= attackRange) { // if target visible and in range
                if (distance < closestDist) {
                    closestDist = distance;
                    closestTarget = target;
                }
            }
        }
        return closestTarget;
    }
}
