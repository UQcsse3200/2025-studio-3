package com.csse3200.game.components;

public class ProjectileComponent extends Component{
    private final int damage;

    public ProjectileComponent(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }
}
