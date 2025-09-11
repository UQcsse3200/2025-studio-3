package com.csse3200.game.components.slot;

import static org.junit.jupiter.api.Assertions.*;

import java.util.EnumSet;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link SlotEngine}.
 *
 * <p>No hardcoded reel count or effect count. Everything is discovered dynamically.
 */
class SlotEngineTest {

  /** Helper: check if all entries in array are equal. */
  private static boolean isAllEqual(int[] arr) {
    if (arr == null || arr.length == 0) return false;
    int v = arr[0];
    for (int i = 1; i < arr.length; i++) {
      if (arr[i] != v) return false;
    }
    return true;
  }

  /**
   * Ensure every effect enum can be produced at least once when triggerProbability=1.0. Also verify
   * each returned reels is k-of-a-kind.
   */
  @Test
  @DisplayName("Each Effect appears at least once with correct reels (always-trigger)")
  void testEachEffectOnce() {
    SlotEngine.SlotConfig cfg = new SlotEngine.SlotConfig();
    cfg.setTriggerProbability(1.0); // always trigger
    SlotEngine engine = new SlotEngine(cfg, new Random(42));

    // discover reel length dynamically
    int reelLen = engine.spin().getReels().length;

    EnumSet<SlotEngine.Effect> seen = EnumSet.noneOf(SlotEngine.Effect.class);

    int attempts = 0;
    int cap = 400; // upper bound for attempts

    while (attempts < cap && seen.size() < SlotEngine.Effect.values().length) {
      SlotEngine.SpinResult r = engine.spin();
      assertTrue(r.isEffectTriggered(), "Effect should be triggered");

      SlotEngine.Effect eff = r.getEffect().orElseThrow();
      int[] reels = r.getReels();

      assertEquals(reelLen, reels.length, "Reels length must match discovered reel length");
      assertTrue(isAllEqual(reels), "Triggered reels must be all equal to effect id");
      for (int v : reels) assertEquals(eff.getId(), v, "Reel value must equal effect id");

      seen.add(eff);
      attempts++;
    }

    for (SlotEngine.Effect eff : SlotEngine.Effect.values()) {
      assertTrue(seen.contains(eff), "Effect " + eff + " was never triggered");
    }
  }

  /**
   * When triggerProbability=0.0, spins should never trigger, and the returned reels should not be
   * all equal.
   */
  @Test
  @DisplayName("With probability=0.0, never trigger and reels not k-of-a-kind")
  void testNeverTriggersReturnsNonTriple() {
    SlotEngine.SlotConfig cfg = new SlotEngine.SlotConfig();
    cfg.setTriggerProbability(0.0); // never trigger
    SlotEngine engine = new SlotEngine(cfg, new Random(123));

    int reelLen = engine.spin().getReels().length;

    for (int i = 0; i < 50; i++) {
      SlotEngine.SpinResult r = engine.spin();

      assertFalse(r.isEffectTriggered(), "Effect should not be triggered");
      assertTrue(r.getEffect().isEmpty(), "Effect should be empty when not triggered");

      int[] reels = r.getReels();
      assertNotNull(reels);
      assertEquals(reelLen, reels.length, "Reels length should remain consistent");
      assertFalse(isAllEqual(reels), "Reels should not all be equal when not triggered");
    }
  }
}
