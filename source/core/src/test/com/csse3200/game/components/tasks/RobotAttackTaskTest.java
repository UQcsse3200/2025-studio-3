package com.csse3200.game.components.tasks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        task = new RobotAttackTask(1.5f, defenderLayer);
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
        assert (task.getNearestVisibleTarget() == null);
    }

    @Test
    void foundWhenInRange() {
        attacker.setPosition(0, 0);
        defender.setPosition(50, 0);
        assert (task.getNearestVisibleTarget() == defender);
    }

    @Test
    void noFoundWhenTargetNotMatchLayer() {
        when(defender.getComponent(HitboxComponent.class).getLayer()).thenReturn((short) (defenderLayer + 1));
        attacker.setPosition(0, 0);
        defender.setPosition(10, 0);
        assert (task.getNearestVisibleTarget() == null);
    }

    @Test
    void noFoundWhenTargetNotInSameLane() {
        attacker.setPosition(0, 0);
        defender.setPosition(10, 1000);
        assert (task.getNearestVisibleTarget() == null);
    }

    @Test
    void noFoundWhenAttackerNotInSameLane() {
        attacker.setPosition(0, 1000);
        defender.setPosition(10, 0);
        assert (task.getNearestVisibleTarget() == null);
    }

    @Test
    void noFoundWhenTargetTooFarInX() {
        attacker.setPosition(0, 0);
        defender.setPosition(1000, 0);
        assert (task.getNearestVisibleTarget() == null);
    }

    @Test
    void noFoundWhenAttackerTooFarInX() {
        attacker.setPosition(1000, 0);
        defender.setPosition(0, 0);
        assert (task.getNearestVisibleTarget() == null);
    }

    private Entity defender() {
        Entity entity = new Entity();
        entity.addComponent(mock(HitboxComponent.class));
        return entity;
    }

}
