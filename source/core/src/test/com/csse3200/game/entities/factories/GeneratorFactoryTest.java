package com.csse3200.game.entities.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.csse3200.game.components.GeneratorStatsComponent;
import com.csse3200.game.components.HitMarkerComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseGeneratorConfig;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.progression.Profile;
import com.csse3200.game.progression.skilltree.SkillSet;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ProfileService;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GeneratorFactoryTest {
  private ConfigService mockConfigService;
  private static final String HEAL = "heal";

  @BeforeEach
  void setUp() {
    ServiceLocator.registerTimeSource(new GameTime());

    // Physics world
    ServiceLocator.registerPhysicsService(new PhysicsService());

    // Render service with debug renderer
    RenderService mockRenderService = mock(RenderService.class);
    DebugRenderer mockDebugRenderer = mock(DebugRenderer.class);
    when(mockRenderService.getDebug()).thenReturn(mockDebugRenderer);
    ServiceLocator.registerRenderService(mockRenderService);

    // Resource service + atlas for animator
    ResourceService mockResourceService = mock(ResourceService.class);
    TextureAtlas mockAtlas = mock(TextureAtlas.class);
    ServiceLocator.registerResourceService(mockResourceService);
    when(mockResourceService.getAsset(anyString(), eq(TextureAtlas.class))).thenReturn(mockAtlas);

    TextureAtlas.AtlasRegion region = mock(TextureAtlas.AtlasRegion.class);
    when(region.getRegionWidth()).thenReturn(16);
    when(region.getRegionHeight()).thenReturn(16);
    when(region.getTexture()).thenReturn(mock(Texture.class));

    Array<TextureAtlas.AtlasRegion> list = new Array<>();
    list.add(region);

    for (String name : new String[] {"default", "idle", "attack"}) {
      when(mockAtlas.findRegions(name)).thenReturn(list);
      when(mockAtlas.findRegion(name)).thenReturn(region);
    }

    ProfileService mockProfileService = mock(ProfileService.class);
    Profile mockProfile = mock(Profile.class);
    SkillSet mockSkillSet = mock(SkillSet.class);

    when(mockProfileService.getProfile()).thenReturn(mockProfile);
    when(mockProfile.getSkillset()).thenReturn(mockSkillSet);
    ServiceLocator.registerProfileService(mockProfileService);

    // mock generator config
    mockConfigService = mock(ConfigService.class);

    // fake configs for each generator
    when(mockConfigService.getGeneratorConfig("furnace"))
        .thenReturn(
            cfg(
                "Furnace",
                "images/entities/defences/forge_1.png",
                "images/entities/defences/forge.atlas",
                1,
                7,
                25,
                50));

    when(mockConfigService.getGeneratorConfig("healer"))
        .thenReturn(
            cfg(
                "Healer",
                "images/entities/defences/healer_1.png",
                "images/entities/defences/healer.atlas",
                50,
                0,
                0,
                200));
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear();
  }

  private static BaseGeneratorConfig cfg(
      String name, String asset, String atlas, int health, int interval, int scrapValue, int cost) {

    BaseGeneratorConfig c = new BaseGeneratorConfig();
    try {
      java.lang.reflect.Field nameField =
          BaseGeneratorConfig.class.getSuperclass().getDeclaredField("name");
      nameField.setAccessible(true);
      nameField.set(c, name);

      java.lang.reflect.Field atlasField =
          BaseGeneratorConfig.class.getSuperclass().getDeclaredField("atlasPath");
      atlasField.setAccessible(true);
      atlasField.set(c, atlas);

      java.lang.reflect.Field assetField =
          BaseGeneratorConfig.class.getSuperclass().getDeclaredField("assetPath");
      assetField.setAccessible(true);
      assetField.set(c, asset);

      java.lang.reflect.Field healthField =
          BaseGeneratorConfig.class.getSuperclass().getDeclaredField("health");
      healthField.setAccessible(true);
      healthField.set(c, health);

      java.lang.reflect.Field intervalField =
          BaseGeneratorConfig.class.getDeclaredField("interval");
      intervalField.setAccessible(true);
      intervalField.set(c, interval);

      java.lang.reflect.Field scrapValueField =
          BaseGeneratorConfig.class.getDeclaredField("scrapValue");
      scrapValueField.setAccessible(true);
      scrapValueField.set(c, scrapValue);

      java.lang.reflect.Field costField = BaseGeneratorConfig.class.getDeclaredField("cost");
      costField.setAccessible(true);
      costField.set(c, cost);

    } catch (Exception e) {
      throw new RuntimeException("Failed to create test generator config", e);
    }
    return c;
  }

  @Test
  void testCreateGeneratorUnitNotNull() {
    // create each defence entity
    for (String defence : new String[] {"furnace", "healer"}) {
      Entity generator =
          GeneratorFactory.createGeneratorUnit(mockConfigService.getGeneratorConfig(defence));
      assertNotNull(
          generator, () -> "DefenceFactory.createDefenceUnit(" + defence + ") returned null");
    }
  }

  @Test
  void testGeneratorUnitHasPhysicsComponent() {
    for (String defence : new String[] {"furnace", "healer"}) {
      Entity generator =
          GeneratorFactory.createGeneratorUnit(mockConfigService.getGeneratorConfig(defence));
      assertNotNull(
          generator.getComponent(PhysicsComponent.class),
          () -> generator + ": missing PhysicsComponent");
    }
  }

  @Test
  void testGeneratorUnitHasColliderComponent() {
    for (String defence : new String[] {"furnace", "healer"}) {
      Entity generator =
          GeneratorFactory.createGeneratorUnit(mockConfigService.getGeneratorConfig(defence));
      assertNotNull(
          generator.getComponent(ColliderComponent.class),
          () -> generator + ": missing ColliderComponent");
    }
  }

  @Test
  void testDefenceUnitHasHitBoxComponent() {
    for (String defence : new String[] {"furnace", "healer"}) {
      Entity generator =
          GeneratorFactory.createGeneratorUnit(mockConfigService.getGeneratorConfig(defence));
      assertNotNull(
          generator.getComponent(HitboxComponent.class),
          () -> generator + ": missing HitBoxComponent");
    }
  }

  @Test
  void testGeneratorUnitHasGeneratorStatsComponent() {
    for (String defence : new String[] {"furnace", "healer"}) {
      Entity generator =
          GeneratorFactory.createGeneratorUnit(mockConfigService.getGeneratorConfig(defence));
      assertNotNull(
          generator.getComponent(GeneratorStatsComponent.class),
          () -> generator + ": missing GeneratorStatsComponent");
    }
  }

  @Test
  void testGeneratorUnitHasHitMarkerComponent() {
    for (String defence : new String[] {"furnace", "healer"}) {
      Entity generator =
          GeneratorFactory.createGeneratorUnit(mockConfigService.getGeneratorConfig(defence));
      assertNotNull(
          generator.getComponent(HitMarkerComponent.class),
          () -> generator + ": missing HitMarkerComponent");
    }
  }

  @Test
  void testGeneratorListenForHeal() {
    for (String defence : new String[] {"furnace", "healer"}) {
      Entity generator =
          GeneratorFactory.createGeneratorUnit(mockConfigService.getGeneratorConfig(defence));

      int oldHealth = generator.getComponent(GeneratorStatsComponent.class).getHealth();
      generator.getEvents().trigger(HEAL);
      int newHealth = generator.getComponent(GeneratorStatsComponent.class).getHealth();

      assert (newHealth == oldHealth + 20);
    }
  }

  @Test
  void testGetAnimation() {
    BaseGeneratorConfig furnace =
        cfg(
            "Furnace",
            "images/entities/defences/forge_1.png",
            "images/entities/defences/forge.atlas",
            1,
            7,
            25,
            50);
    AnimationRenderComponent animator = GeneratorFactory.getAnimationComponent(furnace);
    assertNotNull(animator);
    assertTrue(animator.hasAnimation("idle"), "Should have idle animation");
  }

  @Test
  void testCreateBaseGenerator() {
    Entity baseGenerator = GeneratorFactory.createBaseGenerator();
    baseGenerator.create();

    assertNotNull(baseGenerator, "Entity should not be null");

    assertNotNull(
        baseGenerator.getComponent(ColliderComponent.class), "ColliderComponent should exist");
    assertNotNull(
        baseGenerator.getComponent(PhysicsComponent.class), "PhysicsComponent should exist");
    assertNotNull(
        baseGenerator.getComponent(HitboxComponent.class), "HitboxComponent should exist");
    assertNotNull(
        baseGenerator.getComponent(HitMarkerComponent.class), "HitMarkerComponent should exist");

    PhysicsComponent physics = baseGenerator.getComponent(PhysicsComponent.class);
    assertEquals(
        BodyDef.BodyType.StaticBody, physics.getBody().getType(), "Body type should be StaticBody");

    ColliderComponent collider = baseGenerator.getComponent(ColliderComponent.class);
    short expectedFilter =
        (short)
            (PhysicsLayer.DEFAULT | PhysicsLayer.OBSTACLE | PhysicsLayer.ENEMY | PhysicsLayer.BOSS);
    assertEquals(
        PhysicsLayer.NPC,
        collider.getFixture().getFilterData().categoryBits,
        "Collision layer should be NPC");
    assertEquals(
        expectedFilter,
        collider.getFixture().getFilterData().maskBits,
        "Collision mask should be correct");
  }
}
