package com.csse3200.game.components.items;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;

public class BombComponent extends Component {

    @Override
    public void create() {
        // assuming drag & drop, listen for dropped event (assumed successful placement)
        entity.getEvents().addListener("dropped", this::onDropped);
    }

    private void onDropped(Entity[] enemies) {
        // enemies contains all enemies in the range of the bombs explosion
        for (Entity e : enemies) {
            // iterate through enemies, triggering boom event
            // (could be replaced with generic damage event
            e.getEvents().trigger("boom");
        }
        entity.dispose();
    }

}
