package com.csse3200.game.components.slot;

import com.csse3200.game.areas.SlotMachineArea;

import java.security.SecureRandom;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Random outcome generator for the 3-reel slot machine.
 *
 * Design:
 * - Uses a weighted picker to choose one Effect when a spin "triggers".
 * - The chosen Effect is encoded on all three reels (k-of-a-kind).
 * - The hero face (SlingShooter) is represented as a dedicated Effect (id=8).
 *   The UI drops a hero card when it detects triple hero; applyEffect() does nothing for it.
 * - When a spin does not trigger, a non-triple combination is returned.
 */
public class SlotEngine {
    private static final Logger LOG = Logger.getLogger(SlotEngine.class.getName());

    /** Number of reels (fixed). */
    private static final int NUM_REELS = 3;

    /** All possible effects including the hero triple. */
    public enum Effect {
        GAIN_METALS(0, "GainMetals", 8),
        GAIN_COINS(1, "GainCoins", 8),
        SUMMON_ENEMY(2, "SummonEnemy", 3),
        DOUBLE_FURNACE(3, "DoubleFurnace", 5),
        LOSE_METALS(4, "LoseMetals", 4),
        FREEZE_ENEMY(5, "FreezeEnemy", 5),
        FOG_EVENT(6, "FogEvent", 3),
        DESTROY_ENEMY(7, "DestroyEnemy", 4),
        // Hero triple. UI will drop a hero card when this occurs.
        // ↑ Increased default weight from 1 -> 6 to make hero triple more frequent.
        HERO_CARD(8, "SlingShooterHero", 16);

        private final int id;
        private final String displayName;
        private final int defaultWeight;

        Effect(int id, String displayName, int defaultWeight) {
            this.id = id;
            this.displayName = displayName;
            this.defaultWeight = defaultWeight;
        }
        public int getId() { return id; }
        public String getDisplayName() { return displayName; }
        public int getDefaultWeight() { return defaultWeight; }

        public static Effect fromId(int id) {
            for (Effect e : values()) if (e.id == id) return e;
            throw new IllegalArgumentException("Unknown effect id: " + id);
        }
    }

    /** Result of a spin. */
    public static final class SpinResult {
        private final int[] reels;   // length = 3, values are effect ids (0..8)
        private final Effect effect; // null if not triggered

        public SpinResult(int[] reels, Effect effect) {
            if (reels == null || reels.length != NUM_REELS)
                throw new IllegalArgumentException("reels must be length=" + NUM_REELS);
            this.reels = Arrays.copyOf(reels, NUM_REELS);
            this.effect = effect;
        }
        public int[] getReels() { return Arrays.copyOf(reels, NUM_REELS); }
        public boolean isEffectTriggered() { return effect != null; }
        public Optional<Effect> getEffect() { return Optional.ofNullable(effect); }

        @Override public String toString() {
            return "SpinResult{reels=" + Arrays.toString(reels) +
                    ", effect=" + (effect == null ? "NONE" : effect.getDisplayName()) + "}";
        }
    }

    /** Runtime configuration with trigger probability and per-effect weights. */
    public static class SlotConfig {
        private double triggerProbability = 0.80; // default 80%
        private final LinkedHashMap<Effect, Integer> weights = new LinkedHashMap<>();
        public SlotConfig() { for (Effect e : Effect.values()) weights.put(e, e.getDefaultWeight()); }
        public double getTriggerProbability() { return triggerProbability; }
        public void setTriggerProbability(double p) {
            if (p < 0.0 || p > 1.0) throw new IllegalArgumentException("p must be in [0,1]");
            this.triggerProbability = p;
        }
        public Map<Effect, Integer> getWeights() { return Collections.unmodifiableMap(weights); }
        public void setWeight(Effect e, int w) {
            if (e == null) throw new IllegalArgumentException("effect is null");
            if (w < 0) throw new IllegalArgumentException("weight must be >= 0");
            weights.put(e, w);
        }
        public void setWeights(Map<Effect, Integer> newWeights) {
            if (newWeights == null) throw new IllegalArgumentException("newWeights is null");
            for (Map.Entry<Effect, Integer> e : newWeights.entrySet()) {
                if (e.getKey() == null) throw new IllegalArgumentException("null key");
                if (e.getValue() == null || e.getValue() < 0)
                    throw new IllegalArgumentException("weight must be >= 0 for " + e.getKey());
            }
            weights.putAll(newWeights);
        }
    }

