package com.csse3200.game.components.tasks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.ai.tasks.Task;
import com.csse3200.game.ai.tasks.TaskRunner;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.components.CombatStatsComponent;
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


import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TargetDetectionTasksTest {

    /**
     * Concrete implementation of abstract class for testing.
     */
    private static class TestTargetDetectionTasks extends TargetDetectionTasks {
        public TestTargetDetectionTasks(float attackRange, AttackDirection direction) {
            super(attackRange, direction);
        }

        @Override
        protected int getActivePriority(float dst, Entity target) { // from AttackTask
            if (target == null) {
                return -1; // stop task if no target
            }
            if (dst > attackRange) {
                return -1; // stop task when target not visible, out of range, or not in the same lane
            }
            return 1;
        }

        @Override
        protected int getInactivePriority(float dst, Entity target) { // from AttackTask
            if (target == null) {
                return -1;
            }
            if (dst <= attackRange) {
                return 1; // start task if target is visible, in range, and in the same lane
            }
            return -1;
        }
    }

    Entity defender;
    TestTargetDetectionTasks targetTask;
    PhysicsEngine physicsEngine;

    @BeforeEach
    void setup() {
        RenderService renderService = new RenderService();
        renderService.setDebug(mock(DebugRenderer.class));
        ServiceLocator.registerRenderService(renderService);
        GameTime gameTime = mock(GameTime.class);
        when(gameTime.getDeltaTime()).thenReturn(20f / 1000);
        ServiceLocator.registerTimeSource(gameTime);

        PhysicsService physicsService = mock(PhysicsService.class);
        physicsEngine = mock(PhysicsEngine.class);
        ServiceLocator.registerPhysicsService(physicsService);
        when(physicsService.getPhysics()).thenReturn(physicsEngine);

        LevelGameArea gameArea = mock(LevelGameArea.class);
        when(gameArea.getTileSize()).thenReturn(1f); // Define a tile size
        ServiceLocator.registerGameArea(gameArea);

        defender = mock(Entity.class);
        defender.setPosition(0, 0);

        targetTask = spy(new TestTargetDetectionTasks(10, TargetDetectionTasks.AttackDirection.RIGHT));
        AITaskComponent aiTask = new AITaskComponent().addTask(targetTask);
        defender.addComponent(aiTask);

        when(defender.getPosition()).thenReturn(new Vector2(0, 0));
        when(defender.getCenterPosition()).thenReturn(new Vector2(0, 0));

        TaskRunner taskRunner = mock(TaskRunner.class);
        when(taskRunner.getEntity()).thenReturn(defender);

        targetTask.create(taskRunner);
    }

    @Test
    void constructorTest() {
        TestTargetDetectionTasks task = new TestTargetDetectionTasks(5f, TargetDetectionTasks.AttackDirection.LEFT);

        assertEquals(5f, task.attackRange);
        assertEquals(TargetDetectionTasks.AttackDirection.LEFT, task.direction);
        assertNotNull(task.physics);
    }

    @Test
    void getDistanceToTargetNullTest() {
        doReturn(null).when(targetTask).getNearestVisibleTarget();
        float distance = targetTask.getDistanceToTarget();
        assertEquals(Float.MAX_VALUE, distance);
    }

    @Test
    void getDistanceToTargetTest() {
        Entity target = mock(Entity.class);
        when(target.getCenterPosition()).thenReturn(new Vector2(5f, 0f));

        doReturn(target).when(targetTask).getNearestVisibleTarget();

        float distance = targetTask.getDistanceToTarget();

        assertEquals(5f, distance, "Distance between target and entity should be 5");
    }

    @Test
    void getPriorityActiveTest() {
        try {
            java.lang.reflect.Field status = TargetDetectionTasks.class.getSuperclass().getDeclaredField("status");
            status.setAccessible(true);
            status.set(targetTask, Task.Status.ACTIVE);
        } catch (Exception e) {
            throw new RuntimeException("Status field not found");
        }

        // in range
        Entity target = mock(Entity.class);
        when(target.getCenterPosition()).thenReturn(new Vector2(5f, 0f));

        doReturn(target).when(targetTask).getNearestVisibleTarget();

        int priority = targetTask.getPriority();
        assertEquals(1, priority);

        // out of range
        Entity targetOut = mock(Entity.class);
        when(targetOut.getCenterPosition()).thenReturn(new Vector2(15f, 0f));
        doReturn(targetOut).when(targetTask).getNearestVisibleTarget();

        int priorityOut = targetTask.getPriority();
        assertEquals(-1, priorityOut);
    }

    @Test
    void getPriorityInactiveTest() {
        try {
            java.lang.reflect.Field status = TargetDetectionTasks.class.getSuperclass().getDeclaredField("status");
            status.setAccessible(true);
            status.set(targetTask, Task.Status.INACTIVE);
        } catch (Exception e) {
            throw new RuntimeException("Status field not found");
        }

        // in range
        Entity target = mock(Entity.class);
        when(target.getCenterPosition()).thenReturn(new Vector2(5f, 0f));
        doReturn(target).when(targetTask).getNearestVisibleTarget();

        int priority = targetTask.getPriority();
        assertEquals(1, priority);


        // out of range
        Entity targetOut = mock(Entity.class);
        when(targetOut.getCenterPosition()).thenReturn(new Vector2(15f, 0f));
        doReturn(targetOut).when(targetTask).getNearestVisibleTarget();

        int priorityOut = targetTask.getPriority();
        assertEquals(-1, priorityOut);
    }


    @Test
    void getNearestVisibleTargetTest() {
        Entity target = spy(new Entity());

        // Mock a Fixture that returns the enemy as user data
        Fixture fixture = mock(Fixture.class);
        when(fixture.getUserData()).thenReturn(target);


        // Configure the raycast to hit ONLY on the first call
        // and avoid executing the second raycast
        doAnswer(invocation -> {
            RaycastHit hitArg = invocation.getArgument(3);
            hitArg.setFixture(fixture);
            return true;  // Simulate a successful raycast
        }).when(physicsEngine).raycast(any(), any(), anyShort(), any());


        Entity result = targetTask.getNearestVisibleTarget();

        // hits a target
        assertNotNull(result, "Expected to detect a target entity");
        assertEquals(target, result, "Expected to return the hit enemy entity");

    }

    @Test
    void getNearestVisibleTargetNoHitTest() {
        when(physicsEngine.raycast(any(), any(), anyShort(), any())).thenReturn(false);

        Entity result = targetTask.getNearestVisibleTarget();
        // no near target found
        assertNull(result, "No entity detected should return null");
    }

    @Test
    void getAllTargetsTest() {
        Entity entity1 = mock(Entity.class);
        Entity entity2 = mock(Entity.class);
        Entity entity3 = mock(Entity.class);
        Entity entity4 = mock(Entity.class);

        HitboxComponent hitboxEnemy = mock(HitboxComponent.class);
        HitboxComponent hitboxBoss = mock(HitboxComponent.class);
        HitboxComponent hitboxOther = mock(HitboxComponent.class);
        CombatStatsComponent combatStats = mock(CombatStatsComponent.class);

        when(entity1.getComponent(HitboxComponent.class)).thenReturn(hitboxEnemy);
        when(entity1.getComponent(CombatStatsComponent.class)).thenReturn(combatStats);
        when(hitboxEnemy.getLayer()).thenReturn(PhysicsLayer.ENEMY);

        when(entity2.getComponent(HitboxComponent.class)).thenReturn(hitboxBoss);
        when(entity2.getComponent(CombatStatsComponent.class)).thenReturn(combatStats);
        when(hitboxBoss.getLayer()).thenReturn(PhysicsLayer.BOSS);

        when(entity3.getComponent(HitboxComponent.class)).thenReturn(hitboxOther);
        when(entity3.getComponent(CombatStatsComponent.class)).thenReturn(combatStats);
        when(hitboxOther.getLayer()).thenReturn(PhysicsLayer.NPC); // wrong layer

        when(entity4.getComponent(HitboxComponent.class)).thenReturn(hitboxEnemy);
        when(entity4.getComponent(CombatStatsComponent.class)).thenReturn(null); // no CombatStatsComponent
        when(hitboxEnemy.getLayer()).thenReturn(PhysicsLayer.ENEMY);

        EntityService entityServiceMock = mock(EntityService.class);
        ServiceLocator.registerEntityService(entityServiceMock);

        Array<Entity> entities = new Array<>(false, 16);
        entities.add(entity1);
        entities.add(entity2);
        entities.add(entity3);
        entities.add(entity4);
        when(entityServiceMock.getEntities()).thenReturn(entities);


        List<Entity> targets = targetTask.getAllTargets();

        assertEquals(2, targets.size(), "Only valid targets should have been returned");
        assertTrue(targets.contains(entity1), "Enemy target with hitbox and combat stats is valid");
        assertTrue(targets.contains(entity2), "Boss with hitbox and combat stats is valid");
        assertFalse(targets.contains(entity3), "Defense entity is not a valid target");
        assertFalse(targets.contains(entity4), "Enemy with no combat stats is not valid");
    }
}
