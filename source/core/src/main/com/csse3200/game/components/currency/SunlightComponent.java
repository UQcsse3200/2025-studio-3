package com.csse3200.game.components.currency;

import com.csse3200.game.components.Component;


/**
 * Component holding the currency value granted by a single sunlight pickup.
 * Attach to an entity/actor representing a sun if you need ECS-side access.
 */
public class SunlightComponent extends Component {
    /** how much currency this sun gives when collected */
    private int value = 50;

    /** @return configured currency value of this sun */
    public int getValue() {
        return value;
    }


    /** @param v new value; @return this for chaining */
    public SunlightComponent setValue(int v) {
        this.value = v;
        return this;
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
