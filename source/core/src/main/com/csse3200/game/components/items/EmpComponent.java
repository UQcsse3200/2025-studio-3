package com.csse3200.game.components.items;

import com.badlogic.gdx.utils.Timer;
import com.csse3200.game.entities.Entity;

public class EmpComponent extends Item {
    public EmpComponent() {
        super.eventName = "emp_start";
        super.name = "EMP";
        super.desc = "End of line, man. Temporarily disables robots in a large radius.";
        super.cost = 30f;
    }

    /**
     * Identical functionality to super class function but with
     * a delay of 0.75 seconds.
     *
     * @param entities an array of target entities.
     */
    @Override
    protected void onDropped(Entity[] entities) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                // call the original onDropped logic from Item
                EmpComponent.super.onDropped(entities);
            }
        }, 0.75f);
    }
}
