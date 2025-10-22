package com.csse3200.game.components.tasks;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.ai.tasks.AITaskComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.extensions.GameExtension;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(GameExtension.class)
class TeleportTaskTest {
  private GameTime gameTime;

  @BeforeEach
  void setUp() {
    ServiceLocator.clear();

    // 🔸 Fix the seed so random() behaves deterministically
    MathUtils.random.setSeed(12345L);

    // Fixed timestep so cooldowns are predictable
    gameTime = mock(GameTime.class);
    ServiceLocator.registerTimeSource(gameTime);

    ResourceService mockResourceService = mock(ResourceService.class);
    ServiceLocator.registerResourceService(mockResourceService);
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

    TeleportTask tp = new TeleportTask(1f, 1f, 0, lanes);
    AITaskComponent ai = new AITaskComponent().addTask(tp);
    e.addComponent(ai);
    e.create();
    tp.start();

    // Simulate a few frames to cover cooldown + animation + teleport
    for (int i = 0; i < 5; i++) {
      tp.getPriority();
      tp.update();
    }

    float y = e.getPosition().y;
    assertNotEquals(
        4f,
        y,
        1e-6,
        "Should have switched to a different lane after animation and teleport phases");
  }

  @Test
  void stopsAfterMaxTeleports() {
    Entity e = new Entity();
    e.setPosition(new Vector2(10f, 2f));
    float[] lanes = {1f, 2f};

    TeleportTask tp = attachTeleportTask(e, new TeleportTask(0.5f, 1f, 1, lanes));
    tp.start();

    // First teleport cycle (cooldown + animation + teleport)
    for (int i = 0; i < 6; i++) { // 👈 increased to cover teleportAnimTime fully
      tp.getPriority();
      tp.update();
    }
    float afterFirst = e.getPosition().y;

    // Run more frames — should NOT teleport again
    for (int i = 0; i < 10; i++) {
      tp.getPriority();
      tp.update();
    }

    assertEquals(
        afterFirst,
        e.getPosition().y,
        1e-6,
        "Should have teleported once and stopped after reaching maxTeleports");
  }

  @Test
  void noTeleportWithSingleLane() {
    Entity e = new Entity();
    e.setPosition(new Vector2(5f, 4f));
    float[] lanes = {4f}; // single lane

    TeleportTask tp = attachTeleportTask(e, new TeleportTask(0.5f, 1f, 0, lanes));
    tp.start();

    for (int i = 0; i < 20; i++) {
      tp.getPriority();
      tp.update();
    }

    assertEquals(4f, e.getPosition().y, 1e-6, "Should not teleport with fewer than 2 lanes");
  }

  @Test
  void maintainsXCoordinateOnTeleport() {
    Entity e = new Entity();
    e.setPosition(new Vector2(7f, 100f));
    float[] lanes = {2f, 4f, 6f};

    TeleportTask tp = attachTeleportTask(e, new TeleportTask(0.5f, 1f, 0, lanes));
    tp.start();

    // Simulate several frames to cover cooldown + animation + teleport phases
    for (int i = 0; i < 6; i++) {
      tp.getPriority();
      tp.update();
    }

    assertEquals(7f, e.getPosition().x, 1e-6, "X must remain constant after teleport");
    assertNotEquals(100f, e.getPosition().y, 1e-6, "Y should change to one of the lane values");
  }

  @Test
  void chanceOneAttemptsOverMultipleWindowsYEndsInLaneSet() {
    Entity e = new Entity();
    e.setPosition(new Vector2(3f, 100f)); // start off-lane
    float[] lanes = {1f, 2f, 3f, 4f};

    TeleportTask tp = attachTeleportTask(e, new TeleportTask(0.5f, 1f, 0, lanes));
    tp.start();

    // Simulate several teleport cycles (cooldown + animation + teleport)
    for (int i = 0; i < 12; i++) {
      tp.getPriority();
      tp.update();
    }

    float y = e.getPosition().y;
    boolean inSet = false;
    for (float ly : lanes) {
      if (Math.abs(ly - y) <= 1e-6) {
        inSet = true;
        break;
      }
    }

    assertTrue(
        inSet,
        "With chance=1 over multiple teleport windows, Y should end up on one of the lane values");
  }
}
