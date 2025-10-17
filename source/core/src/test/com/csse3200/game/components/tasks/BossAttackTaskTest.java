package com.csse3200.game.components.tasks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link BossAttackTask}. This test class follows the structure and mocking
 * strategy of the {@code AttackTaskTest} by overriding target-finding methods.
 */
class BossAttackTaskTest {

  private Entity owner;
  private Entity target;
  private GameTime gameTime;
  private final float attackRange = 5f;
  private final float tileSize = 10f;

  @BeforeEach
  void setup() {

    RenderService renderService = new RenderService();
    renderService.setDebug(mock(DebugRenderer.class));
    ServiceLocator.registerRenderService(renderService);

    gameTime = mock(GameTime.class);
    when(gameTime.getDeltaTime()).thenReturn(0.1f);
    ServiceLocator.registerTimeSource(gameTime);

    ServiceLocator.registerPhysicsService(new PhysicsService());

    LevelGameArea levelGameArea = mock(LevelGameArea.class);
    when(levelGameArea.getTileSize()).thenReturn(tileSize);
    ServiceLocator.registerGameArea(levelGameArea);

    owner = new Entity();
    owner.setPosition(10f, 5f);

    target = new Entity().addComponent(new HitboxComponent());
    target.getComponent(HitboxComponent.class).setLayer(PhysicsLayer.NPC);
    target.setPosition(7f, 5f);
  }

  @Test
  void startTriggersAttackAndFireEvents() {
    BossAttackTask attackTask = new BossAttackTask(attackRange);
    AITaskComponent aiComponent = new AITaskComponent().addTask(attackTask);
    owner.addComponent(aiComponent);
    aiComponent.create();

    AtomicBoolean attackStarted = new AtomicBoolean(false);
    AtomicBoolean fired = new AtomicBoolean(false);
    owner.getEvents().addListener("attackStart", () -> attackStarted.set(true));
    owner.getEvents().addListener("fire", () -> fired.set(true));

    attackTask.start();

    assertTrue(attackStarted.get(), "attackStart should be triggered on start()");
    assertTrue(fired.get(), "fire should be triggered on start()");
  }

  @Test
  void attackWhenInRangeAndSameLane() {
    BossAttackTask attackTask =
        new BossAttackTask(attackRange) {
          @Override
          protected List<Entity> getAllTargets() {
            return List.of(target);
          }
        };
    AITaskComponent aiComponent = new AITaskComponent().addTask(attackTask);
    owner.addComponent(aiComponent);
    aiComponent.create();

    float distance = owner.getPosition().dst(target.getPosition());
    int priorityRunning = attackTask.getActivePriority(distance, target);
    int priorityStart = attackTask.getInactivePriority(distance, target);

    assertEquals(1, priorityRunning, "Task should remain active when target is valid");
    assertEquals(1, priorityStart, "Task should become active when target is valid");
  }

  @Test
  void noAttackWhenOutOfRange() {
    target.setPosition(2f, 5f);
    BossAttackTask attackTask =
        new BossAttackTask(attackRange) {
          @Override
          protected List<Entity> getAllTargets() {
            return List.of(target);
          }
        };
    AITaskComponent aiComponent = new AITaskComponent().addTask(attackTask);
    owner.addComponent(aiComponent);
    aiComponent.create();

    float distance = owner.getPosition().dst(target.getPosition());
    int priorityRunning = attackTask.getActivePriority(distance, target);
    int priorityStart = attackTask.getInactivePriority(distance, target);

    assertEquals(-1, priorityRunning, "Task should stop when target is out of range");
    assertEquals(-1, priorityStart, "Task should not start when target is out of range");
  }

  @Test
  void noAttackWhenInDifferentLane() {
    target.setPosition(7f, 20f); // Move target to a different lane
    BossAttackTask attackTask =
        new BossAttackTask(attackRange) {
          @Override
          protected List<Entity> getAllTargets() {
            return List.of(target);
          }
        };
    AITaskComponent aiComponent = new AITaskComponent().addTask(attackTask);
    owner.addComponent(aiComponent);
    aiComponent.create();

    float distance = owner.getPosition().dst(target.getPosition());
    int priorityRunning = attackTask.getActivePriority(distance, target);
    int priorityStart = attackTask.getInactivePriority(distance, target);

    assertEquals(-1, priorityRunning, "Task should stop when target is in a different lane");
    assertEquals(-1, priorityStart, "Task should not start when target is in a different lane");
  }

  @Test
  void firesAfterCooldown() {
    AtomicInteger fireCount = new AtomicInteger(0);
    owner.getEvents().addListener("fire", fireCount::incrementAndGet);

    BossAttackTask attackTask =
        new BossAttackTask(attackRange) {
          @Override
          protected List<Entity> getAllTargets() {
            return List.of(target);
          }
        };
    AITaskComponent aiComponent = new AITaskComponent().addTask(attackTask);
    owner.addComponent(aiComponent);
    aiComponent.create();

    attackTask.start();
    assertEquals(1, fireCount.get(), "Should fire once on start");

    when(gameTime.getDeltaTime()).thenReturn(0.5f);
    attackTask.update();
    assertEquals(1, fireCount.get(), "Should not fire before cooldown is met");

    attackTask.update();
    assertEquals(2, fireCount.get(), "Should fire again after cooldown");
  }

  @Test
  void shouldNotFireAtTargetBehind() {

    target.setPosition(12f, 5f); // Move target behind the owner

    AtomicBoolean fired = new AtomicBoolean(false);
    owner.getEvents().addListener("fire", () -> fired.set(true));

    BossAttackTask attackTask =
        new BossAttackTask(attackRange) {
          @Override
          protected List<Entity> getAllTargets() {
            return List.of(target);
          }
        };
    AITaskComponent aiComponent = new AITaskComponent().addTask(attackTask);
    owner.addComponent(aiComponent);
    aiComponent.create();

    attackTask.update();

    assertFalse(fired.get(), "Should not fire at a target that is behind the owner");
  }
}
