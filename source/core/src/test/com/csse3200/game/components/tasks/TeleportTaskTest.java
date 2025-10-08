package com.csse3200.game.components.tasks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class TeleportTaskSimpleTest {
    private GameTime gameTime;

    @BeforeEach
    void setUp() {
        ServiceLocator.clear();

        // Fixed timestep so cooldowns are predictable
        gameTime = mock(GameTime.class);
        ServiceLocator.registerTimeSource(gameTime);
        when(gameTime.getDeltaTime()).thenReturn(0.5f); // 0.5s/frame
    }

    @AfterEach
    void tearDown() {
        ServiceLocator.clear();
    }

    /** Helper: attach TeleportTask via AITaskComponent */
    private TeleportTask attachTeleportTask(Entity e, TeleportTask task) {
        AITaskComponent ai = new AITaskComponent().addTask(task);
        e.addComponent(ai);
        e.create();
        e.update(); // Let the AI component select and start the task
        return task;
    }

    @Test
    void teleportsAfterCooldown() {
        Entity e = new Entity();
        e.setPosition(new Vector2(8f, 4f));
        float[] lanes = {2f, 4f, 6f};

        TeleportTask tp = attachTeleportTask(e, new TeleportTask(1f, 1f, 0, lanes));

        // After two updates (1.0s), teleport should trigger
        tp.update(); // 0.5s
        tp.update(); // 1.0s → attempt

        float y = e.getPosition().y;
        assertNotEquals(4f, y, 1e-6, "Should switch to a different lane");
    }

    @Test
    void stopsAfterMaxTeleports() {
        Entity e = new Entity();
        e.setPosition(new Vector2(10f, 2f));
        float[] lanes = {1f, 2f, 3f, 4f, 5f};

        TeleportTask tp = attachTeleportTask(e, new TeleportTask(0.5f, 1f, 1, lanes)); // max 1 teleport

        // Let it teleport once
        for (int i = 0; i < 2; i++) tp.update();
        float afterFirst = e.getPosition().y;

        // More updates should not change lane again
        for (int i = 0; i < 10; i++) tp.update();
        assertEquals(afterFirst, e.getPosition().y, 1e-6, "Should not teleport more than maxTeleports");
    }

    @Test
    void doesNotTeleportWhenChanceZero() {
        Entity e = new Entity();
        e.setPosition(new Vector2(10f, 3f));
        float[] lanes = {1f, 2f, 3f, 4f, 5f};

        TeleportTask tp = attachTeleportTask(e, new TeleportTask(0.5f, 0f, 0, lanes)); // chance=0

        for (int i = 0; i < 20; i++) tp.update();
        assertEquals(3f, e.getPosition().y, 1e-6, "Should stay in same lane with chance=0");
    }

    @Test
    void noTeleportWithSingleLane() {
        Entity e = new Entity();
        e.setPosition(new Vector2(5f, 4f));
        float[] lanes = {4f}; // < 2 lanes -> should never teleport

        TeleportTask tp = attachTeleportTask(e, new TeleportTask(0.5f, 1f, 0, lanes));

        for (int i = 0; i < 20; i++) tp.update();
        assertEquals(4f, e.getPosition().y, 1e-6, "Should not teleport with fewer than 2 lanes");
    }

    @Test
    void maintainsXCoordinateOnTeleport() {
        Entity e = new Entity();
        e.setPosition(new Vector2(7f, 100f)); // start Y not in lanes
        float[] lanes = {2f, 4f, 6f};

        TeleportTask tp = attachTeleportTask(e, new TeleportTask(0.5f, 1f, 0, lanes));

        tp.update(); // triggers teleport (cooldown=0.5s; dt=0.5s)

        assertEquals(7f, e.getPosition().x, 1e-6, "X must remain constant after teleport");
        assertNotEquals(100f, e.getPosition().y, 1e-6, "Y should change to one of the lane values");
    }

    @Test
    void chanceOneAttemptsOverMultipleWindowsYEndsInLaneSet() {
        Entity e = new Entity();
        e.setPosition(new Vector2(3f, 100f)); // start off-lane
        float[] lanes = {1f, 2f, 3f, 4f};

        TeleportTask tp = attachTeleportTask(e, new TeleportTask(0.5f, 1f, 0, lanes));

        // Run several cooldown windows
        for (int i = 0; i < 6; i++) tp.update();

        float y = e.getPosition().y;
        boolean inSet = false;
        for (float ly : lanes) {
            if (Math.abs(ly - y) <= 1e-6) {
                inSet = true;
                break;
            }
        }
        assertTrue(inSet, "With chance=1 over multiple windows, Y should be one of the lane values");
    }
}