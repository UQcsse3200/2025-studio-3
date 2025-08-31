package com.csse3200.game.components.items;


import com.csse3200.game.components.Component;
import com.csse3200.game.entities.Entity;

import java.util.Vector;

public class CoffeeComponent extends Component {

    @Override
    public void create() {
        // assuming drag & drop, listen for dropped event (assumed successful placement)
        entity.getEvents().addListener("dropped", this::onDropped);
    }

    private void onDropped(Entity tower) {
        // coffee entity has been dropped successfully on "tower"
        tower.getEvents().trigger("coffee");
        entity.dispose();
        // item functionality logic handled in "tower"
    }
}
