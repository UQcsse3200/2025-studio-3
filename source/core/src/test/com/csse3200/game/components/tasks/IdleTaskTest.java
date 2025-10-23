package com.csse3200.game.components.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.events.EventHandler;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IdleTaskTest {
  private Entity target;

  @BeforeEach
  void setup() {
    RenderService renderService = new RenderService();
    renderService.setDebug(mock(DebugRenderer.class));
    ServiceLocator.registerRenderService(renderService);
    GameTime gameTime = mock(GameTime.class);
    when(gameTime.getDeltaTime()).thenReturn(20f / 1000);
    ServiceLocator.registerTimeSource(gameTime);
    ServiceLocator.registerPhysicsService(new PhysicsService());

    target = new Entity();
  }

  // test defender is idle when target out of range -> priority should be 1
  @Test
  void idleWhenOutOfRange() {
    float attackRange = 5f;
    float targetDistance = 10f; // out of range
    IdleTask idleTask =
        new IdleTask(attackRange, TargetDetectionTasks.AttackDirection.LEFT) {
          @Override
          protected Entity getNearestVisibleTarget() {
            return target;
          }
        };

    int priorityRunning = idleTask.getActivePriority(targetDistance, target);
    assertEquals(1, priorityRunning, "Idle task should keep running when target is out of range");

    int priorityStart = idleTask.getInactivePriority(targetDistance, target);
    assertEquals(1, priorityStart, "Expected to start idle task when target is out of range");
  }

  @Test
  void notIdleWhenInRange() {
    float attackRange = 5f;
    float targetDistance = 3f;
    IdleTask idleTask =
        new IdleTask(attackRange, TargetDetectionTasks.AttackDirection.LEFT) {
          @Override
          protected Entity getNearestVisibleTarget() {
            return target;
          }
        };

    int priority = idleTask.getActivePriority(targetDistance, target);
    assertEquals(-1, priority, "Idle task should not keep running when target is in range");

    int priorityStart = idleTask.getInactivePriority(targetDistance, target);
    assertEquals(-1, priorityStart, "Idle task should not start when target is in range");
  }

  @Test
  void updateShouldTriggerIdleStartEvent() {
    // Mock EventHandler
    EventHandler mockEventHandler = mock(EventHandler.class);

    // Create an entity and set mocked EventHandler
    Entity spyEntity = spy(new Entity());
    when(spyEntity.getEvents()).thenReturn(mockEventHandler);

    // Create IdleTask and mock its owner component
    IdleTask idleTask = new IdleTask(5f, TargetDetectionTasks.AttackDirection.LEFT);
    AITaskComponent aiTask = new AITaskComponent().addTask(idleTask);
    spyEntity.addComponent(aiTask);
    spyEntity.create();

    // Call update method
    idleTask.update();

    // Verify that "idleStart" was triggered once
    verify(mockEventHandler, times(1)).trigger("idleStart");
  }
}
