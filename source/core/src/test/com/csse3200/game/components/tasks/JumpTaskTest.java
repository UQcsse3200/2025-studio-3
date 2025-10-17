package com.csse3200.game.components.tasks;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
public class JumpTaskTest {
  private Entity attacker;
  private Entity defender;

  @BeforeEach
  void setup() {
    GameTime gametime = mock(GameTime.class);
    ServiceLocator.registerTimeSource(gametime);
    when(ServiceLocator.getTimeSource().getDeltaTime()).thenReturn(0.4f);

    RenderService renderService = new RenderService();
    renderService.setDebug(mock(DebugRenderer.class));
    ServiceLocator.registerRenderService(renderService);

    EntityService entityService = mock(EntityService.class);
    ServiceLocator.registerEntityService(entityService);

    Body body = mock(Body.class);
    PhysicsComponent physicsComponent = mock(PhysicsComponent.class);
    when(physicsComponent.getBody()).thenReturn(body);
    when(body.getPosition()).thenReturn(new Vector2(0, 0));

    ServiceLocator.registerPhysicsService(new PhysicsService());

    attacker = new Entity();
    attacker.addComponent(physicsComponent);
    AITaskComponent ai = new AITaskComponent();
    attacker.addComponent(ai);

    defender = new Entity();
    HitboxComponent hitboxComponent = mock(HitboxComponent.class);
    defender.addComponent(hitboxComponent);
    when(hitboxComponent.getLayer()).thenReturn((short) 1);
  }

  @Test
  void lowPriorityWhenNoTarget() {
    JumpTask jumpTask =
        new JumpTask(30f, (short) 1) {
          @Override
          protected Entity getNearestVisibleTarget() {
            return null;
          }
        };
    assertTrue(jumpTask.getPriority() < 0, "Priority should be negative when there is no target");
  }

  @Test
  void highPriorityWhenTarget() {
    JumpTask jumpTask =
        new JumpTask(30f, (short) 1) {
          @Override
          protected Entity getNearestVisibleTarget() {
            return defender;
          }
        };
    assertTrue(
        jumpTask.getPriority() >= 100, "Priority should be very high when there is a target");
  }

  @Test
  void jumpEventTriggeredOnce() {
    AtomicInteger jumpCount = new AtomicInteger(0);
    JumpTask jumpTask =
        new JumpTask(30f, (short) 1) {
          @Override
          protected Entity getNearestVisibleTarget() {
            return defender;
          }
        };
    attacker.getComponent(AITaskComponent.class).addTask(jumpTask);
    attacker.getEvents().addListener("jumpStart", jumpCount::incrementAndGet);
    attacker.create();
    jumpTask.start();
    jumpTask.update();
    jumpTask.update();
    jumpTask.update();
    Assertions.assertEquals(1, jumpCount.get(), "Jump should only be executed once");
  }

  @Test
  void cannotJumpAgainAfterJumping() {
    JumpTask jumpTask =
        new JumpTask(30f, (short) 1) {
          @Override
          protected Entity getNearestVisibleTarget() {
            return defender;
          }
        };
    attacker.getComponent(AITaskComponent.class).addTask(jumpTask);
    attacker.create();
    jumpTask.start();
    // Simulate jump duration
    jumpTask.update();
    assertTrue(jumpTask.getPriority() > 100, "Started jumping, high priority");
    jumpTask.update();
    assertTrue(jumpTask.getPriority() > 100, "Still jumping, high priority");
    // Finish jump
    jumpTask.update();
    assertTrue(jumpTask.getPriority() < 0, "Jump finished, cannot jump again");
  }
}
