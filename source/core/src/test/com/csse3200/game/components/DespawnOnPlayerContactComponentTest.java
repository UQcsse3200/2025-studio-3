package com.csse3200.game.components;

import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Filter;
import com.csse3200.game.components.npc.DespawnOnPlayerContactComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.PhysicsLayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for DespawnOnPlayerContactComponent.
 */
@ExtendWith(MockitoExtension.class)
class DespawnOnPlayerContactComponentTest {

    @Mock HitboxComponent hitboxComponent;
    @Mock Fixture myFixture;     // this will represent "me" (our hitbox's fixture)
    @Mock Fixture otherFixture;  // the other colliding fixture

    private Entity entity;
    private DespawnOnPlayerContactComponent component;

    private static Filter makeFilter(short categoryBits) {
        Filter f = new Filter();
        f.categoryBits = categoryBits;
        return f;
    }

    @BeforeEach
    void setUp() {
        entity = new Entity();
        component = new DespawnOnPlayerContactComponent(PhysicsLayer.PLAYER);
    }

    @Test
    void triggersDespawnWhenPlayerContactsHitbox() {
        // Arrange: entity has a hitbox whose fixture is myFixture
        when(hitboxComponent.getFixture()).thenReturn(myFixture);
        entity.addComponent(hitboxComponent);
        entity.addComponent(component);
        entity.create(); // registers collisionStart listener and grabs hitbox

        // Capture whether despawn was fired
        final boolean[] called = {false};
        final Entity[] payload = {null};
        entity.getEvents().addListener("despawnRobot", (Entity e) -> {
            called[0] = true;
            payload[0] = e;
        });

        // Other fixture is on PLAYER layer → should match target
        when(otherFixture.getFilterData()).thenReturn(makeFilter(PhysicsLayer.PLAYER));

        // Act: simulate collisionStart
        entity.getEvents().trigger("collisionStart", myFixture, otherFixture);

        // Assert
        assertTrue(called[0], "despawnRobot should be triggered");
        assertSame(entity, payload[0], "payload should be this entity");
    }

    @Test
    void doesNotDespawnWhenOtherNotOnTargetLayer() {
        when(hitboxComponent.getFixture()).thenReturn(myFixture);
        entity.addComponent(hitboxComponent);
        entity.addComponent(component);
        entity.create();

        final boolean[] called = {false};
        entity.getEvents().addListener("despawnRobot", (Entity e) -> called[0] = true);

        // Other is on NPC (not PLAYER) → should NOT trigger
        when(otherFixture.getFilterData()).thenReturn(makeFilter(PhysicsLayer.NPC));

        entity.getEvents().trigger("collisionStart", myFixture, otherFixture);

        assertFalse(called[0], "Should not trigger despawn for non-target layer");
    }




    @Test
    void doesNotTriggerWhenMeIsNotOurHitboxFixture() {
        when(hitboxComponent.getFixture()).thenReturn(myFixture);
        entity.addComponent(hitboxComponent);
        entity.addComponent(component);
        entity.create();

        final boolean[] despawn = {false};
        entity.getEvents().addListener("despawnRobot", (Entity e) -> despawn[0] = true);

        Fixture someOtherFixture = mock(Fixture.class); // 'me' is NOT our hitbox fixture

        // No need to stub otherFixture.getFilterData(); method returns early
        entity.getEvents().trigger("collisionStart", someOtherFixture, otherFixture);

        assertFalse(despawn[0]);
    }

    @Test
    void doesNotTriggerWhenNoHitboxPresent() {
        entity.addComponent(component);
        entity.create();

        final boolean[] despawn = {false};
        entity.getEvents().addListener("despawnRobot", (Entity e) -> despawn[0] = true);

        // No need to stub otherFixture.getFilterData(); method returns early
        entity.getEvents().trigger("collisionStart", myFixture, otherFixture);

        assertFalse(despawn[0]);
    }

    @Test
    void triggersWhenConfiguredForNpcLayer() {
        component = new DespawnOnPlayerContactComponent(PhysicsLayer.NPC);

        when(hitboxComponent.getFixture()).thenReturn(myFixture);
        entity.addComponent(hitboxComponent);
        entity.addComponent(component);
        entity.create();

        final boolean[] despawn = {false};
        entity.getEvents().addListener("despawnRobot", (Entity e) -> despawn[0] = true);

        // Here it IS needed because the code reaches the layer check
        when(otherFixture.getFilterData()).thenReturn(makeFilter(PhysicsLayer.NPC));

        entity.getEvents().trigger("collisionStart", myFixture, otherFixture);

        assertTrue(despawn[0]);
    }

    @Test
    void doesNotTriggerWhenHitboxFixtureIsNull() {
        when(hitboxComponent.getFixture()).thenReturn(null); // null hitbox fixture
        entity.addComponent(hitboxComponent);
        entity.addComponent(component);
        entity.create();

        final boolean[] despawn = {false};
        entity.getEvents().addListener("despawnRobot", (Entity e) -> despawn[0] = true);

        // No need to stub otherFixture.getFilterData(); method returns early
        entity.getEvents().trigger("collisionStart", myFixture, otherFixture);

        assertFalse(despawn[0]);
    }




}