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

public abstract class TargetDetectionTasks extends DefaultTask implements PriorityTask {
    protected final List<Entity> targets;
    protected final float attackRange;
    protected final PhysicsEngine physics;
    protected final DebugRenderer debugRenderer;
    protected final RaycastHit hit = new RaycastHit();

    public TargetDetectionTasks(List<Entity> targets, float attackRange) {
        this.targets = targets;
        this.attackRange = attackRange;
        physics = ServiceLocator.getPhysicsService().getPhysics();
        debugRenderer = ServiceLocator.getRenderService().getDebug();
    }

    /**
     * Gets the distance to the nearest visible target
     * @return the distance to the nearest target or a MAX VALUE if there is no target
     */
    protected float getDistanceToTarget() {
        Entity target = getNearestVisibleTarget();
        if (target == null) {
            return Float.MAX_VALUE;
        }
        return owner.getEntity().getPosition().dst(target.getPosition());
    }

    protected abstract int getActivePriority(float distance, Entity target);
    protected abstract int getInactivePriority(float distance, Entity target);

    /**
     * Determines the tasks priority
     * <ul>
     *     <li>When active: returns {@code 1} if target is in range and visible, otherwise {@code -1}.</li>
     *     <li>When inactive: returns {@code 1} if target is in range and visible, otherwise {@code -1}.</li>
     * </ul>
     * @return the tasks priority
     */
    @Override
    public int getPriority() {
        float dst = getDistanceToTarget();
        Entity target = getNearestVisibleTarget();
        if (status == Status.ACTIVE) {
            return getActivePriority(dst, target);
        } else {
            return getInactivePriority(dst, target);
        }
    }

        /**
     * Determines if a target is visible by checking for obstacles in the current entities line of sight
     * @param target the target to check
     * @return {@code true} if the target is visible, {@code false} otherwise
     */
    protected boolean isTargetVisible(Entity target) {
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
    protected Entity getNearestVisibleTarget() {
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
