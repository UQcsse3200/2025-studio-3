package com.csse3200.game.components.slot;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Slot machine engine to generate random outcomes for UI and trigger corresponding effects.
 *
 * <p>Flow: 1) Roll trigger (default 80%). 2) If triggered, pick an event by weights and return
 * triple reels of that event id. Otherwise return a non-triple reels array. 3) Effect handling
 * (TODO by teammates).
 *
 * <p>Output for UI: - SpinResult#getReels(): int[3] of ids in [0..7] -
 * SpinResult#isEffectTriggered(): boolean - SpinResult#getEffect(): Optional<Effect>
 */
public class SlotEngine {
  private static final Logger LOG = Logger.getLogger(SlotEngine.class.getName());

  /** Number of symbols (always equals Effect.values().length). */
  private static final int NUM_SYMBOLS = Effect.values().length;

  /** Number of reels (fixed at 3). */
  private static final int NUM_REELS = 3;

  // Track which effects have already been logged once
  private final EnumSet<Effect> loggedOnce = EnumSet.noneOf(Effect.class);

  /**
   * Enumeration of all possible effects. Each effect has a stable id (0..7) and a display name for
   * logging/debugging.
   */
  public enum Effect {
    GAIN_METALS(0, "GainMetals", 8),
    GAIN_COINS(1, "GainCoins", 8),
    SUMMON_ENEMY(2, "SummonEnemy", 3),
    DOUBLE_FURNACE(3, "DoubleFurnace", 5),
    LOSE_METALS(4, "LoseMetals", 4),
    FREEZE_ENEMY(5, "FreezeEnemy", 5),
    FOG_EVENT(6, "FogEvent", 3),
    DESTROY_ENEMY(7, "DestroyEnemy", 4);

    private final int id;
    private final String displayName;
    private final int defaultWeight;

    Effect(int id, String displayName, int defaultWeight) {
      this.id = id;
      this.displayName = displayName;
      this.defaultWeight = defaultWeight;
    }

    /**
     * @return Stable id of the effect (0..7).
     */
    public int getId() {
      return id;
    }

    /**
     * @return Readable display name for logging.
     */
    public String getDisplayName() {
      return displayName;
    }

    public int getDefaultWeight() {
      return defaultWeight;
    }

    /** Convert an integer id to Effect enum. */
    public static Effect fromId(int id) {
      for (Effect e : values()) {
        if (e.id == id) return e;
      }
      throw new IllegalArgumentException("Unknown effect id: " + id);
    }
  }

  /** Immutable object representing the result of a spin. */
  public static final class SpinResult {
    private final int[] reels;
    private final Effect effect; // null if no effect triggered

    /**
     * Construct a spin result.
     *
     * @param reels a 3-length array with values in [0..7]
     * @param effect the triggered effect (null if none)
     */
    public SpinResult(int[] reels, Effect effect) {
      if (reels == null || reels.length != NUM_REELS) {
        throw new IllegalArgumentException("reels must be length=" + NUM_REELS);
      }
      this.reels = Arrays.copyOf(reels, NUM_REELS);
      this.effect = effect;
    }

    /**
     * @return Copy of reels array (length=3).
     */
    public int[] getReels() {
      return Arrays.copyOf(reels, NUM_REELS);
    }

    /**
     * @return True if an effect was triggered.
     */
    public boolean isEffectTriggered() {
      return effect != null;
    }

    /**
     * @return The triggered effect wrapped in Optional.
     */
    public Optional<Effect> getEffect() {
      return Optional.ofNullable(effect);
    }

    @Override
    public String toString() {
      return "SpinResult{reels="
          + Arrays.toString(reels)
          + ", effect="
          + (effect == null ? "NONE" : effect.getDisplayName())
          + "}";
    }
  }

  /** Configuration for SlotEngine. Includes overall trigger probability and event weights. */
  public static class SlotConfig {
    private double triggerProbability = 0.80; // default: 80%

    // Keep a mutable weights map; initialize with enum defaults and keep order stable.
    private final LinkedHashMap<Effect, Integer> weights = new LinkedHashMap<>();

    public SlotConfig() {
      for (Effect e : Effect.values()) {
        weights.put(e, e.getDefaultWeight());
      }
    }

    /**
     * @return Probability of triggering an event (0..1).
     */
    public double getTriggerProbability() {
      return triggerProbability;
    }

    /**
     * @param p New trigger probability (0..1).
     */
    public void setTriggerProbability(double p) {
      if (p < 0.0 || p > 1.0) {
        throw new IllegalArgumentException("Probability must be in [0,1]");
      }
      this.triggerProbability = p;
    }

    /**
     * @return Weight map for events.
     */
    public Map<Effect, Integer> getWeights() {
      return Collections.unmodifiableMap(new LinkedHashMap<>(weights));
    }

    /** Set a single weight (allows 0 to disable an effect). */
    public void setWeight(Effect effect, int weight) {
      if (effect == null) throw new IllegalArgumentException("effect is null");
      if (weight < 0) throw new IllegalArgumentException("weight must be >= 0");
      weights.put(effect, weight);
    }

