package com.csse3200.game.components.currency;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.math.Interpolation;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CurrencyInteraction extends Image {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyInteraction.class);

    private final int value;

    public CurrencyInteraction(Texture texture, int value) {
        super(texture);
        this.value = value;
        logger.debug("CurrencyInteraction created");

        setSize(128, 128);
        setOrigin(getWidth()/ 2f, getHeight()/2f);
        setColor(getColor().r, getColor().g, getColor().b, 1f);
        setTouchable(Touchable.enabled);
        setBounds(getX(), getY(), getWidth(), getHeight());

        addListener(new ClickListener() {
            {
                setTapSquareSize(18f);
            }
            @Override public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                event.stop();
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.stop();
                onClicked();
            }
        });
    }

    private void onClicked() {
        logger.debug("CurrencyInteraction onClicked");
        setTouchable(Touchable.disabled);
        clearActions();
        float padding = 16f;
        float targetX = padding;
        float targetY = getStage().getHeight() - padding - getHeight();

        float dur = 0.35f;
        addAction(Actions.sequence(
                Actions.parallel(
                        Actions.moveTo(targetX, targetY, dur, Interpolation.pow2In),
                        Actions.scaleTo(0.6f, 0.6f, dur),
                        Actions.fadeOut(dur)
                ),
                Actions.run(() -> {
                    ServiceLocator.getCurrencyService().add(value);
                    dispose();
                })
        ));
    }

    public void dispose() {
        clearActions();
        clearListeners();
        remove();
    }
}
