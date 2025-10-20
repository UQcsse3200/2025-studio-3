package com.csse3200.game.components.tasks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.ai.tasks.TaskRunner;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.EntityService;
import com.csse3200.game.physics.PhysicsEngine;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.physics.components.HitboxComponent;
import com.csse3200.game.physics.raycast.RaycastHit;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyShort;
import static org.mockito.Mockito.*;

public class TargetDetectionTasksTest {

    private Entity defender;
    private TestTargetDetectionTasks task;
    private PhysicsEngine mockPhysics;

    /**
     * Concrete implementation of abstract class for testing.
     */
    private static class TestTargetDetectionTasks extends TargetDetectionTasks {
        public TestTargetDetectionTasks(float attackRange, AttackDirection direction) {
            super(attackRange, direction);
        }

        @Override
        protected int getActivePriority(float distance, Entity target) {
            return (distance < attackRange && target != null) ? 1 : -1;
        }

        @Override
        protected int getInactivePriority(float distance, Entity target) {
            return (distance < attackRange && target != null) ? 1 : -1;
        }
    }

    @BeforeEach
    void setup() {
        // Mock physics and services
        mockPhysics = mock(PhysicsEngine.class);
        PhysicsService physicsService = mock(PhysicsService.class);
        when(physicsService.getPhysics()).thenReturn(mockPhysics);
        ServiceLocator.registerPhysicsService(physicsService);

        LevelGameArea gameArea = mock(LevelGameArea.class);
        when(gameArea.getTileSize()).thenReturn(1f);
        ServiceLocator.registerGameArea(gameArea);

        EntityService entityService = new EntityService();
        ServiceLocator.registerEntityService(entityService);

        GameTime gameTime = mock(GameTime.class);
        when(gameTime.getDeltaTime()).thenReturn(20f / 1000);
        ServiceLocator.registerTimeSource(gameTime);


        task = new TestTargetDetectionTasks(5f, TargetDetectionTasks.AttackDirection.RIGHT);

        defender = mock(Entity.class);
        when(defender.getCenterPosition()).thenReturn(new Vector2(0, 0));
        when(defender.getPosition()).thenReturn(new Vector2(0, 0));
        ServiceLocator.getEntityService().register(defender);

        AITaskComponent aiTaskComponent = new AITaskComponent().addTask(task);
        defender.addComponent(aiTaskComponent);

        TaskRunner taskRunner = spy(TaskRunner.class);
        when(taskRunner.getEntity()).thenReturn(defender);


    }

    @Test
    void getAllTargetsTest() {
        Entity target = mock(Entity.class);
        when(target.getPosition()).thenReturn(new Vector2(3f, 0));
        ServiceLocator.getEntityService().register(target);

        target.addComponent(new HitboxComponent().setLayer(PhysicsLayer.ENEMY));

        List<Entity> targets = task.getAllTargets();
        assertTrue(targets.contains(target), "Should find enemy entity");
        assertFalse(targets.contains(defender), "Should not include self");
    }

    @Test
    void getNearestVisibleTargetTestFindsEntity() {
        Entity target = mock(Entity.class);
        when(target.getPosition()).thenReturn(new Vector2(3f, 0));
        ServiceLocator.getEntityService().register(target);

        RaycastHit hit = new RaycastHit();
        Fixture fixture = mock(Fixture.class);
        when(fixture.getUserData()).thenReturn(target);
        hit.setFixture(fixture); // mock hitting target entity

        when(mockPhysics.raycast(any(), any(), anyShort(), any(RaycastHit.class)))
                .then(invocation -> {
                    RaycastHit arg = invocation.getArgument(3);
                    arg.setFixture(fixture);
                    return true;
                });

        Entity result = task.getNearestVisibleTarget();
        assertNotNull(result, "Should detect entity from raycast");
    }

    @Test
    void getDistanceToTargetTestFindsEntity() {
        Entity target = mock(Entity.class);
        when(target.getPosition()).thenReturn(new Vector2(3f, 0));
        ServiceLocator.getEntityService().register(target);

        Entity closest = task.getNearestVisibleTarget();
        assertEquals(3,  defender.getPosition().dst(closest.getPosition()), "Distance to target should be 3f");
    }

    @Test
    void getDistanceToTargetTestFindsNoTarget() {
        Entity target = mock(Entity.class);
        when(target.getPosition()).thenReturn(new Vector2(10f, 0)); // out of range
        ServiceLocator.getEntityService().register(target);

        Entity closest = task.getNearestVisibleTarget();
        assertEquals(0,  defender.getPosition().dst(closest.getPosition()), "Distance to target should be 0f");
    }

    @Test
    void testGetDistanceToTargetReturnsMaxWhenNoTarget() {
        TargetDetectionTasks spyTask = spy(task);
        doReturn(null).when(spyTask).getNearestVisibleTarget();

        float dist = spyTask.getDistanceToTarget();
        assertEquals(Float.MAX_VALUE, dist, "Should return max float when no target");
    }

   @Test
    void getPriorityTestActive() {
       Entity target = mock(Entity.class);
       when(target.getPosition()).thenReturn(new Vector2(3f, 0));
       ServiceLocator.getEntityService().register(target);


   }

    @Test
    void getPriorityTestInactive() {
        Entity target = mock(Entity.class);
        when(target.getPosition()).thenReturn(new Vector2(10f, 0)); // out of range
        ServiceLocator.getEntityService().register(target);
    }

}
