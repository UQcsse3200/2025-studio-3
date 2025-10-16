package com.csse3200.game.entities.factories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Array;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.TouchAttackComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseBossConfig;
import com.csse3200.game.entities.configs.BossConfigs;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import java.lang.reflect.Field;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link BossFactory}. This version uses reflection to modify the contents of
 * the statically loaded config object, ensuring tests use predictable data without interfering with
 * other test classes.
 */
class BossFactoryTest {

  private static Application application;

  @BeforeAll
  static void beforeAll() {
    // A headless application is necessary for Gdx services to be available.
    application = new HeadlessApplication(new ApplicationAdapter() {});
    Gdx.gl = mock(GL20.class);
  }

  @AfterAll
  static void afterAll() {
    // Clean up the Gdx context.
    application.exit();
  }

  @BeforeEach
  void setUp() throws Exception {
    // Mock and register all necessary game services.
    ServiceLocator.registerPhysicsService(new PhysicsService());
    ServiceLocator.registerTimeSource(new GameTime());

    ResourceService mockResourceService = mock(ResourceService.class);
    TextureAtlas mockAtlas = mock(TextureAtlas.class);
    ServiceLocator.registerResourceService(mockResourceService);
    when(mockResourceService.getAsset(anyString(), eq(TextureAtlas.class))).thenReturn(mockAtlas);

    RenderService mockRenderService = mock(RenderService.class);
    when(mockRenderService.getDebug()).thenReturn(mock(DebugRenderer.class));
    ServiceLocator.registerRenderService(mockRenderService);

    TextureAtlas.AtlasRegion region = mock(TextureAtlas.AtlasRegion.class);
    Array<TextureAtlas.AtlasRegion> regions = new Array<>(new TextureAtlas.AtlasRegion[] {region});
    when(mockAtlas.findRegions(anyString())).thenReturn(regions);

    Field configsField = BossFactory.class.getDeclaredField("configs");
    configsField.setAccessible(true);
    BossConfigs staticallyLoadedConfigs = (BossConfigs) configsField.get(null);

    // Overwrite the fields within the existing object
    staticallyLoadedConfigs.scrapTitan = cfg(100, 10, 1f, "atlas/scrap_titan.atlas", 2.0f, 0f);
    staticallyLoadedConfigs.samuraiBot = cfg(200, 20, 2f, "atlas/samurai.atlas", 3.0f, 0f);
    staticallyLoadedConfigs.gunBot = cfg(150, 15, 1.5f, "atlas/gun_Bot.atlas", 2.5f, 5f);
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear();
  }

  private BaseBossConfig cfg(
      int health, int attack, float speed, String atlas, float scale, float range) {
    BaseBossConfig config = new BaseBossConfig();
    config.health = health;
    config.attack = attack;
    config.speed = speed;
    config.atlasFilePath = atlas;
    config.scale = scale;
    return config;
  }

  @Test
  void shouldCreateScrapTitanWithCorrectComponentsAndStats() {
    Entity scrapTitan = BossFactory.createBossType(BossFactory.BossTypes.SCRAP_TITAN);

    assertNotNull(scrapTitan);
    assertNotNull(scrapTitan.getComponent(CombatStatsComponent.class));
    assertNotNull(scrapTitan.getComponent(TouchAttackComponent.class));

    CombatStatsComponent stats = scrapTitan.getComponent(CombatStatsComponent.class);
    assertEquals(100, stats.getHealth());
    assertEquals(10, stats.getBaseAttack());

    scrapTitan.create();
    assertPhysicsLayers(scrapTitan, PhysicsLayer.ENEMY);
  }

  @Test
  void shouldCreateSamuraiBotWithCorrectComponentsAndStats() {
    Entity samuraiBot = BossFactory.createBossType(BossFactory.BossTypes.SAMURAI_BOT);

    assertNotNull(samuraiBot);
    assertNotNull(samuraiBot.getComponent(CombatStatsComponent.class));
    assertNotNull(samuraiBot.getComponent(TouchAttackComponent.class));

    CombatStatsComponent stats = samuraiBot.getComponent(CombatStatsComponent.class);
    assertEquals(200, stats.getHealth());
    assertEquals(20, stats.getBaseAttack());

    samuraiBot.create();
    assertPhysicsLayers(samuraiBot, PhysicsLayer.ENEMY);
  }

  @Test
  void shouldCreateGunBotWithCorrectComponentsAndStats() {
    Entity gunBot = BossFactory.createBossType(BossFactory.BossTypes.GUN_BOT);

    assertNotNull(gunBot);
    assertNotNull(gunBot.getComponent(CombatStatsComponent.class));
    assertNull(gunBot.getComponent(TouchAttackComponent.class));

    CombatStatsComponent stats = gunBot.getComponent(CombatStatsComponent.class);
    assertEquals(150, stats.getHealth());
    assertEquals(15, stats.getBaseAttack());

    gunBot.create();
    assertPhysicsLayers(gunBot, PhysicsLayer.ENEMY);
  }

  private void assertPhysicsLayers(Entity entity, short expectedLayer) {
    ColliderComponent collider = entity.getComponent(ColliderComponent.class);
    HitboxComponent hitbox = entity.getComponent(HitboxComponent.class);
    Filter filter = collider.getFixture().getFilterData();

    assertEquals(expectedLayer, filter.categoryBits);
    assertEquals(expectedLayer, hitbox.getLayer());

    short expectedMask = (short) (PhysicsLayer.DEFAULT | PhysicsLayer.NPC | PhysicsLayer.OBSTACLE);
    assertEquals(expectedMask, filter.maskBits);
  }
}
