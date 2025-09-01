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

// stays idle until attack triggered
public class IdleTask extends DefaultTask implements PriorityTask {
    private final float attackRange;
    private final List<Entity> targets;
    private final PhysicsEngine physics;
    private final DebugRenderer debugRenderer;
    private final RaycastHit hit = new RaycastHit();



    public IdleTask(float attackRange, List<Entity> targets) {
        this.attackRange = attackRange;
        this.targets = targets;
        physics = ServiceLocator.getPhysicsService().getPhysics();
        debugRenderer = ServiceLocator.getRenderService().getDebug();
    }

    @Override
    public void update() {
        System.out.println("IdleTask priority: " + getPriority());
    }

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
        if (dst <= attackRange) {
            return -1; // inactive when enemy in range
        }
        return 1; // keep idle
    }

    private int getInactivePriority() {
        float dst = getDistanceToTarget();
        if (dst <= attackRange) {
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
