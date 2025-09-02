package com.csse3200.game.components.items;

import com.badlogic.gdx.utils.Timer;
import com.csse3200.game.entities.Entity;

public class GrenadeComponent extends Item {
    public GrenadeComponent() {
        super.eventName = "grenade";
        super.name = "Grenade";
        super.desc = "With it thou mayst blow thine enemies to tiny bits. Damages enemies in a small radius after a short delay.";
        super.cost = 35f;
    }

    /**
     * Identical functionality to super class function but with
     * a delay of 1.5 seconds.
     *
     * @param entities an array of target entities.
     */
    @Override
    protected void onDropped(Entity[] entities) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                // call the original onDropped logic from Item
                GrenadeComponent.super.onDropped(entities);
            }
        }, 1.5f);
    }
}
