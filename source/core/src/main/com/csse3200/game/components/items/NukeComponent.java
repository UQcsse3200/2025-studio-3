package com.csse3200.game.components.items;

import com.badlogic.gdx.utils.Timer;
import com.csse3200.game.entities.Entity;

public class NukeComponent extends Item {
    public NukeComponent() {
        super.eventName = "nuke";
        super.name = "Nuke";
        super.desc = "Become death, destroyer of worlds. Obliterates everything on screen.";
        super.cost = 999f;
    }

    /**
     * Identical functionality to super class function but with
     * a delay of five seconds.
     *
     * @param entities an array of target entities.
     */
    @Override
    protected void onDropped(Entity[] entities) {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                // call the original onDropped logic from Item
                NukeComponent.super.onDropped(entities);
            }
        }, 5f);
    }
}
