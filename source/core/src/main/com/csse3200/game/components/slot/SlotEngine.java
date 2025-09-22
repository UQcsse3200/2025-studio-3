package com.csse3200.game.components.slot;

import com.csse3200.game.areas.SlotMachineArea;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Slot machine engine to generate random outcomes for UI and trigger corresponding effects.
 *
 * <p>Flow: 1) Roll trigger (default 80%). 2) If triggered, pick an event by weights and return
 * triple reels of that event id. Otherwise, return a non-triple reels array. 3) Effect handling
 *
 * <p>* Output for UI: * - {@code SpinResult#getReels(): int[3]} of ids in [0..7] * - {@code
 * SpinResult#isEffectTriggered(): boolean} * - {@code
 * SpinResult#getEffect():Optional&lt;Effect&gt;}
 *
 * <p><b>New:</b> Spin Credits * Start with 5 credits by default (configurable). * Automatically +1
 * credit every 5 seconds (configurable). * {@link #spin()} consumes 1 credit each time. * When no
 * credits left, return a non-triple result with no effect, and log a warning.
 */
public class SlotEngine {
  private static final Logger LOG = Logger.getLogger(SlotEngine.class.getName());

  /** Number of symbols (always equals Effect.values().length). */
  private static final int NUM_SYMBOLS = Effect.values().length;

  /** Number of reels (fixed at 3). */
  private static final int NUM_REELS = 3;

  // Track which effects have already been logged once
  private final EnumSet<Effect> loggedOnce = EnumSet.noneOf(Effect.class);

  // New: spin credits state
  private final AtomicInteger remainingSpins;
  private ScheduledExecutorService refillExec;

  /** Try to consume one credit. */
  private boolean consumeOneCredit() {
    while (true) {
      int cur = remainingSpins.get();
      if (cur <= 0) return false;
      if (remainingSpins.compareAndSet(cur, cur - 1)) {
        if (LOG.isLoggable(Level.FINE)) {
          LOG.fine(() -> String.format("[Slot] credit consumed: %d -> %d", cur, cur - 1));
        }
        return true;
      }
    }
  }

  /** Add credits with optional cap and log the source (manual or auto). */
  private void updateSpins(int delta, String source) {
    if (delta <= 0) return;
    int max = config.getMaxSpins();
    int before;
    int after;
    while (true) {
      before = remainingSpins.get();
      int next = before + delta;
      if (max > 0 && next > max) next = max; // respect max cap if set
      if (next == before) {
        if (LOG.isLoggable(Level.FINE)) {
          LOG.fine("[Slot] spins unchanged (at cap), source=" + source + ", left=" + before);
        }
        return;
      }
      if (remainingSpins.compareAndSet(before, next)) {
        after = next;
        break;
      }
    }
    final int b = before;
    final int a = after;
    LOG.info(() -> String.format("[Slot] spins +%d (%s): %d -> %d", delta, source, b, a));
  }

  /**
   * Enumeration of all possible effects. Each effect has a stable id (0..7) and a display name for
   * logging/debugging.
   */
  public enum Effect {
    GAIN_METALS(0, "GainMetals", 0),
    GAIN_COINS(1, "GainCoins", 0),
    SUMMON_ENEMY(2, "SummonEnemy", 1),
    FREEZE_ENEMY(5, "FreezeEnemy", 0),
    DESTROY_ENEMY(7, "DestroyEnemy", 1),
    DROP_SLINGSHOOTER_CARD(8, "DropSlingShooterCard", 10);

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

    // New: credits configuration
    /** Initial credits at start, default 5. */
    private int initialSpins = 5;

    /** Auto-refill interval in seconds, default 5. */
    private int refillPeriodSeconds = 5;

    /** Maximum cap of credits; <=0 means no cap. */
    private int maxSpins = 0;

    public SlotConfig() {
      for (Effect e : Effect.values()) {
        weights.put(e, e.getDefaultWeight());
      }
    }

    // New: credits getters and setters
    public int getInitialSpins() {
      return initialSpins;
    }

    public void setInitialSpins(int initialSpins) {
      if (initialSpins < 0) throw new IllegalArgumentException("initialSpins must be >= 0");
      this.initialSpins = initialSpins;
    }

    public int getRefillPeriodSeconds() {
      return refillPeriodSeconds;
    }

    public void setRefillPeriodSeconds(int refillPeriodSeconds) {
      if (refillPeriodSeconds <= 0)
        throw new IllegalArgumentException("refillPeriodSeconds must be > 0");
      this.refillPeriodSeconds = refillPeriodSeconds;
    }

    public int getMaxSpins() {
      return maxSpins;
    }

    public void setMaxSpins(int maxSpins) {
      this.maxSpins = maxSpins;
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
    this(new SlotConfig(), new SecureRandom());
  }

  /** Construct with injected config and RNG. */
  public SlotEngine(SlotConfig config, Random random) {
    this.config = Objects.requireNonNull(config, "config");
    this.random = Objects.requireNonNull(random, "random");
    this.remainingSpins = new AtomicInteger(config.getInitialSpins());
    startAutoRefill();
  }

  private SlotMachineArea slotMachineArea;

  public SlotEngine(SlotMachineArea area) {
    this(new SlotConfig(), new SecureRandom());
    this.slotMachineArea = area;
  }

  public void setSlotMachineArea(SlotMachineArea area) {
    this.slotMachineArea = area;
  }

  // Credits API
  public int getRemainingSpins() {
    return remainingSpins.get();
  }

  public boolean canSpin() {
    return remainingSpins.get() > 0;
  }

  public void addSpins(int delta) {
    if (delta > 0) updateSpins(delta, "manual_add");
  }

  public synchronized void startAutoRefill() {
    if (refillExec != null && !refillExec.isShutdown()) {
      LOG.fine("[Slot] auto_refill already running, skip start");
      return;
    }
    refillExec =
        Executors.newSingleThreadScheduledExecutor(
            r -> {
              Thread t = new Thread(r, "SlotEngine-Refill");
              t.setDaemon(true);
              return t;
            });
    int period = config.getRefillPeriodSeconds();
    refillExec.scheduleAtFixedRate(
        () -> updateSpins(1, "auto_refill"), period, period, TimeUnit.SECONDS);
    LOG.info(() -> String.format("[Slot] auto_refill started, +1 per %ds", period));
  }

  public synchronized void stopAutoRefill() {
    if (refillExec != null) {
      refillExec.shutdownNow();
      refillExec = null;
      LOG.info("[Slot] auto_refill stopped");
    }
  }

  public void dispose() {
    stopAutoRefill();
  }

  /**
   * Perform one spin: - If triggered: return triple reels and call effect. - If not triggered:
   * return non-triple reels with NONE.
   *
   * @return SpinResult consumable by UI.
   */
  public SpinResult spin() {
    // First check if we have credits; if not, return non-trigger result
    if (!consumeOneCredit()) {
      int[] reelsNoCredit = genNonTripleAny();
      SpinResult noCreditRes = new SpinResult(reelsNoCredit, null);
      LOG.warning(
          () ->
              String.format(
                  "[Slot] spin blocked: no credits; return non-trigger. reels=%s, left=%d",
                  Arrays.toString(noCreditRes.getReels()), remainingSpins.get()));
      return noCreditRes;
    }
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
                  "[Slot] triggered=true effect=%s(%d) reels=%s p=%.2f left=%d",
                  eff.getDisplayName(),
                  eff.getId(),
                  Arrays.toString(res.getReels()),
                  config.getTriggerProbability(),
                  remainingSpins.get()));
    }
  }

  public void applyEffect(SpinResult res) {
    if (res == null || !res.isEffectTriggered()) return;

    Effect eff = res.getEffect().orElse(null);
    if (eff == null) return;

    if (slotMachineArea != null) {
      SlotEffect.executeByEffect(eff, slotMachineArea);
    } else {
      LOG.log(Level.WARNING, "LevelGameArea not set; effect skipped: {0}", eff);
    }
  }
}
