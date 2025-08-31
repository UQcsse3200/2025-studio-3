package com.csse3200.game.components.items;

import com.csse3200.game.components.Component;

public class CoffeeComponent extends Component {

    @Override
    public void create() {
        entity.getEvents().trigger("coffee");
        // termination of the coffee effect must be controlled elsewhere
        entity.dispose();
    }

}
