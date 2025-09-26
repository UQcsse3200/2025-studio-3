package com.csse3200.game.components.tasks;

import com.csse3200.game.entities.Entity;

public class JumpTask extends RobotTargetDetectionTasks {
    private boolean hasJumped = false;

    public JumpTask(float attackRange, short targetLayer) {
        super(attackRange, targetLayer);
    }

    @Override
    public void start() {
        super.start();
        this.owner.getEntity().getEvents().trigger("jumpStart");
    }

    @Override
    public void update() {
        Entity target = getNearestVisibleTarget();

        if (target == null || hasJumped) {
            return;
        }
        this.owner.getEntity().getEvents().trigger("jump");
        hasJumped = true;
    }
}
