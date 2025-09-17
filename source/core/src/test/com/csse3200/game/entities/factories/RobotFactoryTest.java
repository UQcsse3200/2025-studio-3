package com.csse3200.game.entities.factories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.utils.Array;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.HitMarkerComponent;
import com.csse3200.game.components.TouchAttackComponent;
import com.csse3200.game.components.npc.RobotAnimationController;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseEnemyConfig;
import com.csse3200.game.entities.configs.NPCConfigs;
import com.csse3200.game.persistence.FileLoader;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.AnimationRenderComponent;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class RobotFactoryTest {

  private MockedStatic<FileLoader> fileLoaderMock;

  @BeforeEach
  void setUp() {
    // Physics world
    ServiceLocator.registerPhysicsService(new PhysicsService());

    // Render service with debug renderer
    RenderService mockRenderService = mock(RenderService.class);
    DebugRenderer mockDebugRenderer = mock(DebugRenderer.class);
    when(mockRenderService.getDebug()).thenReturn(mockDebugRenderer);
    ServiceLocator.registerRenderService(mockRenderService);

    ServiceLocator.registerTimeSource(new GameTime());

    // Resource service + atlas for animator
    ResourceService mockResourceService = mock(ResourceService.class);
    TextureAtlas mockAtlas = mock(TextureAtlas.class);
    ServiceLocator.registerResourceService(mockResourceService);
    when(mockResourceService.getAsset(anyString(), eq(TextureAtlas.class))).thenReturn(mockAtlas);

    AtlasRegion region = mock(AtlasRegion.class);
    when(region.getRegionWidth()).thenReturn(16);
    when(region.getRegionHeight()).thenReturn(16);
    Array<AtlasRegion> list = new Array<>();
    list.add(region);

    for (String name :
        new String[] {"moveLeft", "attack", "damagedMoveLeft", "damagedAttack", "default"}) {
      when(mockAtlas.findRegions(name)).thenReturn(list);
      when(mockAtlas.findRegion(name)).thenReturn(region);
    }

    // Fake configs
    NPCConfigs fake = new NPCConfigs();
    fake.standardRobot = cfg(20, 5, 1.5f, "images/robot_placeholder.atlas", 1.0f);
    fake.fastRobot = cfg(15, 4, 3.0f, "images/robot_placeholder.atlas", 1.0f);
    fake.tankyRobot = cfg(40, 7, 0.9f, "images/robot_placeholder.atlas", 1.0f);
    fake.bungeeRobot = cfg(25, 6, 1.2f, "images/robot_placeholder.atlas", 1.0f);

    fileLoaderMock = mockStatic(FileLoader.class);
    fileLoaderMock
        .when(() -> FileLoader.readClass(eq(NPCConfigs.class), anyString()))
        .thenReturn(fake);
  }

  @AfterEach
  void tearDown() {
    if (fileLoaderMock != null) fileLoaderMock.close();
  }

  private static BaseEnemyConfig cfg(
      int health, int attack, float speed, String atlas, float scale) {
    BaseEnemyConfig c = new BaseEnemyConfig();
    c.health = health;
    c.attack = attack;
    c.movementSpeed = speed;
    c.atlasFilePath = atlas;
    c.scale = scale;
    return c;
  }

  @Test
  void createRobotType_allVariantsHaveCoreComponentsAndCorrectLayers() {
    for (RobotFactory.RobotType type : RobotFactory.RobotType.values()) {
      Entity robot = RobotFactory.createRobotType(type);
      assertNotNull(robot, () -> "RobotFactory.createRobotType(" + type + ") returned null");

      // Check expected components
      assertNotNull(
          robot.getComponent(PhysicsComponent.class), () -> type + ": missing PhysicsComponent");
      assertNotNull(
          robot.getComponent(ColliderComponent.class), () -> type + ": missing ColliderComponent");
      assertNotNull(
          robot.getComponent(HitboxComponent.class), () -> type + ": missing HitboxComponent");
      assertNotNull(
          robot.getComponent(CombatStatsComponent.class),
          () -> type + ": missing CombatStatsComponent");
      assertNotNull(
          robot.getComponent(AnimationRenderComponent.class), () -> type + ": missing Animator");
      assertNotNull(
          robot.getComponent(RobotAnimationController.class),
          () -> type + ": missing RobotAnimationController");
      assertNotNull(
          robot.getComponent(HitMarkerComponent.class),
          () -> type + ": missing HitMarkerComponent");
      assertNotNull(
          robot.getComponent(TouchAttackComponent.class),
          () -> type + ": missing TouchAttackComponent");

      // Animations should be present
      AnimationRenderComponent animator = robot.getComponent(AnimationRenderComponent.class);
      assertDoesNotThrow(() -> animator.startAnimation("moveLeft"), () -> type + ": no 'moveLeft'");
      assertDoesNotThrow(() -> animator.startAnimation("attack"), () -> type + ": no 'attack'");
      assertDoesNotThrow(
          () -> animator.startAnimation("damagedMoveLeft"), () -> type + ": no 'damagedMoveLeft'");
      assertDoesNotThrow(
          () -> animator.startAnimation("damagedAttack"), () -> type + ": no 'damagedAttack'");
      assertDoesNotThrow(() -> animator.startAnimation("default"), () -> type + ": no 'default'");

      robot.create();

      ColliderComponent collider = robot.getComponent(ColliderComponent.class);
      HitboxComponent hitbox = robot.getComponent(HitboxComponent.class);
      Filter f = collider.getFixture().getFilterData();

      assertEquals(PhysicsLayer.ENEMY, f.categoryBits, () -> type + ": wrong collider category");
      short expectedMask =
          (short) (PhysicsLayer.DEFAULT | PhysicsLayer.NPC | PhysicsLayer.OBSTACLE);
      assertEquals(expectedMask, f.maskBits, () -> type + ": wrong collider mask");

      assertEquals(PhysicsLayer.ENEMY, hitbox.getLayer(), () -> type + ": wrong hitbox layer");
    }
  }
}
