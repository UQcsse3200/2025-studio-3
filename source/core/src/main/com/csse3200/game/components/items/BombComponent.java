package com.csse3200.game.components.items;

import com.badlogic.gdx.utils.Timer;
import com.csse3200.game.components.Component;

public class BombComponent extends Component {

    @Override
    public void create() {
        float fuseTime = 2f;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                boom();
            }
        }, fuseTime);
    }

    private void boom() {
        entity.getEvents().trigger("boom");
        entity.dispose();
    }

}
