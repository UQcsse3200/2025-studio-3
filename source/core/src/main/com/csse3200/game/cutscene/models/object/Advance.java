package com.csse3200.game.cutscene.models.object;

/**
 * Class to store data for the Advance setting
 * Each type of advance has their own generator.
 */
public class Advance {
    private AdvanceMode mode;
    private int delayMs;
    private String signalKey;

    /**
     * Creates an {@code Advance} with parameter details
     * This is NOT to be used, rather use provided static methods (e.g. {@link Advance#auto()}.
     * @param mode       The {@link AdvanceMode} mode
     * @param delayMs    The delay in ms
     * @param signalKey  The signal key
     */
    public Advance(AdvanceMode mode, int delayMs, String signalKey) {
        this.mode = mode;
        this.delayMs = delayMs;
        this.signalKey = signalKey;
    }

    public AdvanceMode getMode() {
        return mode;
    }

    public int getDelayMs() {
        return delayMs;
    }

    public String getSignalKey() {
        return signalKey;
    }

    /**
     * Get an {@code Advance} configured for {@link AdvanceMode#INPUT}.
     * No delay or signal key are used.
     *
     * @return {@code Advance} in INPUT mode
     */
    public static Advance input() {
        return new Advance(AdvanceMode.INPUT, 0, null);
    }

    /**
     * Get an {@code Advance} configured for {@link AdvanceMode#AUTO}.
     * No delay or signal key are used.
     *
     * @return {@code Advance} in AUTO mode
     */
    public static Advance auto() {
        return new Advance(AdvanceMode.AUTO, 0, null);
    }

    /**
     * Get an {@code Advance} configured for {@link AdvanceMode#AUTO_DELAY}.
     * No signal key is set.
     *
     * @param ms  The delay (in Milliseconds) to set the advance to
     * @return {@code Advance} in AUTO_DELAY mode with ms set
     */
    public static Advance autoDelay(int ms) {
        return new Advance(AdvanceMode.AUTO_DELAY, ms, null);
    }

    /**
     * Get an {@code Advance} configured for {@link AdvanceMode#SIGNAL}.
     * No delay is set.
     *
     * @param key  The signal key to set
     * @return {@code Advance} in SIGNAL mode with key set
     */
    public static Advance signal(String key) {
        return new Advance(AdvanceMode.SIGNAL, 0, key);
    }
}
