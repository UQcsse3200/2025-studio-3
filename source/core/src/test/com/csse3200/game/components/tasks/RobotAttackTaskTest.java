package com.csse3200.game.components.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.badlogic.gdx.utils.Array;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class RobotAttackTaskTest {
  private final short defenderLayer = 1;
  private Entity attacker;
  private Entity defender;
  private RobotAttackTask task;

  @BeforeEach
  void beforeEach() {
    RenderService renderService = new RenderService();
    renderService.setDebug(mock(DebugRenderer.class));
    ServiceLocator.registerRenderService(renderService);

    GameTime gameTime = mock(GameTime.class);
    when(gameTime.getDeltaTime()).thenReturn(0.2f);
    ServiceLocator.registerTimeSource(gameTime);

    ServiceLocator.registerPhysicsService(new PhysicsService());

    EntityService entityService = mock(EntityService.class);
    when(entityService.getEntities()).thenReturn(Array.with());
    ServiceLocator.registerEntityService(entityService);

    attacker = new Entity();
    defender = defender();
    task = new RobotAttackTask(90f, defenderLayer);
    AITaskComponent ai = new AITaskComponent();
    ai.addTask(task);
    attacker.addComponent(ai);

    Array<Entity> entities = Array.with(attacker, defender);
    when(ServiceLocator.getEntityService().getEntities()).thenReturn(entities);
    when(defender.getComponent(HitboxComponent.class).getLayer()).thenReturn(defenderLayer);
  }

  @Test
  void noFoundWhenNoEntity() {
    when(ServiceLocator.getEntityService().getEntities()).thenReturn(new Array<>());
    assertNull(task.getNearestVisibleTarget());
  }

  @Test
  void foundWhenInRange() {
    attacker.setPosition(10, 0);
    defender.setPosition(0, 0);
    assertEquals(defender, task.getNearestVisibleTarget());
  }

  @Test
  void noFoundWhenTargetNotMatchLayer() {
    when(defender.getComponent(HitboxComponent.class).getLayer())
        .thenReturn((short) (defenderLayer + 1));
    attacker.setPosition(10, 0);
    defender.setPosition(0, 0);
    assertNull(task.getNearestVisibleTarget());
  }

  @Test
  void noFoundWhenTargetNotInSameLane() {
    attacker.setPosition(10, 0);
    defender.setPosition(0, 1000);
    assertNull(task.getNearestVisibleTarget());
  }

  @Test
  void noFoundWhenAttackerNotInSameLane() {
    attacker.setPosition(10, 1000);
    defender.setPosition(0, 0);
    assertNull(task.getNearestVisibleTarget());
  }

  @Test
  void noFoundWhenTargetTooFarInX() {
    attacker.setPosition(0, 0);
    defender.setPosition(1000, 0);
    assertNull(task.getNearestVisibleTarget());
  }

  @Test
  void noFoundWhenAttackerTooFarInX() {
    attacker.setPosition(1000, 0);
    defender.setPosition(0, 0);
    assertNull(task.getNearestVisibleTarget());
  }

  private Entity defender() {
    Entity entity = new Entity();
    entity.addComponent(mock(HitboxComponent.class));
    return entity;
  }
}
