package com.csse3200.game.components.items;

import com.badlogic.gdx.utils.Timer;
import com.csse3200.game.components.Component;

public class BombComponent extends Component {

    @Override
    public void create() {
        // control how long the bomb takes to blow up
        float fuseTime = 2f;
        // creates a background task that triggers the event "boom"
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                entity.getEvents().trigger("boom");
                entity.dispose(); // will need a blowing up animation later
            }
        }, fuseTime);
    }

}