    /**
     * Bulk set weights. Missing effects keep their previous values; negative values are rejected.
     * Passing an empty map is allowed (but picking will fail later with an exception if all weights
     * are non-positive).
     */
    public void setWeights(Map<Effect, Integer> newWeights) {
      if (newWeights == null) throw new IllegalArgumentException("newWeights is null");
      for (Map.Entry<Effect, Integer> e : newWeights.entrySet()) {
        if (e.getKey() == null) throw new IllegalArgumentException("weights contains null key");
        if (e.getValue() == null || e.getValue() < 0) {
          throw new IllegalArgumentException("weight must be >= 0 for " + e.getKey());
        }
      }
      weights.putAll(newWeights);
    }
  }

  /** Utility to select a random item by integer weights. */
  private static class WeightedPicker<T> {
    private final NavigableMap<Integer, T> map = new TreeMap<>();
    private final int total;

    public WeightedPicker(Map<T, Integer> weights) {
      int sum = 0;
      for (Map.Entry<T, Integer> e : weights.entrySet()) {
        int w = e.getValue() == null ? 0 : e.getValue();
        if (w <= 0) continue;
        sum += w;
        map.put(sum, e.getKey());
      }
      if (map.isEmpty()) throw new IllegalArgumentException("No positive weights provided.");
      this.total = sum;
    }

    /** Pick one item based on weights. */
    public T pick(Random random) {
      int r = random.nextInt(total) + 1; // [1..total]
      return map.ceilingEntry(r).getValue();
    }
  }

  private final SlotConfig config;
  private final Random random;

  /** Construct with default config and RNG. */
  public SlotEngine() {
    this(new SlotConfig(), new Random());
  }

  /** Construct with injected config and RNG. */
  public SlotEngine(SlotConfig config, Random random) {
    this.config = config;
    this.random = random;
  }

  /**
   * Perform one spin: - If triggered: return triple reels and call effect. - If not triggered:
   * return non-triple reels with NONE.
   *
   * @return SpinResult consumable by UI.
   */
  public SpinResult spin() {
    // Step1: Check trigger
    if (roll(config.getTriggerProbability())) {
      // Step2: Build a picker from the **current** weights (reflects recent setWeight/ setWeights)
      WeightedPicker<Effect> picker = new WeightedPicker<>(config.getWeights());
      Effect eff = picker.pick(random);

      // Step3: Generate triple reels using NUM_REELS (no hard-coded 3)
      int[] reels = new int[NUM_REELS];
      Arrays.fill(reels, eff.getId());

      SpinResult res = new SpinResult(reels, eff);
      logResult(res);

      // Step4: Call corresponding effect function
      switch (eff) {
        case GAIN_METALS:
          // TODO: teammate implements GainMetals effect
          break;
        case GAIN_COINS:
          // TODO: teammate implements GainCoins effect
          break;
        case SUMMON_ENEMY:
          // TODO: teammate implements SummonEnemy effect
          break;
        case DOUBLE_FURNACE:
          // TODO: teammate implements DoubleFurnace effect
          break;
        case LOSE_METALS:
          // TODO: teammate implements LoseMetals effect
          break;
        case FREEZE_ENEMY:
          // TODO: teammate implements FreezeEnemy effect
          break;
        case FOG_EVENT:
          // TODO: teammate implements FogEvent
          break;
        case DESTROY_ENEMY:
          // TODO: teammate implements DestroyEnemy effect
          break;
        default:
          throw new IllegalStateException("Unknown effect id: " + eff.getId());
      }
      return res;
    }

    // Step5: Not triggered → non-triple reels
    int[] reels = genNonTripleAny();
    SpinResult res = new SpinResult(reels, null);
    logResult(res);
    return res;
  }

  /** Bernoulli trial with probability p. */
  private boolean roll(double p) {
    boolean hit = random.nextDouble() < p;
    if (LOG.isLoggable(Level.FINE)) {
      LOG.fine(() -> String.format("roll(p=%.2f) -> %s", p, hit));
    }
    return hit;
  }

  /** Generate a 3-length array that is NOT triple. */
  private int[] genNonTripleAny() {
    int[] arr = new int[NUM_REELS];
    do {
      for (int i = 0; i < NUM_REELS; i++) {
        arr[i] = random.nextInt(NUM_SYMBOLS);
      }
    } while (isTriple(arr)); // ensure not triple
    return arr;
  }

  private static boolean isTriple(int[] a) {
    if (a == null || a.length != NUM_REELS) return false;
    for (int i = 1; i < a.length; i++) {
      if (a[i] != a[0]) return false;
    }
    return true;
  }

  /** Log concise outcome information. */
  private void logResult(SpinResult res) {
    if (!res.isEffectTriggered()) {
      return; // do not log NONE
    }
    Effect eff = res.getEffect().orElse(null);
    // log only the first time we see this effect
    if (eff != null && loggedOnce.add(eff)) {
      LOG.info(
          () ->
              String.format(
                  "[Slot] triggered=true effect=%s(%d) reels=%s p=%.2f",
                  eff.getDisplayName(),
                  eff.getId(),
                  Arrays.toString(res.getReels()),
                  config.getTriggerProbability()));
    }
  }
}
