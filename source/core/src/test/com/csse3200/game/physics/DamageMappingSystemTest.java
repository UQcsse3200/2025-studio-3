package com.csse3200.game.physics;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.attacking_system.DamageMappingSystem;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.services.ProfileService;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.SettingsService;
import com.csse3200.game.progression.Profile;
import com.csse3200.game.progression.statistics.Statistics;
import com.csse3200.game.progression.wallet.Wallet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DamageMappingSystemTest {
  private Entity attacker;
  private Entity defender;
  private CombatStatsComponent attackerStats;
  private CombatStatsComponent defenderStats;
  private Fixture attackerFixture;
  private Fixture defenderFixture;
  private DamageMappingSystem damageSystem;
  private ResourceService resources;

  @BeforeEach
  void setUp() {
    // Mock resource and settings services
    resources = mock(ResourceService.class);
    ServiceLocator.registerResourceService(resources);
    SettingsService mockSettingsService = mock(SettingsService.class);
    ServiceLocator.registerSettingsService(mockSettingsService);
    when(mockSettingsService.getSoundVolume()).thenReturn(1.0f);

    // Mock ProfileService
    ProfileService mockProfileService = mock(ProfileService.class);
    Profile mockProfile = mock(Profile.class);
    Statistics mockStatistics = mock(Statistics.class);
    Wallet mockWallet = mock(Wallet.class);
    
    when(mockProfileService.getProfile()).thenReturn(mockProfile);
    when(mockProfile.getStatistics()).thenReturn(mockStatistics);
    when(mockProfile.getWallet()).thenReturn(mockWallet);
    ServiceLocator.registerProfileService(mockProfileService);

    Sound mockSound = mock(Sound.class);
    // Mock the sound assets that CombatStatsComponent might request
    when(resources.getAsset("sounds/human-death.mp3", Sound.class)).thenReturn(mockSound);
    when(resources.getAsset("sounds/robot-death.mp3", Sound.class)).thenReturn(mockSound);
    when(resources.getAsset("sounds/damage.mp3", Sound.class)).thenReturn(mockSound);
    when(resources.getAsset("sounds/generator-death.mp3", Sound.class)).thenReturn(mockSound);

    attacker = new Entity();
    attackerStats = new CombatStatsComponent(100, 10);
    attacker.addComponent(attackerStats);
    attacker.addComponent(mock(HitboxComponent.class));
    attacker.create();
    attacker.setProperty("isProjectile", true);

    defender = new Entity();
    defenderStats = new CombatStatsComponent(50, 5);
    defender.addComponent(defenderStats);
    defender.addComponent(mock(ColliderComponent.class));
    defender.create();

    damageSystem = new DamageMappingSystem(attacker);

    attackerFixture = mock(Fixture.class);
    var attackerBody = mock(com.badlogic.gdx.physics.box2d.Body.class);
    BodyUserData attackerData = new BodyUserData();
    attackerData.setEntity(attacker);
    when(attackerFixture.getBody()).thenReturn(attackerBody);
    when(attackerBody.getUserData()).thenReturn(attackerData);

    defenderFixture = mock(Fixture.class);
    var defenderBody = mock(com.badlogic.gdx.physics.box2d.Body.class);
    BodyUserData defenderData = new BodyUserData();
    defenderData.setEntity(defender);
    when(defenderFixture.getBody()).thenReturn(defenderBody);
    when(defenderBody.getUserData()).thenReturn(defenderData);
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear();
  }

  @Test
  void DamgaeIsApplied() {
    damageSystem.onCollisionStart(attackerFixture, defenderFixture);
    assertEquals(40, defenderStats.getHealth(), "Defender should lose 10HP.");
  }

  @Test
  void DeathTriggered() {
    defenderStats.setHealth(5);
    final boolean[] deathTriggered = {false};
    defender.getEvents().addListener("death", () -> deathTriggered[0] = true);
    damageSystem.onCollisionStart(attackerFixture, defenderFixture);
    assertTrue(defenderStats.getHealth() <= 0, "Defender HP should be <=0");
  }

  @Test
  void NoDamageIfNoCombatStats() {
    Entity temp = new Entity();
    temp.create();

    Fixture tempFixture = mock(Fixture.class);
    var tempBody = mock(com.badlogic.gdx.physics.box2d.Body.class);
    BodyUserData tempData = new BodyUserData();
    tempData.setEntity(temp);
    when(tempFixture.getBody()).thenReturn(tempBody);
    when(tempBody.getUserData()).thenReturn(tempData);
    damageSystem.onCollisionStart(attackerFixture, tempFixture);
    assertEquals(
        50, defenderStats.getHealth(), "Entity without CombatStats should not take Damage");
  }

  @Test
  void DestroyTriggered() {
    final boolean[] destroyTriggered = {false};
    attacker.getEvents().addListener("destroy", () -> destroyTriggered[0] = true);
    damageSystem.onCollisionStart(attackerFixture, defenderFixture);
    assertTrue(destroyTriggered[0], "Projectile should trigger destroy event.");
  }

  @Test
  void NoDamageIfNotProjectile() {
    attacker.setProperty("isProjectile", false);
    damageSystem.onCollisionStart(attackerFixture, defenderFixture);
    assertEquals(50, defenderStats.getHealth(), "Non-Projectile should not deal damage");
  }
}
