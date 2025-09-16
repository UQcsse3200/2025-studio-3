package com.csse3200.game.components.tasks;

import static org.junit.jupiter.api.Assertions.*;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TeleportTaskSimpleTest {

  @BeforeEach
  void setUp() {
    ServiceLocator.clear();
    // Fixed timestep so cooldowns are predictable
    ServiceLocator.registerTimeSource(
        new GameTime() {
          @Override
          public float getDeltaTime() {
            return 0.5f;
          } // 0.5s/frame
        });
  }

  @AfterEach
  void tearDown() {
    ServiceLocator.clear();
  }

  @Test
  void teleportsAfterCooldown() {
    Entity e = new Entity();
    e.setPosition(new Vector2(8f, 4f));
    float[] lanes = {2f, 4f, 6f};

    // cooldown=1s, chance=1 (always), unlimited
    TeleportTask tp = new TeleportTask(1f, 1f, 0, lanes);
    e.addComponent(tp);
    e.create();

    // After two updates (1.0s), teleport should trigger
    tp.update(); // 0.5s
    tp.update(); // 1.0s -> attempt

    float y = e.getPosition().y;
    assertNotEquals(4f, y, 1e-6, "Should switch to a different lane");
  }

  @Test
  void stopsAfterMaxTeleports() {
    Entity e = new Entity();
    e.setPosition(new Vector2(10f, 2f));
    float[] lanes = {1f, 2f, 3f, 4f, 5f};

    TeleportTask tp = new TeleportTask(0.5f, 1f, 1, lanes); // max 1 teleport
    e.addComponent(tp);
    e.create();

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

    TeleportTask tp = new TeleportTask(0.5f, 0f, 0, lanes); // chance=0 → never teleport
    e.addComponent(tp);
    e.create();

    for (int i = 0; i < 20; i++) tp.update();
    assertEquals(3f, e.getPosition().y, 1e-6, "Should stay in same lane with chance=0");
  }

  @Test
  void noTeleportWithSingleLane() {
    Entity e = new Entity();
    e.setPosition(new Vector2(5f, 4f));
    float[] lanes = {4f}; // < 2 lanes -> should never teleport

    TeleportTask tp = new TeleportTask(0.5f, 1f, 0, lanes);
    e.addComponent(tp);
    e.create();

    for (int i = 0; i < 20; i++) tp.update();
    assertEquals(4f, e.getPosition().y, 1e-6, "Should not teleport with fewer than 2 lanes");
  }

  @Test
  void maintainsXCoordinateOnTeleport() {
    Entity e = new Entity();
    e.setPosition(new Vector2(7f, 100f)); // start Y not in lanes => first teleport must change Y
    float[] lanes = {2f, 4f, 6f};

    TeleportTask tp = new TeleportTask(0.5f, 1f, 0, lanes); // chance=1 -> attempt on each window
    e.addComponent(tp);
    e.create();

    tp.update(); // triggers a teleport (cooldown=0.5s; dt=0.5s)
    assertEquals(7f, e.getPosition().x, 1e-6, "X must remain constant after teleport");
    assertNotEquals(100f, e.getPosition().y, 1e-6, "Y should change to one of the lane values");
  }

  @Test
  void chanceOneAttemptsOverMultipleWindowsYEndsInLaneSet() {
    Entity e = new Entity();
    e.setPosition(new Vector2(3f, 100f)); // start off-lane to guarantee first change
    float[] lanes = {1f, 2f, 3f, 4f};

    TeleportTask tp = new TeleportTask(0.5f, 1f, 0, lanes);
    e.addComponent(tp);
    e.create();

    // Run several cooldown windows; we don't assert change every window (avoid RNG flake),
    // but we do assert the final Y is one of the lane values.
    for (int i = 0; i < 6; i++) tp.update();

    float y = e.getPosition().y;
    boolean inSet = false;
    for (float ly : lanes)
      if (Math.abs(ly - y) <= 1e-6) {
        inSet = true;
        break;
      }
    assertTrue(inSet, "With chance=1 over multiple windows, Y should be one of the lane values");
  }
}
