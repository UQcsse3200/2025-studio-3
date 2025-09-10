package com.csse3200.game.components.slot;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Slot machine draw & resolve engine.
 *
 * <p>Rules:
 *
 * <ul>
 *   <li>Frog (id=6) has independent probability (default 5%). If it appears, it triggers
 *       immediately (no triple required).
 *   <li>If Frog didn't trigger, Triple has probability (default 20%).
 *   <li>When Triple triggers, pick one effect from {0,1,2,3,4,5,7} by weight (0..5=15, 7=10;
 *       total=100) and set all three reels to that id.
 *   <li>Otherwise return a non-triple array (and with no Frog) to UI; effect = NONE.
 * </ul>
 *
 * <p>Output to UI:
 *
 * <ul>
 *   <li>{@link SpinResult#getReels()} – a 3-length int array of ids in [0..7].
 *   <li>{@link SpinResult#isEffectTriggered()} / {@link SpinResult#getEffect()} – whether/which
 *       effect is triggered.
 *   <li>{@link SpinResult#getTriggerType()} – "FROG" | "TRIPLE" | "NONE".
 * </ul>
 *
 * <p>Logging uses JDK {@link java.util.logging.Logger}. INFO prints the concise outcome line. FINE
 * may print branch details if needed.
 */
public class SlotEngine {
  private static final Logger LOG = Logger.getLogger(SlotEngine.class.getName());

  /** Effect ids and display names (stable ids for UI and implementation). */
  public enum Effect {
    GAIN_METALS(0, "GainMetals"),
    GAIN_COINS(1, "GainCoins"),
    SUMMON_ENEMY(2, "SummonEnemy"),
    DOUBLE_FURNACE(3, "DoubleFurnace"),
    LOSE_METALS(4, "LoseMetals"),
    FREEZE_ENEMY(5, "FreezeEnemy"),
    FROG(6, "Frog"),
    DESTROY_ENEMY(7, "DestroyEnemy");

    private final int id;
    private final String displayName;

    Effect(int id, String displayName) {
      this.id = id;
      this.displayName = displayName;
    }

    /**
     * @return Stable effect id that matches UI/implementation side.
     */
    public int getId() {
      return id;
    }

    /**
     * @return Readable effect name for logs/debugging.
     */
    public String getDisplayName() {
      return displayName;
    }

    /** Convert id to enum or throw if unknown. */
    public static Effect fromId(int id) {
      for (Effect e : values()) {
        if (e.id == id) return e;
      }
      throw new IllegalArgumentException("Unknown effect id: " + id);
    }
  }

  /** Single spin outcome returned to UI. */
  public static final class SpinResult {
    private final int[] reels;
    private final boolean effectTriggered;
    private final Optional<Effect> effect;
    private final String triggerType; // "FROG" | "TRIPLE" | "NONE"

    /**
     * @param reels length=3 array (values in [0..7])
     * @param effectTriggered whether any effect triggered (Frog or Triple)
     * @param effect the triggered effect if present
     * @param triggerType "FROG" | "TRIPLE" | "NONE"
     */
    public SpinResult(
        int[] reels, boolean effectTriggered, Optional<Effect> effect, String triggerType) {
      if (reels == null || reels.length != 3) {
        throw new IllegalArgumentException("reels must be length=3");
      }
      this.reels = Arrays.copyOf(reels, 3);
      this.effectTriggered = effectTriggered;
      this.effect = effect;
      this.triggerType = triggerType;
    }

    /**
     * @return copy of reels array (length=3, elements in [0..7]).
     */
    public int[] getReels() {
      return Arrays.copyOf(reels, 3);
    }

    /**
     * @return if any effect is triggered.
     */
    public boolean isEffectTriggered() {
      return effectTriggered;
    }

    /**
     * @return triggered effect if present.
     */
    public Optional<Effect> getEffect() {
      return effect;
    }

    /**
     * @return "FROG" | "TRIPLE" | "NONE".
     */
    public String getTriggerType() {
      return triggerType;
    }

    @Override
    public String toString() {
      return "SpinResult{reels="
          + Arrays.toString(reels)
          + ", effectTriggered="
          + effectTriggered
          + ", effect="
          + effect.map(Effect::getDisplayName).orElse("NONE")
          + ", triggerType="
          + triggerType
          + "}";
    }
  }

  /** Probability & weight configuration (tunable). Default: triple=0.20, frog=0.05. */
  public static class SlotConfig {
    private double tripleProbability = 0.40; // updated per requirement
    private double frogProbability = 0.10; // updated per requirement

    // Weights for triple effects (Frog excluded): 0..5=15, 7=10 (sum=100).
    private final Map<Effect, Integer> tripleEffectWeights = new LinkedHashMap<>();

    public SlotConfig() {
      tripleEffectWeights.put(Effect.GAIN_METALS, 15);
      tripleEffectWeights.put(Effect.GAIN_COINS, 15);
      tripleEffectWeights.put(Effect.SUMMON_ENEMY, 15);
      tripleEffectWeights.put(Effect.DOUBLE_FURNACE, 15);
      tripleEffectWeights.put(Effect.LOSE_METALS, 15);
      tripleEffectWeights.put(Effect.FREEZE_ENEMY, 15);
      tripleEffectWeights.put(Effect.DESTROY_ENEMY, 10);
    }

    public double getTripleProbability() {
      return tripleProbability;
    }

    public double getFrogProbability() {
      return frogProbability;
    }

    public void setTripleProbability(double p) {
      if (p < 0 || p > 1) throw new IllegalArgumentException("tripleProbability must be [0,1]");
      this.tripleProbability = p;
    }

    public void setFrogProbability(double p) {
      if (p < 0 || p > 1) throw new IllegalArgumentException("frogProbability must be [0,1]");
      this.frogProbability = p;
    }

    public Map<Effect, Integer> getTripleEffectWeights() {
      return tripleEffectWeights;
    }
  }

  /**
   * Simple weighted picker using integer weights.
   *
   * @param <T> item type
   */
  private static class WeightedPicker<T> {
    private final NavigableMap<Integer, T> map = new TreeMap<>();
    private final int totalWeight;

    public WeightedPicker(Map<T, Integer> weights) {
      int sum = 0;
      for (Map.Entry<T, Integer> e : weights.entrySet()) {
        int w = e.getValue();
        if (w <= 0) continue;
        sum += w;
        map.put(sum, e.getKey());
      }
      if (map.isEmpty()) throw new IllegalArgumentException("No positive weights provided.");
      this.totalWeight = sum;
    }

    public T pick(Random random) {
      int r = random.nextInt(totalWeight) + 1; // [1..totalWeight]
      return map.ceilingEntry(r).getValue();
    }

    public int getTotalWeight() {
      return totalWeight;
    }
  }

  private final SlotConfig config;
  private final Random random;
  private final WeightedPicker<Effect> triplePicker;

  /** Construct with default config and seed. */
  public SlotEngine() {
    this(new SlotConfig(), new Random());
  }

  /** Construct with injected config and RNG (for tests). */
  public SlotEngine(SlotConfig config, Random random) {
    this.config = config;
    this.random = random;
    this.triplePicker = new WeightedPicker<>(config.getTripleEffectWeights());
  }

  /**
   * Perform one spin and resolve outcome.
   *
   * <p>【UI对接】Call this from your UI code (e.g., when the slot frame/button is clicked) to generate
   * the reels array and the (optional) triggered effect.
   *
   * @return SpinResult consumable by UI.
   */
  public SpinResult spin() {
    // 1) Frog check (independent). If hit, trigger immediately.
    if (roll(config.getFrogProbability())) {
      int[] reels = genArrayWithAtLeastOne(Effect.FROG.getId()); // 【UI对接】show at least one frog(6)
      SpinResult res = new SpinResult(reels, true, Optional.of(Effect.FROG), "FROG");
      logResult(res);

      // 【效果实现对接】Dispatch to actual effect system by id/name if needed.
      // e.g., callEffect(res.getEffect().get().getId(), res.getEffect().get().getDisplayName());
      return res;
    }

    // 2) Triple check (only if no Frog was triggered).
    if (roll(config.getTripleProbability())) {
      Effect eff = triplePicker.pick(random); // choose from {0,1,2,3,4,5,7}
      int[] reels = new int[] {eff.getId(), eff.getId(), eff.getId()}; // 【UI对接】3-of-a-kind display
      SpinResult res = new SpinResult(reels, true, Optional.of(eff), "TRIPLE");
      logResult(res);

      // 【效果实现对接】Dispatch triple effect by id/name if needed.
      // e.g., callEffect(eff.getId(), eff.getDisplayName());
      return res;
    }

    // 3) Neither Frog nor Triple – return a non-triple, no-frog array to UI; no effect.
    int[] reels = genNonTripleWithoutFrog(); // 【UI对接】regular, no effect
    SpinResult res = new SpinResult(reels, false, Optional.empty(), "NONE");
    logResult(res);
    return res;
  }

  /** Bernoulli trial helper. */
  private boolean roll(double p) {
    boolean hit = random.nextDouble() < p;
    if (LOG.isLoggable(Level.FINE)) {
      LOG.fine(() -> String.format("roll(p=%.2f) -> %s", p, hit));
    }
    return hit;
  }

  /** Generate a 3-length array with at least one position equal to mustIncludeId. */
  private int[] genArrayWithAtLeastOne(int mustIncludeId) {
    int[] arr = new int[3];
    int pos = random.nextInt(3);
    for (int i = 0; i < 3; i++) {
      if (i == pos) arr[i] = mustIncludeId;
      else arr[i] = random.nextInt(8); // 0..7
    }
    return arr;
  }

  /** Generate a 3-length array that is not triple and contains no Frog(6). */
  private int[] genNonTripleWithoutFrog() {
    int[] arr = new int[3];
    do {
      for (int i = 0; i < 3; i++) {
        int v;
        do {
          v = random.nextInt(8);
        } while (v == Effect.FROG.getId());
        arr[i] = v;
      }
    } while (arr[0] == arr[1] && arr[1] == arr[2]); // ensure not triple
    return arr;
  }

  /** Unified concise logging for outcomes. */
  private void logResult(SpinResult res) {
    if (res.isEffectTriggered()) {
      LOG.info(
          () ->
              String.format(
                  "[Slot] trigger=%s effect=%s(%d) reels=%s",
                  res.getTriggerType(),
                  res.getEffect().map(Effect::getDisplayName).orElse("NONE"),
                  res.getEffect().map(Effect::getId).orElse(-1),
                  Arrays.toString(res.getReels())));
    } else {
      LOG.info(
          () -> String.format("[Slot] trigger=NONE reels=%s", Arrays.toString(res.getReels())));
    }
  }
}
