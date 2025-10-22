package com.csse3200.game.entities.factories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.ai.tasks.PriorityTask;
import com.csse3200.game.components.DefenderStatsComponent;
import com.csse3200.game.components.HitMarkerComponent;
import com.csse3200.game.components.npc.DefenceAnimationController;
import com.csse3200.game.components.tasks.AttackTask;
import com.csse3200.game.components.tasks.IdleTask;
import com.csse3200.game.components.tasks.TargetDetectionTasks;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.BaseDefenderConfig;
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
import com.csse3200.game.services.*;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefenceFactoryTest {
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

    // mock defender config
    mockConfigService = mock(ConfigService.class);

    // fake configs for each defender
    when(mockConfigService.getDefenderConfig("slingshooter"))
        .thenReturn(
            cfg(
                "slingshooter",
                50,
                500,
                "images/entities/defences/sling_shooter.atlas",
                "images/entities/defences/sling_shooter_1.png",
                "images/effects/sling_projectile.png",
                100,
                1,
                1,
                1,
                "right"));
    when(mockConfigService.getDefenderConfig("armyguy"))
        .thenReturn(
            cfg(
                "armyguy",
                80,
                1000,
                "images/entities/defences/machine_gun.atlas",
                "images/entities/defences/army_guy_1.png",
                "images/effects/bullet.png",
                100,
                1,
                1,
                0.5f,
                "right"));
    when(mockConfigService.getDefenderConfig("shadow"))
        .thenReturn(
            cfg(
                "shadow",
                20,
                250,
                "images/entities/defences/shadow.atlas",
                "images/entities/defences/shadow_idle1.png",
                "images/effects/shock.png",
                100,
                1,
                1,
                1.5f,
                "left"));
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear();
  }

  private static BaseDefenderConfig cfg(
      String name,
      int health,
      int range,
      String atlas,
      String asset,
      String projectile,
      int cost,
      int damage,
      float critChance,
      float attackSpeed,
      String direction) {
    BaseDefenderConfig c = new BaseDefenderConfig();
    try {
      java.lang.reflect.Field nameField =
          BaseDefenderConfig.class.getSuperclass().getDeclaredField("name");
      nameField.setAccessible(true);
      nameField.set(c, name);

      java.lang.reflect.Field healthField =
          BaseDefenderConfig.class.getSuperclass().getDeclaredField("health");
      healthField.setAccessible(true);
      healthField.set(c, health);

      java.lang.reflect.Field rangeField = BaseDefenderConfig.class.getDeclaredField("range");
      rangeField.setAccessible(true);
      rangeField.set(c, range);

      java.lang.reflect.Field atlasField =
          BaseDefenderConfig.class.getSuperclass().getDeclaredField("atlasPath");
      atlasField.setAccessible(true);
      atlasField.set(c, atlas);

      java.lang.reflect.Field assetField =
          BaseDefenderConfig.class.getSuperclass().getDeclaredField("assetPath");
      assetField.setAccessible(true);
      assetField.set(c, asset);

      java.lang.reflect.Field projectileField =
          BaseDefenderConfig.class.getDeclaredField("projectilePath");
      projectileField.setAccessible(true);
      projectileField.set(c, projectile);

      java.lang.reflect.Field costField = BaseDefenderConfig.class.getDeclaredField("cost");
      costField.setAccessible(true);
      costField.set(c, cost);

      java.lang.reflect.Field damageField = BaseDefenderConfig.class.getDeclaredField("damage");
      damageField.setAccessible(true);
      damageField.set(c, damage);

      java.lang.reflect.Field directionField =
          BaseDefenderConfig.class.getDeclaredField("direction");
      directionField.setAccessible(true);
      directionField.set(c, direction);

      java.lang.reflect.Field speedField = BaseDefenderConfig.class.getDeclaredField("attackSpeed");
      speedField.setAccessible(true);
      speedField.set(c, attackSpeed);

      java.lang.reflect.Field critField = BaseDefenderConfig.class.getDeclaredField("critChance");
      critField.setAccessible(true);
      critField.set(c, critChance);

    } catch (Exception e) {
      throw new RuntimeException("Failed to create test defender config", e);
    }
    return c;
  }

  @Test
  void testCreateDefenceUnitNotNull() {
    // create each defence entity
    for (String defense : new String[] {"slingshooter", "armyguy", "shadow"}) {
      Entity defender =
          DefenceFactory.createDefenceUnit(mockConfigService.getDefenderConfig(defense));
      assertNotNull(
          defender, () -> "DefenceFactory.createDefenceUnit(" + defense + ") returned null");
    }
  }

  @Test
  void testDefenceUnitHasPhysicsComponent() {
    for (String defense : new String[] {"slingshooter", "armyguy", "shadow"}) {
      Entity defender =
          DefenceFactory.createDefenceUnit(mockConfigService.getDefenderConfig(defense));
      assertNotNull(
          defender.getComponent(PhysicsComponent.class),
          () -> defense + ": missing PhysicsComponent");
    }
  }

  @Test
  void testDefenceUnitHasColliderComponent() {
    for (String defense : new String[] {"slingshooter", "armyguy", "shadow"}) {
      Entity defender =
          DefenceFactory.createDefenceUnit(mockConfigService.getDefenderConfig(defense));
      assertNotNull(
          defender.getComponent(ColliderComponent.class),
          () -> defense + ": missing ColliderComponent");
    }
  }

  @Test
  void testDefenceUnitHasHitBoxComponent() {
    for (String defense : new String[] {"slingshooter", "armyguy", "shadow"}) {
      Entity defender =
          DefenceFactory.createDefenceUnit(mockConfigService.getDefenderConfig(defense));
      assertNotNull(
          defender.getComponent(HitboxComponent.class),
          () -> defense + ": missing HitBoxComponent");
    }
  }

  @Test
  void testDefenceUnitHasDefenderStatsComponent() {
    for (String defense : new String[] {"slingshooter", "armyguy", "shadow"}) {
      Entity defender =
          DefenceFactory.createDefenceUnit(mockConfigService.getDefenderConfig(defense));
      assertNotNull(
          defender.getComponent(DefenderStatsComponent.class),
          () -> defense + ": missing DefenderStatsComponent");
    }
  }

  @Test
  void testDefenceUnitHasTasksComponent() {
    for (String defense : new String[] {"slingshooter", "armyguy", "shadow"}) {
      Entity defender =
          DefenceFactory.createDefenceUnit(mockConfigService.getDefenderConfig(defense));
      assertNotNull(
          defender.getComponent(AITaskComponent.class),
          () -> defense + ": missing AITasksComponent");
    }
  }

  @Test
  void testDefenceUnitHasAnimator() {
    for (String defense : new String[] {"slingshooter", "armyguy", "shadow"}) {
      Entity defender =
          DefenceFactory.createDefenceUnit(mockConfigService.getDefenderConfig(defense));
      assertNotNull(
          defender.getComponent(AnimationRenderComponent.class),
          () -> defense + ": missing Animator");
      assertNotNull(
          defender.getComponent(DefenceAnimationController.class),
          () -> defense + "missing DefenceAnimationController");
    }
  }

  @Test
  void testDefenceUnitHasHitMarkerComponent() {
    for (String defense : new String[] {"slingshooter", "armyguy", "shadow"}) {
      Entity defender =
          DefenceFactory.createDefenceUnit(mockConfigService.getDefenderConfig(defense));
      assertNotNull(
          defender.getComponent(HitMarkerComponent.class),
          () -> defense + ": missing HitMarkerComponent");
    }
  }

  @Test
  void testDefenderListenForHeal() {
    for (String defense : new String[] {"slingshooter", "armyguy", "shadow"}) {
      Entity defender =
          DefenceFactory.createDefenceUnit(mockConfigService.getDefenderConfig(defense));

      int oldHealth = defender.getComponent(DefenderStatsComponent.class).getHealth();
      defender.getEvents().trigger(HEAL);
      int newHealth = defender.getComponent(DefenderStatsComponent.class).getHealth();

      assertEquals(oldHealth + 20, newHealth, "Health should increase by 20 after healing");
    }
  }

  @Test
  void testGetTaskComponentLeft() {
    BaseDefenderConfig leftConfig = cfg("", 50, 200, "", "", "", 0, 20, 1, 1, "left");
    AITaskComponent tasksComponent = DefenceFactory.getTaskComponent(leftConfig);

    // OpenAI was used to help create these tests
    try {
      java.lang.reflect.Field priorityTasksField =
          AITaskComponent.class.getDeclaredField("priorityTasks");
      priorityTasksField.setAccessible(true);
      List<PriorityTask> tasks = (List<PriorityTask>) priorityTasksField.get(tasksComponent);

      assertEquals(2, tasks.size());
      assertTrue(tasks.get(0) instanceof AttackTask, "First task should be AttackTask");
      assertTrue(tasks.get(1) instanceof IdleTask, "Second task should be IdleTask");

      AttackTask attackTask = (AttackTask) tasks.get(0);
      IdleTask idleTask = (IdleTask) tasks.get(1);

      java.lang.reflect.Field attackRange =
          AttackTask.class.getSuperclass().getDeclaredField("attackRange");
      attackRange.setAccessible(true);
      java.lang.reflect.Field attackDirection =
          AttackTask.class.getSuperclass().getDeclaredField("direction");
      attackDirection.setAccessible(true);
      java.lang.reflect.Field attackSpeed = AttackTask.class.getDeclaredField("fireCooldown");
      attackSpeed.setAccessible(true);

      java.lang.reflect.Field idleRange =
          AttackTask.class.getSuperclass().getDeclaredField("attackRange");
      idleRange.setAccessible(true);
      java.lang.reflect.Field idleDirection =
          AttackTask.class.getSuperclass().getDeclaredField("direction");
      idleDirection.setAccessible(true);

      assertEquals(200f, attackRange.get(attackTask));
      assertEquals(TargetDetectionTasks.AttackDirection.LEFT, attackDirection.get(attackTask));

      assertEquals(200f, idleRange.get(idleTask));
      assertEquals(TargetDetectionTasks.AttackDirection.LEFT, idleDirection.get(idleTask));
    } catch (Exception e) {
      throw new RuntimeException("Task doesn't exist", e);
    }
  }

  @Test
  void testGetTaskComponentRight() {
    BaseDefenderConfig rightConfig = cfg("", 50, 200, "", "", "", 0, 20, 1, 1, "right");
    AITaskComponent tasksComponent = DefenceFactory.getTaskComponent(rightConfig);

    try {
      java.lang.reflect.Field priorityTasksField =
          AITaskComponent.class.getDeclaredField("priorityTasks");
      priorityTasksField.setAccessible(true);
      List<PriorityTask> tasks = (List<PriorityTask>) priorityTasksField.get(tasksComponent);

      assertEquals(2, tasks.size());
      assertTrue(tasks.get(0) instanceof AttackTask, "First task should be AttackTask");
      assertTrue(tasks.get(1) instanceof IdleTask, "Second task should be IdleTask");

      AttackTask attackTask = (AttackTask) tasks.get(0);
      IdleTask idleTask = (IdleTask) tasks.get(1);

      java.lang.reflect.Field attackRange =
          AttackTask.class.getSuperclass().getDeclaredField("attackRange");
      attackRange.setAccessible(true);
      java.lang.reflect.Field attackDirection =
          AttackTask.class.getSuperclass().getDeclaredField("direction");
      attackDirection.setAccessible(true);
      java.lang.reflect.Field attackSpeed = AttackTask.class.getDeclaredField("fireCooldown");
      attackSpeed.setAccessible(true);

      java.lang.reflect.Field idleRange =
          AttackTask.class.getSuperclass().getDeclaredField("attackRange");
      idleRange.setAccessible(true);
      java.lang.reflect.Field idleDirection =
          AttackTask.class.getSuperclass().getDeclaredField("direction");
      idleDirection.setAccessible(true);

      assertEquals(200f, attackRange.get(attackTask));
      assertEquals(TargetDetectionTasks.AttackDirection.RIGHT, attackDirection.get(attackTask));

      assertEquals(200f, idleRange.get(idleTask));
      assertEquals(TargetDetectionTasks.AttackDirection.RIGHT, idleDirection.get(idleTask));
    } catch (Exception e) {
      throw new RuntimeException("Task doesn't exist", e);
    }
  }

  @Test
  void testGetAnimation() {
    BaseDefenderConfig shadow =
        cfg(
            "",
            20,
            250,
            "images/entities/defences/shadow.atlas",
            "images/entities/defences/shadow_idle1.png",
            "images/effects/shock.png",
            100,
            1,
            1,
            1.5f,
            "left");
    AnimationRenderComponent animator = DefenceFactory.getAnimationComponent(shadow);
    assertNotNull(animator);
    assertTrue(animator.hasAnimation("idle"), "Should have idle animation");
    assertTrue(animator.hasAnimation("attack"), "Should have attack animation");
  }

  @Test
  void testCreateBaseDefender() {
    Entity baseDefender = DefenceFactory.createBaseDefender();
    baseDefender.create();

    assertNotNull(baseDefender, "Entity should not be null");

    assertNotNull(
        baseDefender.getComponent(ColliderComponent.class), "ColliderComponent should exist");
    assertNotNull(
        baseDefender.getComponent(PhysicsComponent.class), "PhysicsComponent should exist");
    assertNotNull(baseDefender.getComponent(HitboxComponent.class), "HitboxComponent should exist");
    assertNotNull(
        baseDefender.getComponent(HitMarkerComponent.class), "HitMarkerComponent should exist");

    PhysicsComponent physics = baseDefender.getComponent(PhysicsComponent.class);
    assertEquals(
        BodyDef.BodyType.StaticBody, physics.getBody().getType(), "Body type should be StaticBody");

    ColliderComponent collider = baseDefender.getComponent(ColliderComponent.class);
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

  @Test
  void testShellExplosionAssetsExist() {
    ResourceService resourceService = ServiceLocator.getResourceService();
    assertNotNull(resourceService, "ResourceService should be registered");

    // Verify that explosion atlas is loadable
    TextureAtlas explosionAtlas =
        resourceService.getAsset("images/effects/shell_explosion.atlas", TextureAtlas.class);
    assertNotNull(explosionAtlas, "Explosion atlas should be loaded");
  }
}
