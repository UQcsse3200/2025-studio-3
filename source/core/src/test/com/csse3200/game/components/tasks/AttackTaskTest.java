package com.csse3200.game.components.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.areas.LevelGameArea; // <<< CHANGE HERE (1): ADD IMPORT
import com.csse3200.game.entities.Entity;
import com.csse3200.game.physics.PhysicsService;
import com.csse3200.game.rendering.DebugRenderer;
import com.csse3200.game.rendering.RenderService;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AttackTaskTest {
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

        // <<< CHANGE HERE (2): MOCK AND REGISTER THE GAME AREA
        // This is the fix. We create a mock LevelGameArea so the AttackTask's
        // isTargetInSameLane() method can work correctly.
        LevelGameArea gameArea = mock(LevelGameArea.class);
        when(gameArea.getTileSize()).thenReturn(1f); // Define a tile size
        ServiceLocator.registerGameArea(gameArea);
        // <<< END OF FIX

        target = new Entity();
        // Place target at origin for predictable distance checks
        target.setPosition(0, 0);
    }

    @Test
    void attackWhenInRange() {
        // AI was used to help create this method
        float attackRange = 5f;

        AttackTask attackTask =
                new AttackTask(attackRange) {
                    @Override
                    protected boolean isTargetVisible(Entity target) {
                        return true;
                    }

                    @Override
                    protected Entity getNearestVisibleTarget() {
                        return target;
                    }
                };

        // Set up defender entity and attach AI task component
        Entity defender = new Entity();
        defender.setPosition(0, 0); // Ensure it's in the same "lane"
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
    void noAttackWhenOutOfRange() {
        float attackRange = 5f;
        float targetDistance = 10f;
        AttackTask attackTask =
                new AttackTask(attackRange) {
                    @Override
                    protected Entity getNearestVisibleTarget() {
                        return target;
                    }
                };

        // Since the lane check will now pass, this test correctly isolates the distance check.
        int priority = attackTask.getActivePriority(targetDistance, target);
        assertEquals(-1, priority, "Attack task should stop when target is out of range");

        int priorityStart = attackTask.getInactivePriority(targetDistance, target);
        assertEquals(-1, priorityStart, "Attack task should not start when target is out of range");
    }

    @Test
    void startTriggersAttackStartAndFire() {
        float attackRange = 5f;
        AttackTask attackTask = new AttackTask(attackRange);

        Entity attacker = new Entity();
        AITaskComponent aiTaskComponent = new AITaskComponent();
        aiTaskComponent.addTask(attackTask);
        attacker.addComponent(aiTaskComponent);
        aiTaskComponent.create();

        AtomicBoolean attackStarted = new AtomicBoolean(false);
        AtomicBoolean fired = new AtomicBoolean(false);
        attacker.getEvents().addListener("attackStart", () -> attackStarted.set(true));
        attacker.getEvents().addListener("fire", () -> fired.set(true));

        attackTask.start();

        assertTrue(attackStarted.get(), "attackStart should be triggered on start()");
        assertTrue(fired.get(), "fire should be triggered on start()");
    }

    @Test
    void updateDoesNothingWithoutTarget() {
        AttackTask attackTask =
                new AttackTask(5f) {
                    @Override
                    protected Entity getNearestVisibleTarget() {
                        return null; // no target
                    }
                };

        Entity attacker = new Entity();
        AITaskComponent aiTaskComponent = new AITaskComponent();
        aiTaskComponent.addTask(attackTask);
        attacker.addComponent(aiTaskComponent);
        aiTaskComponent.create();

        AtomicBoolean fired = new AtomicBoolean(false);
        attacker.getEvents().addListener("fire", () -> fired.set(true));

        attackTask.update();

        assertFalse(fired.get(), "update() should not fire when no target is present");
    }

    @Test
    void firesAfterCooldown() {
        // Track number of times "fire" is triggered
        AtomicInteger fireCount = new AtomicInteger(0);

        // Mock and register GameTime
        GameTime gameTime = mock(GameTime.class);
        when(gameTime.getDeltaTime()).thenReturn(0f); // start with 0 delta
        ServiceLocator.registerTimeSource(gameTime);

        float attackRange = 5f;
        AttackTask attackTask =
                new AttackTask(attackRange) {
                    @Override
                    protected Entity getNearestVisibleTarget() {
                        return target;
                    }

                    @Override
                    protected float getDistanceToTarget() {
                        return 0f; // always in range
                    }
                };

        Entity defender = new Entity();
        AITaskComponent aiTaskComponent = new AITaskComponent();
        aiTaskComponent.addTask(attackTask);
        defender.addComponent(aiTaskComponent);
        aiTaskComponent.create();

        // Listen to "fire" events
        defender.getEvents().addListener("fire", fireCount::incrementAndGet);

        // Trigger start -> should fire immediately
        attackTask.start();
        assertEquals(1, fireCount.get(), "Should fire immediately on start");

        // Update with delta < cooldown -> should NOT fire
        // Cooldown is 0.95s, so 0.5s is not enough
        when(gameTime.getDeltaTime()).thenReturn(0.5f);
        attackTask.update();
        assertEquals(1, fireCount.get(), "Should not fire before cooldown");

        // Update with delta >= cooldown -> should fire
        when(gameTime.getDeltaTime()).thenReturn(1.0f); // 1.0s is >= 0.95s
        attackTask.update();
        assertEquals(2, fireCount.get(), "Should fire after cooldown");
    }

    @Test
    void firesMultipleTimesOverTime() {
        GameTime gameTime = ServiceLocator.getTimeSource();
        when(gameTime.getDeltaTime()).thenReturn(1f); // Each update call simulates 1 second

        AttackTask attackTask =
                new AttackTask(5f) {
                    @Override
                    protected Entity getNearestVisibleTarget() {
                        return target;
                    }

                    @Override
                    protected float getDistanceToTarget() {
                        return 1f; // within range
                    }
                };

        Entity attacker = new Entity();
        AITaskComponent aiTaskComponent = new AITaskComponent();
        aiTaskComponent.addTask(attackTask);
        attacker.addComponent(aiTaskComponent);
        aiTaskComponent.create();

        AtomicInteger fireCount = new AtomicInteger(0);
        attacker.getEvents().addListener("fire", fireCount::incrementAndGet);

        // Start the task to get the first shot and initialize the timer
        attackTask.start();
        assertEquals(1, fireCount.get());

        // Cooldown is 0.95s. We simulate 4 updates at 1s intervals.
        // This should result in 4 more shots, for a total of 5.
        for (int i = 0; i < 4; i++) {
            attackTask.update();
        }

        assertEquals(5, fireCount.get(), "Should have fired 5 times (1 on start + 4 from updates)");
    }
}