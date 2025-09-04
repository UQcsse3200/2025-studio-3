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

    public CurrencyGeneratorComponent() {
        this(5f, 25, "images/normal_sunlight.png");
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
                    Actions.run(() -> spawnOneSun())
            )));
        }
    }

    private void spawnOneSun() {
        ResourceService rs = ServiceLocator.getResourceService();
        Stage stage = ServiceLocator.getRenderService().getStage();
        if (rs == null || stage == null) return;

        Texture tex = rs.getAsset(sunTexturePath, Texture.class);
        if (tex == null) return;

        CurrencyInteraction sun = new CurrencyInteraction(tex, sunValue);

        float padding = 32f;
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();
        float x = MathUtils.random(padding, Math.max(padding, w - sun.getWidth() - padding));
        float y = MathUtils.random(padding, Math.max(padding, h - sun.getHeight() - padding));
        sun.setPosition(x, y);

        float dur = 0.25f;
        sun.getColor().a = 0f;
        sun.setScale(0.7f);
        sun.addAction(Actions.parallel(
                Actions.fadeIn(dur),
                Actions.scaleTo(1f, 1f, dur, Interpolation.pow2)
        ));

        stage.addActor(sun);
    }
}
