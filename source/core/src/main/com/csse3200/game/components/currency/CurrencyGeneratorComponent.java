package com.csse3200.game.components.currency;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.csse3200.game.components.Component;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;

public class CurrencyGeneratorComponent extends Component {
    private final float intervalSec;
    private final int sunValue;
    private final String sunTexturePath;

    /** FALL_FRAC_PER_SEC: Fraction of screen height per second used as falling speed.
     *      e.g. 0.25 -> sun falls 25% of the screen height each second
     *  ROT_SPEED_DPS: Rotating speed in degrees per second.
     */
    private float FALL_FRAC_PER_SEC = 0.1f;
    private float ROT_SPEED_DPS = 100f;
    private float SUN_SIZE_PX = 128f;

    public CurrencyGeneratorComponent() {
        this(8f, 25, "images/normal_sunlight.png");
    }

    public CurrencyGeneratorComponent(float intervalSec, int sunValue, String sunTexturePath) {
        this.intervalSec = Math.max(0.5f, intervalSec);
        this.sunValue = Math.max(1, sunValue);
        this.sunTexturePath = sunTexturePath;
    }

    @Override
    public void create() {
        super.create();
        Stage stage = ServiceLocator.getRenderService().getStage();
        if (stage != null) {
            stage.addAction(Actions.forever(Actions.sequence(
                    Actions.delay(intervalSec),
                    Actions.run(this::spawnOneSunRandom)
            )));
        }
    }

    public void spawnSunAt(float targetX, float targetY) {
        ResourceService rs = ServiceLocator.getResourceService();
        Stage stage = ServiceLocator.getRenderService().getStage();
        if (rs == null || stage == null) return;

        Texture tex = rs.getAsset(sunTexturePath, Texture.class);
        if (tex == null) return;

        CurrencyInteraction sun = new CurrencyInteraction(tex, sunValue);
        sun.setSize(SUN_SIZE_PX, SUN_SIZE_PX);
        sun.setOrigin(SUN_SIZE_PX / 2f, SUN_SIZE_PX / 2f);

        float worldH = stage.getViewport().getWorldHeight();
        float startX = targetX;
        float startY = worldH + SUN_SIZE_PX;
        sun.setPosition(startX, startY);
        stage.addActor(sun);

        // speed (px/s) derived from world height fraction
        float fallSpeedPps = Math.max(1f, worldH * FALL_FRAC_PER_SEC);
        float distance = Math.max(0f, startY - targetY);
        float duration = distance / fallSpeedPps;

        // rotation
        float oneTurn = (ROT_SPEED_DPS <= 0f) ? 0f : (360f / ROT_SPEED_DPS);

        if (duration > 0f) {
            sun.addAction(Actions.parallel(
                    Actions.moveTo(targetX, targetY, duration, Interpolation.sine),
                    Actions.forever(Actions.rotateBy(360f, Math.max(0.01f, oneTurn)))
            ));
        } else {
            sun.setPosition(targetX, targetY);
        }
    }

    private void spawnOneSunRandom() {
        ResourceService rs = ServiceLocator.getResourceService();
        Stage stage = ServiceLocator.getRenderService().getStage();
        if (rs == null || stage == null) return;

        float padding = 32f;
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();

        float targetX = MathUtils.random(padding, Math.max(padding, w - SUN_SIZE_PX - padding));
        float targetY = MathUtils.random(padding, Math.max(padding, h - SUN_SIZE_PX - padding));
        spawnSunAt(targetX, targetY);
    }

    public CurrencyGeneratorComponent setFallFracPerSec(float frac) {
        this.FALL_FRAC_PER_SEC = Math.max(0.01f, frac);
        return this;
    }
    public CurrencyGeneratorComponent setRotatingSpeedDps(float dps) {
        this.ROT_SPEED_DPS = Math.max(0f, dps);
        return this;
    }
    public CurrencyGeneratorComponent setSunSizePx(float px) {
        this.SUN_SIZE_PX = Math.max(8f, px);
        return this;
    }
}
