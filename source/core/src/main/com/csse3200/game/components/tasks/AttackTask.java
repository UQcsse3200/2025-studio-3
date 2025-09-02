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

// TODO : integrate with attack system team

/**
 * Allows an entity to attack the closest target entity from a list of potential targets.
 * This task runs when there is a visible target within the entities range of attack
 */
public class AttackTask extends DefaultTask implements PriorityTask {
    private final List<Entity> targets;
    private final float attackRange;
    private final PhysicsEngine physics;
    private final DebugRenderer debugRenderer;
    private final RaycastHit hit = new RaycastHit();
    private MovementTask movementTask; // TODO: this should be projectiles

    /**
     * Creates an attack task
     * @param targets a list of potential targets
     * @param attackRange the maximum distance the entity can find a target to attack
     */
    public AttackTask(List<Entity> targets, float attackRange) {
        this.targets = targets;
        this.attackRange = attackRange;
        physics = ServiceLocator.getPhysicsService().getPhysics();
        debugRenderer = ServiceLocator.getRenderService().getDebug();
    }

    /**
     * Starts the attack task. The closest visible target within the entity's attack range is found
     * and ATTACK LOGIC BEGINS.
     */
    @Override
    public void start() {
        super.start();
        Entity target = getNearestVisibleTarget();
        if (target != null) {
            // TODO: attach logic instantiation instead
            movementTask = new MovementTask(target.getPosition());
            movementTask.create(owner);
            movementTask.start();
        }

        this.owner.getEntity().getEvents().trigger("chaseStart");
    }

    /**
     * Updates the task each game frame
     * // TODO finish this doc with attack logic
     */
    @Override
    public void update() {
        System.out.println("AttackTask priority: " + getPriority());
        Entity target = getNearestVisibleTarget();

        if (target == null) {
            return;
        }

        movementTask.setTarget(target.getPosition());
        movementTask.update();
        if (movementTask.getStatus() != Status.ACTIVE) {
            movementTask.start();
        }

        if (getDistanceToTarget() <= attackRange && isTargetVisible(target)) {
            // TODO: attack
        }
    }

    /**
     * Stops the attack
     */
    @Override
    public void stop() {
        super.stop();
        movementTask.stop();
    }

    // high priority only when close to enemy

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
        if (target == null) {
            return Float.MAX_VALUE;
        }
        return owner.getEntity().getPosition().dst(target.getPosition());
    }

    /**
     * Determines the tasks priority when the task is running.
     * @return {@code 1} if a target is visible and within attack range,
     *         otherwise {@code -1}
     */
    private int getActivePriority() {
        float dst = getDistanceToTarget();
        Entity target = getNearestVisibleTarget();
        if (target == null) {
            return -1; // stop task if no target
        }
        if (dst > attackRange || !isTargetVisible(target)) {
            return -1; // stop task when target not visible or out of range
        }
        return 1;
    }

    /**
     * Computes the priority when the task is inactive.
     *
     * @return {@code 1} if the target is visible and within attack range,
     *         otherwise {@code -1}
     */
    private int getInactivePriority() {
        float dst = getDistanceToTarget();
        Entity target = getNearestVisibleTarget();
        if (target == null) {
            return -1;
        }
        if (dst <= attackRange && isTargetVisible(target)) {
            return 1; // start task if target is visible and in range
        }
        return -1;
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
