package com.csse3200.game.components;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.services.CurrencyService;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.SettingsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class TouchAttackComponentTest {
  @BeforeEach
  void beforeEach() {
    ServiceLocator.registerPhysicsService(new PhysicsService());
    ServiceLocator.registerCurrencyService(new CurrencyService(0, Integer.MAX_VALUE));
    // Mock resource and settings services
    ResourceService resources = mock(ResourceService.class);
    ServiceLocator.registerResourceService(resources);
    SettingsService mockSettingsService = mock(SettingsService.class);
    ServiceLocator.registerSettingsService(mockSettingsService);
    when(mockSettingsService.getSoundVolume()).thenReturn(1.0f);

    Sound mockSound = mock(Sound.class);
    // Mock the sound assets that CombatStatsComponent might request
    when(resources.getAsset("sounds/human-death.mp3", Sound.class)).thenReturn(mockSound);
    when(resources.getAsset("sounds/robot-death.mp3", Sound.class)).thenReturn(mockSound);
    when(resources.getAsset("sounds/damage.mp3", Sound.class)).thenReturn(mockSound);
    when(resources.getAsset("sounds/generator-death.mp3", Sound.class)).thenReturn(mockSound);
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear();
  }

  @Test
  void shouldAttack() {
    short targetLayer = (1 << 3);
    Entity entity = createAttacker(targetLayer);
    Entity target = createTarget(targetLayer);

    Fixture entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
    Fixture targetFixture = target.getComponent(HitboxComponent.class).getFixture();
    entity.getEvents().trigger("collisionStart", entityFixture, targetFixture);

    assertEquals(0, target.getComponent(CombatStatsComponent.class).getHealth());
  }

  @Test
  void shouldNotAttackOtherLayer() {
    short targetLayer = (1 << 3);
    short attackLayer = (1 << 4);
    Entity entity = createAttacker(attackLayer);
    Entity target = createTarget(targetLayer);

    Fixture entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
    Fixture targetFixture = target.getComponent(HitboxComponent.class).getFixture();
    entity.getEvents().trigger("collisionStart", entityFixture, targetFixture);

    assertEquals(10, target.getComponent(CombatStatsComponent.class).getHealth());
  }

  @Test
  void shouldNotAttackWithoutCombatComponent() {
    short targetLayer = (1 << 3);
    Entity entity = createAttacker(targetLayer);
    // Target does not have a combat component
    Entity target =
        new Entity()
            .addComponent(new PhysicsComponent())
            .addComponent(new HitboxComponent().setLayer(targetLayer));
    target.create();

    Fixture entityFixture = entity.getComponent(HitboxComponent.class).getFixture();
    Fixture targetFixture = target.getComponent(HitboxComponent.class).getFixture();

    // This should not cause an exception, but the attack should be ignored
    assertDoesNotThrow(
        () -> entity.getEvents().trigger("collisionStart", entityFixture, targetFixture));
  }

  Entity createAttacker(short targetLayer) {
    Entity entity =
        new Entity()
            .addComponent(new TouchAttackComponent(targetLayer))
            .addComponent(new CombatStatsComponent(0, 10))
            .addComponent(new PhysicsComponent())
            .addComponent(new HitboxComponent());
    entity.create();
    return entity;
  }

  Entity createTarget(short layer) {
    Entity target =
        new Entity()
            .addComponent(new CombatStatsComponent(10, 0))
            .addComponent(new PhysicsComponent())
            .addComponent(new HitboxComponent().setLayer(layer));
    target.create();
    return target;
  }
}
