package com.csse3200.game.components.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AttackTaskTest {
  private Entity target;
  private List<Entity> targets;

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
    targets = new ArrayList<>();
    targets.add(target);
  }

  @Test
  public void attackWhenInRange() {
    // AI was used to help create this method
    float attackRange = 5f;

    AttackTask attackTask =
        new AttackTask(targets, attackRange) {
          @Override
          protected boolean isTargetVisible(Entity target) {
            return true;
          }
        };

    // Set up defender entity and attach AI task component
    Entity defender = new Entity();
    AITaskComponent aiTaskComponent = new AITaskComponent();
    aiTaskComponent.addTask(attackTask);
    defender.addComponent(aiTaskComponent);

    // Manually trigger the setup that assigns owner to the task
    aiTaskComponent.create();

    float distance = target.getPosition().dst(defender.getPosition());

    int priorityRunning = attackTask.getActivePriority(distance, target);
    assertEquals(1, priorityRunning, "Attack task should keep running when target is in range");

    int priorityStart = attackTask.getInactivePriority(distance, target);
    assertEquals(1, priorityStart, "Attack task should start when target is in range");
  }

  @Test
  public void noAttackWhenOutOfRange() {
    float attackRange = 5f;
    float targetDistance = 10f;
    AttackTask attackTask = new AttackTask(targets, attackRange);

    int priority = attackTask.getActivePriority(targetDistance, target);
    assertEquals(-1, priority, "Attack task should stop when target is out of range");

    int priorityStart = attackTask.getInactivePriority(targetDistance, target);
    assertEquals(-1, priorityStart, "Attack task should not start when target is out of range");
  }
}
