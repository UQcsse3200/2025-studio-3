package com.csse3200.game.components.currency;

import com.badlogic.gdx.utils.Timer;
import com.csse3200.game.components.Component;

/**
 * A lightweight, opt-in component that periodically generates currency
 * (similar to the PVZ sunflower). Attach it to any entity that should
 * produce currency over time.
 *
 * It only depends on the existing Currency class and LibGDX's Timer.
 * Safe to use without changing others' code.
 */
public class CurrencyGeneratorComponent extends Component {
    private final Currency currency;    // the wallet to credit
    private final int amountPerTick;    // how much to add every tick
    private final float intervalSec;    // seconds between ticks

    private boolean running = false;
    private Timer.Task task;

    /**
     * @param currency      wallet to credit (must not be null)
     * @param amountPerTick amount to add each interval (> 0)
     * @param intervalSec   interval in seconds (> 0)
     */
    public CurrencyGeneratorComponent(Currency currency, int amountPerTick, float intervalSec) {
        if (currency == null) {
            throw new IllegalArgumentException("currency must not be null");
        }
        if (amountPerTick <= 0) {
            throw new IllegalArgumentException("amountPerTick must be > 0");
        }
        if (intervalSec <= 0f) {
            throw new IllegalArgumentException("intervalSec must be > 0");
        }
        this.currency = currency;
        this.amountPerTick = amountPerTick;
        this.intervalSec = intervalSec;
    }

    /** Auto-start when the entity is added to the world. */
    @Override
    public void create() {
        super.create();
        start();
    }

    /** Start periodic generation (idempotent). */
    public void start() {
        if (running) return;
        running = true;
        task = Timer.schedule(new Timer.Task() {
            @Override public void run() {
                currency.addSunshine(amountPerTick);
                // Optional: trigger event for HUD or logs
                if (getEntity() != null && getEntity().getEvents() != null) {
                    getEntity().getEvents().trigger("currencyGenerated", amountPerTick);
                }
            }
        }, intervalSec, intervalSec); // delay, then repeat
    }

    /** Stop periodic generation. */
    public void stop() {
        running = false;
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    @Override
    public void dispose() {
        stop();
        super.dispose();
    }
}
