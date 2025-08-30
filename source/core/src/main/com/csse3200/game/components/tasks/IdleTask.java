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

// stays idle until attack triggered
public class IdleTask extends DefaultTask implements PriorityTask {
    private final float attackRange;
    private final Entity target;
    private final PhysicsEngine physics;
    private final DebugRenderer debugRenderer;
    private final RaycastHit hit = new RaycastHit();



    public IdleTask(float attackRange, Entity target) {
        this.attackRange = attackRange;
        this.target = target;
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
}
