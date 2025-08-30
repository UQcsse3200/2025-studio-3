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

// TODO : integrate with attack system team
public class AttackTask extends DefaultTask implements PriorityTask {
    private final Entity target;
    private final float attackRange;
    private final PhysicsEngine physics;
    private final DebugRenderer debugRenderer;
    private final RaycastHit hit = new RaycastHit();
    private MovementTask movementTask; // TODO: this should be projectiles

    public AttackTask(Entity target, float attackRange) {
        this.target = target;
        this.attackRange = attackRange;
        physics = ServiceLocator.getPhysicsService().getPhysics();
        debugRenderer = ServiceLocator.getRenderService().getDebug();
    }

    @Override
    public void start() {
        super.start();
        // TODO: attach logic instantiation instead
        movementTask = new MovementTask(target.getPosition());
        movementTask.create(owner);
        movementTask.start();

        this.owner.getEntity().getEvents().trigger("chaseStart");
    }

    @Override
    public void update() {
        System.out.println("AttackTask priority: " + getPriority());
        movementTask.setTarget(target.getPosition());
        movementTask.update();
        if (movementTask.getStatus() != Status.ACTIVE) {
            movementTask.start();
        }

        if (getDistanceToTarget() <= attackRange && isTargetVisible()) {
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
        return owner.getEntity().getPosition().dst(target.getPosition());
    }

    private int getActivePriority() {
        float dst = getDistanceToTarget();
        if (dst > attackRange || !isTargetVisible()) {
            return -1; // Too far, stop attack
        }
        return 1;
    }

    private int getInactivePriority() {
        float dst = getDistanceToTarget();
        if (dst <= attackRange && isTargetVisible()) {
            return 1;
        }
        return -1;
    }

    private boolean isTargetVisible() {
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
}
