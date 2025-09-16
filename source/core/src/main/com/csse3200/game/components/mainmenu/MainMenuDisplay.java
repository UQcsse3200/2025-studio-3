package com.csse3200.game.components.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A ui component for displaying the Main menu.
 */
public class MainMenuDisplay extends UIComponent {
    private static final Logger logger = LoggerFactory.getLogger(MainMenuDisplay.class);
    private static final float Z_INDEX = 2f;
    private Table table;

    @Override
    public void create() {
        super.create();

        // Load the button atlas first
        ServiceLocator.getResourceService().loadTextureAtlases(new String[] { "images/btn-blue.atlas" });
//        ServiceLocator.getResourceService().loadFonts(new String[] { "flat-earth/skin/fonts/pixel_32.fnt" });
        ServiceLocator.getResourceService().loadAll(); // blocks until finished

        addActors();
    }

    private TextButton makeTexturedButton(String text, TextureAtlas atlas, String region) {
        Drawable background = new TextureRegionDrawable(atlas.findRegion(region));

        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.5f);
        font.setColor(Color.BLACK);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = background;
        style.down = background;   // you can replace later with a "pressed" region
        style.over = background;   // you can replace later with a "hover" region
        style.font = font; // default font

        TextButton button = new TextButton(text, style);
        button.getLabel().setColor(new Color(0.1f, 0.1f, 0.1f, 1f));
        button.getLabel().setAlignment(com.badlogic.gdx.utils.Align.center);
        return button;
    }



    private void addActors() {
        table = new Table();
        table.setFillParent(true);
        Image title =
                new Image(
                        ServiceLocator.getResourceService()
                                .getAsset("images/bg-text.png", Texture.class));

        logger.debug("show background title");

        TextureAtlas buttonAtlas = ServiceLocator.getResourceService().getAsset("images/btn-blue.atlas", TextureAtlas.class);

        TextButton startBtn = makeTexturedButton("Start", buttonAtlas, "default");
        TextButton loadBtn = makeTexturedButton("Load", buttonAtlas, "default");
        TextButton worldMapBtn = makeTexturedButton("World Map", buttonAtlas, "default");
        TextButton settingsBtn = makeTexturedButton("Settings", buttonAtlas, "default");
        TextButton exitBtn = makeTexturedButton("Exit", buttonAtlas, "default");

        // Triggers an event when the button is pressed
        startBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        logger.debug("Start button clicked");
                        entity.getEvents().trigger("start");
                    }
                });

        loadBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        logger.debug("Load button clicked");
                        entity.getEvents().trigger("load");
                    }
                });

        settingsBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        logger.debug("Settings button clicked");
                        entity.getEvents().trigger("settings");
                    }
                });

        worldMapBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                logger.debug("World Map button clicked");
                //entity.getEvents().trigger("world_map");
            }
        });

        exitBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {

                        logger.debug("Exit button clicked");
                        entity.getEvents().trigger("exit");
                    }
                });

        table.center();

        // add title
        float xf = 0.40f;

        table.add(title)
                .size(title.getWidth() * xf, title.getHeight() * xf) // scale the cell
                .top()  // vertical alignment
                .center() // horizontal alignment
                .padTop(20f)
                .padBottom(20f); // optional padding
        table.row();

        // add buttons with consistent width and spacing
        float buttonWidth = 200f;
        float buttonHeight = 50f;

        table.add(startBtn).size(buttonWidth, buttonHeight).padBottom(2f);
        table.row();
        table.add(loadBtn).size(buttonWidth, buttonHeight).padBottom(2f);
        table.row();
        table.add(settingsBtn).size(buttonWidth, buttonHeight).padBottom(2f);
        table.row();
        table.add(worldMapBtn).size(buttonWidth, buttonHeight).padBottom(2f);
        table.row();
        table.add(exitBtn).size(buttonWidth, buttonHeight).padBottom(2f);

        stage.addActor(table);
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
        table.clear();
        super.dispose();
    }
}
