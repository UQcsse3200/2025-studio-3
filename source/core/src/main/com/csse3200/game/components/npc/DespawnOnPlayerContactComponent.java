package com.csse3200.game.components.npc;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.csse3200.game.components.Component;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.HitboxComponent;

public class DespawnOnPlayerContactComponent extends Component {
    private final short targetLayer;
    private HitboxComponent hitbox;

    public DespawnOnPlayerContactComponent(short targetLayer) {
        this.targetLayer = targetLayer; // e.g., PhysicsLayer.PLAYER
    }

    @Override
    public void create() {
        hitbox = entity.getComponent(HitboxComponent.class);
        entity.getEvents().addListener("collisionStart", this::onCollisionStart);
    }

    private void onCollisionStart(Fixture me, Fixture other) {
        // only react if our hitbox caused this event
        if (hitbox == null || hitbox.getFixture() != me) return;

        // only react if the other fixture is on the target layer (PLAYER)
        if (!PhysicsLayer.contains(targetLayer, other.getFilterData().categoryBits)) return;

        // Ask whoever spawned me to despawn this entity
        entity.getEvents().trigger("despawnGhost", entity);
    }
}