    /** Lightweight weighted picker using cumulative sums. */
    private static class WeightedPicker<T> {
        private final NavigableMap<Integer, T> map = new TreeMap<>();
        private final int total;
        WeightedPicker(Map<T, Integer> weights) {
            int sum = 0;
            for (Map.Entry<T, Integer> e : weights.entrySet()) {
                int w = e.getValue() == null ? 0 : e.getValue();
                if (w <= 0) continue;
                sum += w; map.put(sum, e.getKey());
            }
            if (map.isEmpty()) throw new IllegalArgumentException("No positive weights.");
            total = sum;
        }
        public T pick(Random rnd) {
            int r = rnd.nextInt(total) + 1; // [1..total]
            return map.ceilingEntry(r).getValue();
        }
    }

    private final SlotConfig config;
    private final Random random;
    private final EnumSet<Effect> loggedOnce = EnumSet.noneOf(Effect.class);
    private SlotMachineArea slotMachineArea;

    public SlotEngine() { this(new SlotConfig(), new SecureRandom()); }
    public SlotEngine(SlotConfig config, Random random) {
        this.config = config; this.random = random;
    }
    public SlotEngine(SlotMachineArea area) { this(); this.slotMachineArea = area; }
    public void setSlotMachineArea(SlotMachineArea area) { this.slotMachineArea = area; }

    /** Run a single spin. */
    public SpinResult spin() {
        if (roll(config.getTriggerProbability())) {
            // Pick one effect and encode it as a triple.
            WeightedPicker<Effect> picker = new WeightedPicker<>(config.getWeights());
            Effect eff = picker.pick(random);
            int[] reels = new int[NUM_REELS];
            Arrays.fill(reels, eff.getId());
            SpinResult res = new SpinResult(reels, eff);
            logResult(res);
            return res;
        }

        // Not triggered: return a non-triple combination.
        int[] reels = genNonTripleAny();
        SpinResult res = new SpinResult(reels, null);
        logResult(res);
        return res;
    }

    /** Apply non-visual effects. HERO_CARD intentionally does nothing here (UI handles drop). */
    public void applyEffect(SpinResult res) {
        if (res == null || !res.isEffectTriggered()) return;
        Effect eff = res.getEffect().orElse(null);
        if (eff == null) return;

        if (eff == Effect.HERO_CARD) {
            // no-op: the UI will spawn the hero card
            return;
        }

        if (slotMachineArea != null) {
            SlotEffect.executeByEffect(eff, slotMachineArea);
        } else {
            LOG.log(Level.WARNING, "SlotMachineArea not set; effect skipped: {0}", eff);
        }
    }

    // ----- internals -----

    private boolean roll(double p) { return random.nextDouble() < p; }

    private int[] genNonTripleAny() {
        int numSymbols = Effect.values().length; // 0..8
        int[] arr = new int[NUM_REELS];
        do {
            for (int i = 0; i < NUM_REELS; i++) arr[i] = random.nextInt(numSymbols);
        } while (isTriple(arr));
        return arr;
    }

    private static boolean isTriple(int[] a) {
        if (a == null || a.length != NUM_REELS) return false;
        for (int i = 1; i < a.length; i++) if (a[i] != a[0]) return false;
        return true;
    }

    private void logResult(SpinResult res) {
        if (!res.isEffectTriggered()) return; // keep logs clean for non-triggered spins
        Effect eff = res.getEffect().orElse(null);
        if (eff != null && loggedOnce.add(eff)) {
            LOG.info(() -> String.format(
                    "[Slot] triggered=true effect=%s(%d) reels=%s p=%.2f",
                    eff.getDisplayName(), eff.getId(),
                    Arrays.toString(res.getReels()),
                    config.getTriggerProbability()));
        }
    }
}
