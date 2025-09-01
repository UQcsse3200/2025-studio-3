package com.csse3200.game.components.currency;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Timer;
import com.csse3200.game.components.Component;

/**
 * Spawns clickable sun tokens on screen over time.
 * Player must click them to add currency.
 */
public class CurrencySunSpawnerComponent extends Component {
    private final Stage stage;
    private final Currency currency;
    private final Texture sunTexture;

    private final int valuePerSun;     // value of each sun
    private final float intervalSec;   // spawn interval
    private final float lifetimeSec;   // lifetime before fading out

    private boolean running = false;
    private Timer.Task task;

    public CurrencySunSpawnerComponent(Stage stage,
                                       Currency currency,
                                       Texture sunTexture,
                                       int valuePerSun,
                                       float intervalSec,
                                       float lifetimeSec) {
        this.stage = stage;
        this.currency = currency;
        this.sunTexture = sunTexture;
        this.valuePerSun = valuePerSun;
        this.intervalSec = intervalSec;
        this.lifetimeSec = lifetimeSec;
    }

    @Override
    public void create() {
        super.create();
        start();
    }

    public void start() {
        if (running) return;
        running = true;
        task = Timer.schedule(new Timer.Task() {
            @Override public void run() {
                spawnOneSun();
            }
        }, intervalSec, intervalSec);
    }

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

    private void spawnOneSun() {
        if (stage == null || sunTexture == null || currency == null) return;

        CurrencyInteraction sun = new CurrencyInteraction(sunTexture, valuePerSun, currency);

        float margin = 32f;
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        float x = margin + (float) Math.random() * Math.max(1f, (w - sun.getWidth() - margin * 2));
        float y = margin + (float) Math.random() * Math.max(1f, (h - sun.getHeight() - margin * 2));

        sun.setPosition(x, y);

        // Fade out and remove if not clicked in time
        sun.addAction(Actions.sequence(
                Actions.delay(lifetimeSec),
                Actions.fadeOut(0.35f),
                Actions.removeActor()
        ));

        stage.addActor(sun);
    }
}
