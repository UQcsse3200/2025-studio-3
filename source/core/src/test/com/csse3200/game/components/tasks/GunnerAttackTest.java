package com.csse3200.game.components.tasks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class GunnerAttackTaskTest {
  private Entity gunner;
  private Entity defense;
  private GameTime gameTime;

  @BeforeEach
  void setup() {
    // Mock time
    gameTime = mock(GameTime.class);
    ServiceLocator.registerTimeSource(gameTime);
    when(gameTime.getDeltaTime()).thenReturn(1f);

    // Register dummy render service to prevent NPE
    var mockRender = mock(com.csse3200.game.rendering.RenderService.class);
    when(mockRender.getDebug()).thenReturn(null);
    ServiceLocator.registerRenderService(mockRender);

    // Mock physics and entity services
    ServiceLocator.registerPhysicsService(new PhysicsService());
    EntityService entityService = mock(EntityService.class);
    ServiceLocator.registerEntityService(entityService);

    // Physics component, body, etc. (same as before)
    PhysicsComponent physicsComponent = mock(PhysicsComponent.class);
    Body body = mock(Body.class);
    when(physicsComponent.getBody()).thenReturn(body);
    when(body.getPosition()).thenReturn(new Vector2(5, 0));

    gunner = new Entity();
    gunner.addComponent(physicsComponent);
    AITaskComponent ai = new AITaskComponent();
    gunner.addComponent(ai);

    defense = new Entity();
    defense.setPosition(new Vector2(3, 0));

    when(entityService.getEntities())
        .thenReturn(new com.badlogic.gdx.utils.Array<>(new Entity[] {defense}));
  }

  @Test
  void lowPriorityWhenTargetNot() {
    // no visible target
    when(ServiceLocator.getEntityService().getEntities())
        .thenReturn(new com.badlogic.gdx.utils.Array<>());
    // set up gunner attack task
    GunnerAttackTask task = new GunnerAttackTask(5f, (short) 1);
    gunner.getComponent(AITaskComponent.class).addTask(task);
    gunner.create();
    assertTrue(task.getPriority() < 0, "Priority should be negative when there are no targets");
  }

  @Test
  void fireOnceCooldownReached() {
    AtomicInteger fireCount = new AtomicInteger(0); // count number of times fire is called
    // set up gunner attack task

    GunnerAttackTask task =
        new GunnerAttackTask(10f, (short) 1) {
          @Override
          protected Entity getNearestVisibleTarget() {
            return defense; // always have a target
          }
        };

    gunner.getComponent(AITaskComponent.class).addTask(task);
    gunner.getEvents().addListener("fire", fireCount::incrementAndGet);
    gunner.create();

    // update with delta < cooldown
    when(gameTime.getDeltaTime()).thenReturn(0.5f);
    task.update(); // time = 0.5
    task.update(); // time = 1 (should fire once)

    assertEquals(1, fireCount.get(), "Should fire exactly once after cooldown");
  }

  @Test
  void doesNotFireWithoutTarget() {
    AtomicInteger fireCount = new AtomicInteger(0); // count number of times fire is called
    // set up gunner attack task
    GunnerAttackTask task =
        new GunnerAttackTask(10f, (short) 1) {
          @Override
          protected Entity getNearestVisibleTarget() {
            return null; // no visible target
          }
        };
    // add task to gunner
    gunner.getComponent(AITaskComponent.class).addTask(task);
    gunner.getEvents().addListener("fire", fireCount::incrementAndGet);
    gunner.create();
    task.update();

    assertEquals(0, fireCount.get(), "Should not fire when no target is visible");
  }
}
