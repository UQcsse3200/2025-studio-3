package com.csse3200.game.components.items;

public class CoffeeComponent extends Item {

    public CoffeeComponent() {
        super.eventName = "coffee_start";
        super.name = "Coffee";
        super.desc = "Some serious gourmet shit. Temporarily increases attack speed for a specified unit.";
        super.cost = 5f;
    }
}
