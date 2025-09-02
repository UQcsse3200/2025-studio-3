package com.csse3200.game.components.items;

public class BuffComponent extends Item {
    public BuffComponent() {
        super.eventName = "buff";
        super.name = "Buff";
        super.desc = "Power overwhelming! Doubles the maximum health of a specified unit.";
        super.cost = 25f;
    }
}
