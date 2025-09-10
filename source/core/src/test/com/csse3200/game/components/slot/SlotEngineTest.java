package com.csse3200.game.components.slot;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link SlotEngine}.
 *
 * <p>This test class contains two smoke tests: 1. EachEffectOnce - ensure all effect ids (0..7) can
 * be generated at least once. 2. NeverTriggers - ensure that when probability=0.0, no effect is
 * triggered and reels are not triple.
 */
public class SlotEngineTest {

  /** Helper: check if reels form a triple (all three values equal). */
  private static boolean isTriple(int[] reels) {
    return reels != null && reels.length == 3 && reels[0] == reels[1] && reels[1] == reels[2];
  }

  /**
   * Test that every effect id (0..7) can be generated at least once. Force triggerProbability=1.0
   * so that every spin must trigger.
   */
  @Test
  @DisplayName("Each effect id (0..7) appears at least once and returns correct triple")
  void testEachEffectOnce() {
    SlotEngine.SlotConfig cfg = new SlotEngine.SlotConfig();
    cfg.setTriggerProbability(1.0); // force trigger every spin

    SlotEngine engine = new SlotEngine(cfg, new Random(42));

    boolean[] seen = new boolean[8];
    int attempts = 0;

    // Run up to 200 spins to cover all 8 ids
    while (attempts < 200 && !allSeen(seen)) {
      SlotEngine.SpinResult r = engine.spin();
      assertTrue(r.isEffectTriggered(), "Effect should be triggered");

      int effectId = r.getEffect().get().getId();
      int[] reels = r.getReels();

      // Validate reels are triple of the chosen id
      assertEquals(effectId, reels[0]);
      assertEquals(effectId, reels[1]);
      assertEquals(effectId, reels[2]);

      seen[effectId] = true;
      attempts++;
    }

    // Assert that all effect ids 0..7 were seen
    for (int id = 0; id <= 7; id++) {
      assertTrue(seen[id], "Effect id " + id + " was never triggered");
    }
  }

  /** Helper: check if all ids have been seen. */
  private boolean allSeen(boolean[] seen) {
    for (boolean b : seen) {
      if (!b) return false;
    }
    return true;
  }

  /**
   * Test that when triggerProbability=0.0, no effect is triggered and the returned reels are not
   * triple.
   */
  @Test
  @DisplayName("When probability=0.0, spins never trigger and reels are not triple")
  void testNeverTriggersReturnsNonTriple() {
    SlotEngine.SlotConfig cfg = new SlotEngine.SlotConfig();
    cfg.setTriggerProbability(0.0); // never trigger

    SlotEngine engine = new SlotEngine(cfg, new Random(123));

    for (int i = 0; i < 50; i++) {
      SlotEngine.SpinResult r = engine.spin();

      assertFalse(r.isEffectTriggered(), "Effect should not be triggered");
      assertTrue(r.getEffect().isEmpty(), "Effect should be empty when not triggered");
      assertEquals("NONE", r.getTriggerType(), "Trigger type should be NONE");

      int[] reels = r.getReels();
      assertNotNull(reels);
      assertEquals(3, reels.length);
      assertFalse(isTriple(reels), "Reels should not form a triple when not triggered");
    }
  }
}
