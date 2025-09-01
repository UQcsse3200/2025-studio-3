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
public class AttackTask extends DefaultTask implements PriorityTask {
    private final List<Entity> targets;
    private final float attackRange;
    private final PhysicsEngine physics;
    private final DebugRenderer debugRenderer;
    private final RaycastHit hit = new RaycastHit();
    private MovementTask movementTask; // TODO: this should be projectiles

    public AttackTask(List<Entity> targets, float attackRange) {
        this.targets = targets;
        this.attackRange = attackRange;
        physics = ServiceLocator.getPhysicsService().getPhysics();
        debugRenderer = ServiceLocator.getRenderService().getDebug();
    }

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

    @Override
    public void stop() {
        super.stop();
        movementTask.stop();
    }

    // high priority only when close to enemy
    @Override
    public int getPriority() {
        if (status == Status.ACTIVE) {
            return getActivePriority();
        }

        return getInactivePriority();
    }

    private float getDistanceToTarget() {
        Entity target = getNearestVisibleTarget();
        if (target == null) {
            return Float.MAX_VALUE;
        }
        return owner.getEntity().getPosition().dst(target.getPosition());
    }

    private int getActivePriority() {
        float dst = getDistanceToTarget();
        Entity target = getNearestVisibleTarget();
        if (target == null) {
            return -1;
        }
        if (dst > attackRange || !isTargetVisible(target)) {
            return -1; // Too far, stop attack
        }
        return 1;
    }

    private int getInactivePriority() {
        float dst = getDistanceToTarget();
        Entity target = getNearestVisibleTarget();
        if (target == null) {
            return -1;
        }
        if (dst <= attackRange && isTargetVisible(target)) {
            return 1;
        }
        return -1;
    }

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
