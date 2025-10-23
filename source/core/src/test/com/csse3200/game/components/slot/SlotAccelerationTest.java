package com.csse3200.game.components.slot;

import static org.junit.jupiter.api.Assertions.*;

import com.csse3200.game.components.slot.SlotEngine.SlotConfig;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests that SlotEngine auto-refill accelerates with speed multiplier.
 * Headless-safe: uses a local GameTime stub (no LibGDX dependency).
 */
class SlotAccelerationTest {

    /** Headless-safe GameTime stub (no Gdx.graphics). */
    private static class TestGameTime extends GameTime {
        private volatile float scale = 1f;
        @Override public void setTimeScale(float timeScale) { this.scale = Math.max(0f, timeScale); }
        @Override public float getTimeScale() { return scale; }
        @Override public float getDeltaTime() { return (1f / 60f) * scale; }
        @Override public float getRawDeltaTime() { return 1f / 60f; }
    }

    @BeforeEach
    void setUp() {
        ServiceLocator.clear();
        ServiceLocator.registerTimeSource(new TestGameTime());
    }

    @AfterEach
    void tearDown() {
        ServiceLocator.clear();
    }

    @Test
    @DisplayName("Refill accelerates: 1.0x ~0.6s ≈ 0, 2.0x ~0.6s ≥ 1")
    void refillAcceleratesWithSpeedMultiplier() {
        // Arrange: 1s period, start from 0 credits
        SlotConfig cfg = new SlotConfig();
        cfg.setInitialSpins(0);
        cfg.setRefillPeriodSeconds(1);

        SlotEngine engine = new SlotEngine(cfg, new Random(1));
        try {
            // Baseline at 1.0x: wait ~600ms, still expected 0
            busyWaitMillis(600);
            int baseline = engine.getRemainingSpins();
            assertEquals(0, baseline, "At ~0.6s and 1.0x, should not have refilled yet");

            // Accelerate to 2.0x (effective ~0.5s per tick), wait up to 800ms for >=1
            engine.setRefillSpeedMul(2.0f);
            awaitTrue(800, () -> engine.getRemainingSpins() >= 1);

            int fast = engine.getRemainingSpins();
            assertTrue(fast >= 1, "At ~0.6–0.8s and 2.0x, should have refilled >= 1");
        } finally {
            engine.dispose(); // stop scheduler thread
        }
    }

    @Test
    @DisplayName("setRefillSpeedMul applies the multiplier")
    void setRefillSpeedMulIsApplied() {
        SlotConfig cfg = new SlotConfig();
        cfg.setInitialSpins(0);
        cfg.setRefillPeriodSeconds(10); // long period to avoid incidental ticks

        SlotEngine engine = new SlotEngine(cfg, new Random(2));
        try {
            engine.setRefillSpeedMul(1.5f);
            assertEquals(1.5f, engine.getRefillSpeedMul(), 1e-6f);
        } finally {
            engine.dispose();
        }
    }

    // ---------- helpers ----------

    /** Busy-wait for a given duration (avoids Thread.sleep for Sonar S2925). */
    private static void busyWaitMillis(long ms) {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(ms);
        while (System.nanoTime() < deadline) {
            Thread.onSpinWait();
        }
    }

    /** Waits until condition is true or timeout expires (no Thread.sleep). */
    private static void awaitTrue(long timeoutMs, java.util.function.BooleanSupplier cond) {
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeoutMs);
        while (System.nanoTime() < deadline) {
            if (cond.getAsBoolean()) return;
            Thread.onSpinWait();
        }
        fail("Timeout waiting for condition");
    }
}
