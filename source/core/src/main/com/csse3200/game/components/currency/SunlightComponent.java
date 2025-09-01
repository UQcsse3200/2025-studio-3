package com.csse3200.game.components.currency;

import com.csse3200.game.components.Component;

public class SunlightComponent extends Component {
    private int value = 50; // how much currency this sun gives when collected

    public int getValue() {
        return value;
    }

    public SunlightComponent setValue(int v) {
        this.value = v;
        return this;
    }
}
