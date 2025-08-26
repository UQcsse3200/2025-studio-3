package com.csse3200.game.components.shop;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

/**
 * 
 */
public class ShopDisplay extends UIComponent {
    private static final Logger logger = LoggerFactory.getLogger(ShopDisplay.class);
    private static final float Z_INDEX = 2f;

    /**
     * Creates the shop display.
     */
    @Override
    public void create() {
        super.create();
        addActors();
    }

    private void addActors() {
        Image backgroundImage = new Image(
                ServiceLocator.getResourceService()
                        .getAsset("images/shopbackground.png", Texture.class));

        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);
    }

    @Override
    public void draw(SpriteBatch batch) {
        // draw is handled by the stage
    }

    @Override
    public float getZIndex() {
        return Z_INDEX;
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
