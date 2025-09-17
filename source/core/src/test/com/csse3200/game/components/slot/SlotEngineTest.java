package com.csse3200.game.components.slot;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Minimal tests for {@link SlotEngine}. These tests avoid any asset dependency and focus on core
 * behavior.
 */
class SlotEngineTest {

  private static boolean allEqual(int[] arr) {
    if (arr == null || arr.length == 0) return false;
    int v = arr[0];
    for (int i = 1; i < arr.length; i++) if (arr[i] != v) return false;
    return true;
  }

  @Test
  @DisplayName("With probability=1.0 every spin is a triggered triple and carries an Effect")
  void alwaysTriggers() {
    SlotEngine.SlotConfig cfg = new SlotEngine.SlotConfig();
    cfg.setTriggerProbability(1.0);
    // Use a fixed seed for determinism.
    SlotEngine engine = new SlotEngine(cfg, new Random(1234));

    for (int i = 0; i < 20; i++) {
      SlotEngine.SpinResult r = engine.spin();
      assertTrue(r.isEffectTriggered(), "Effect should be triggered");
      assertTrue(r.getEffect().isPresent(), "Effect should be present");
      assertTrue(allEqual(r.getReels()), "Triggered reels must be all equal (triple)");
      int effectId = r.getEffect().get().getId();
      for (int v : r.getReels()) assertEquals(effectId, v, "Reel value must equal effect id");
    }
  }

  @Test
  @DisplayName("With probability=0.0 never triggers and reels are not a triple")
  void neverTriggers() {
    SlotEngine.SlotConfig cfg = new SlotEngine.SlotConfig();
    cfg.setTriggerProbability(0.0);
    SlotEngine engine = new SlotEngine(cfg, new Random(5678));

    for (int i = 0; i < 20; i++) {
      SlotEngine.SpinResult r = engine.spin();
      assertFalse(r.isEffectTriggered(), "Effect should not be triggered");
      assertTrue(r.getEffect().isEmpty(), "Effect should be empty");
      assertFalse(allEqual(r.getReels()), "Reels should not be a triple when not triggered");
    }
  }
}
