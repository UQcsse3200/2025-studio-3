package com.csse3200.game.components.slot;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Slot machine engine to generate random outcomes for UI and trigger corresponding effects.
 *
 * <p>Flow:
 *
 * <ol>
 *   <li>Step 1: Check whether an event is triggered (default 80%).
 *   <li>Step 2: If triggered, select an event by relative weights:
 *       <ul>
 *         <li>Events 0–6: each weight = 8
 *         <li>Event 7: weight = 5
 *         <li>Total weight = 61
 *       </ul>
 *   <li>Step 3: Generate triple reels of the selected event id.
 *   <li>Step 4: Call the corresponding effect function (TODO for teammates).
 *   <li>Step 5: If not triggered, generate a non-triple reels array and return "NONE".
 * </ol>
 *
 * <p>Output for UI:
 *
 * <ul>
 *   <li>{@link SpinResult#getReels()} – a 3-length int array of ids in [0..7]
 *   <li>{@link SpinResult#isEffectTriggered()} – whether an effect is triggered
 *   <li>{@link SpinResult#getEffect()} – the triggered effect, if any
 *   <li>{@link SpinResult#getTriggerType()} – "EVENT" or "NONE"
 * </ul>
 */
public class SlotEngine {
  private static final Logger LOG = Logger.getLogger(SlotEngine.class.getName());

  /**
   * Enumeration of all possible effects. Each effect has a stable id (0..7) and a display name for
   * logging/debugging.
   */
  public enum Effect {
    GAIN_METALS(0, "GainMetals"),
    GAIN_COINS(1, "GainCoins"),
    SUMMON_ENEMY(2, "SummonEnemy"),
    DOUBLE_FURNACE(3, "DoubleFurnace"),
    LOSE_METALS(4, "LoseMetals"),
    FREEZE_ENEMY(5, "FreezeEnemy"),
    FROG_EVENT(6, "FrogEvent"),
    DESTROY_ENEMY(7, "DestroyEnemy");

    private final int id;
    private final String displayName;

    Effect(int id, String displayName) {
      this.id = id;
      this.displayName = displayName;
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
    private final boolean effectTriggered;
    private final Effect effect; // null if no effect triggered
    private final String triggerType; // "EVENT" | "NONE"

    /**
     * Construct a spin result.
     *
     * @param reels a 3-length array with values in [0..7]
     * @param effectTriggered whether an effect was triggered
     * @param effect the triggered effect (null if none)
     * @param triggerType "EVENT" if an effect was triggered, otherwise "NONE"
     */
    public SpinResult(int[] reels, boolean effectTriggered, Effect effect, String triggerType) {
      if (reels == null || reels.length != 3) {
        throw new IllegalArgumentException("reels must be length=3");
      }
      this.reels = Arrays.copyOf(reels, 3);
      this.effectTriggered = effectTriggered;
      this.effect = effect;
      this.triggerType = triggerType;
    }

    /**
     * @return Copy of reels array (length=3).
     */
    public int[] getReels() {
      return Arrays.copyOf(reels, 3);
    }

    /**
     * @return True if an effect was triggered.
     */
    public boolean isEffectTriggered() {
      return effectTriggered;
    }

    /**
     * @return The triggered effect wrapped in Optional.
     */
    public Optional<Effect> getEffect() {
      return Optional.ofNullable(effect);
    }

    /**
     * @return "EVENT" or "NONE".
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
          + (effect == null ? "NONE" : effect.getDisplayName())
          + ", triggerType="
          + triggerType
          + "}";
    }
  }

  /** Configuration for SlotEngine. Includes overall trigger probability and event weights. */
  public static class SlotConfig {
    private double triggerProbability = 0.80; // default: 80%
    private final Map<Effect, Integer> weights = new LinkedHashMap<>();

    /** Default: events 0..6 each weight=8, event7 weight=5. */
    public SlotConfig() {
      for (int i = 0; i <= 6; i++) {
        weights.put(Effect.fromId(i), 8);
      }
      weights.put(Effect.DESTROY_ENEMY, 5);
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
      this.triggerProbability = p;
    }

    /**
     * @return Weight map for events.
     */
    public Map<Effect, Integer> getWeights() {
      return weights;
    }
  }

  /** Utility to select a random item by integer weights. */
  private static class WeightedPicker<T> {
    private final NavigableMap<Integer, T> map = new TreeMap<>();
    private final int total;

    public WeightedPicker(Map<T, Integer> weights) {
      int sum = 0;
      for (Map.Entry<T, Integer> e : weights.entrySet()) {
        int w = e.getValue();
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
  private final WeightedPicker<Effect> eventPicker;

  /** Construct with default config and RNG. */
  public SlotEngine() {
    this(new SlotConfig(), new Random());
  }

  /** Construct with injected config and RNG. */
  public SlotEngine(SlotConfig config, Random random) {
    this.config = config;
    this.random = random;
    this.eventPicker = new WeightedPicker<>(config.getWeights());
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
      // Step2: Pick event
      Effect eff = eventPicker.pick(random);

      // Step3: Generate triple reels
      int[] reels = new int[] {eff.getId(), eff.getId(), eff.getId()};
      SpinResult res = new SpinResult(reels, true, eff, "EVENT");
      logResult(res);

      // Step4: Call corresponding effect function
      switch (eff.getId()) {
        case 0:
          // TODO: teammate implements GainMetals effect
          break;
        case 1:
          // TODO: teammate implements GainCoins effect
          break;
        case 2:
          // TODO: teammate implements SummonEnemy effect
          break;
        case 3:
          // TODO: teammate implements DoubleFurnace effect
          break;
        case 4:
          // TODO: teammate implements LoseMetals effect
          break;
        case 5:
          // TODO: teammate implements FreezeEnemy effect
          break;
        case 6:
          // TODO: teammate implements FrogEvent (QTE)
          break;
        case 7:
          // TODO: teammate implements DestroyEnemy effect
          break;
        default:
          throw new IllegalStateException("Unknown effect id: " + eff.getId());
      }

      return res;
    }

    // Step5: Not triggered → non-triple reels
    int[] reels = genNonTripleAny();
    SpinResult res = new SpinResult(reels, false, null, "NONE");
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
    int[] arr = new int[3];
    do {
      for (int i = 0; i < 3; i++) {
        arr[i] = random.nextInt(8);
      }
    } while (arr[0] == arr[1] && arr[1] == arr[2]); // ensure not triple
    return arr;
  }

  /** Log concise outcome information. */
  private void logResult(SpinResult res) {
    if (res.isEffectTriggered()) {
      LOG.fine(
          () ->
              String.format(
                  "[Slot] trigger=%s effect=%s(%d) reels=%s",
                  res.getTriggerType(),
                  res.getEffect().map(Effect::getDisplayName).orElse("NONE"),
                  res.getEffect().map(Effect::getId).orElse(-1),
                  Arrays.toString(res.getReels())));
    } else {
      LOG.fine(
          () -> String.format("[Slot] trigger=NONE reels=%s", Arrays.toString(res.getReels())));
    }
  }
}
