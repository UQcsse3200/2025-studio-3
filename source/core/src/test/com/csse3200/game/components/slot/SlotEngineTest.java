package com.csse3200.game.components.slot;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import org.junit.jupiter.api.Test;

/** Basic tests to ensure rule correctness with deterministic seeds. */
public class SlotEngineTest {

  @Test
  void frogShouldTriggerFirst() {
    SlotEngine.SlotConfig cfg = new SlotEngine.SlotConfig();
    cfg.setFrogProbability(1.0); // 100% frog
    cfg.setTripleProbability(1.0); // even if 100% triple, frog should take precedence

    SlotEngine engine = new SlotEngine(cfg, new Random(42));
    SlotEngine.SpinResult res = engine.spin();

    assertTrue(res.isEffectTriggered());
    assertEquals("FROG", res.getTriggerType());
    assertTrue(res.getEffect().isPresent());
    assertEquals(SlotEngine.Effect.FROG, res.getEffect().get());
    int[] r = res.getReels();
    assertTrue(r[0] == 6 || r[1] == 6 || r[2] == 6);
  }

  @Test
  void tripleShouldWork() {
    SlotEngine.SlotConfig cfg = new SlotEngine.SlotConfig();
    cfg.setFrogProbability(0.0);
    cfg.setTripleProbability(1.0); // force triple

    SlotEngine engine = new SlotEngine(cfg, new Random(1));
    SlotEngine.SpinResult res = engine.spin();

    assertTrue(res.isEffectTriggered());
    assertEquals("TRIPLE", res.getTriggerType());
    assertTrue(res.getEffect().isPresent());
    int[] r = res.getReels();
    assertEquals(r[0], r[1]);
    assertEquals(r[1], r[2]);
    assertNotEquals(SlotEngine.Effect.FROG, res.getEffect().get());
  }

  @Test
  void noneCaseShouldBeNonTripleAndNoFrog() {
    SlotEngine.SlotConfig cfg = new SlotEngine.SlotConfig();
    cfg.setFrogProbability(0.0);
    cfg.setTripleProbability(0.0);

    SlotEngine engine = new SlotEngine(cfg, new Random(7));
    SlotEngine.SpinResult res = engine.spin();

    assertFalse(res.isEffectTriggered());
    assertEquals("NONE", res.getTriggerType());
    int[] r = res.getReels();
    assertFalse(r[0] == r[1] && r[1] == r[2]); // not triple
    for (int v : r) assertNotEquals(SlotEngine.Effect.FROG.getId(), v); // no frog
  }
}
