package com.csse3200.game.components.mainmenu;

import com.badlogic.gdx.graphics.Texture;
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

/** A UI component for displaying the Main menu (functional version). */
public class MainMenuDisplay extends UIComponent {
    private static final Logger logger = LoggerFactory.getLogger(MainMenuDisplay.class);
    private static final float Z_INDEX = 2f;
    private Table mainTable;
    private Table topRightTable;

    @Override
    public void create() {
        super.create();
        addActors();
    }

    private void addActors() {
        // --- Main Table (center UI for Start + Load) ---
        mainTable = new Table();
        mainTable.setFillParent(true);

        // Title at the top (Vida will replace with logo later)
        Image title =
                new Image(
                        ServiceLocator.getResourceService()
                                .getAsset("images/box_boy_title.png", Texture.class));
        mainTable.add(title).padTop(40f).center();
        mainTable.row();

        // Start + Load buttons
        TextButton startBtn = new TextButton("Start Game", skin);
        TextButton loadBtn = new TextButton("Load Game", skin);

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

        mainTable.add(startBtn).padTop(30f).center();
        mainTable.row();
        mainTable.add(loadBtn).padTop(15f).center();

        // --- Top-right Table (Exit + Settings gear) ---
        topRightTable = new Table();
        topRightTable.top().right();

        // Exit button
        TextButton exitBtn = new TextButton("Exit", skin);
        exitBtn.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        logger.debug("Exit button clicked");
                        entity.getEvents().trigger("exit");
                    }
                });

        // Settings gear icon
        Image settingsIcon =
                new Image(
                        ServiceLocator.getResourceService()
                                .getAsset("images/settings_icon.png", Texture.class));
        settingsIcon.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent changeEvent, Actor actor) {
                        logger.debug("Settings icon clicked");
                        entity.getEvents().trigger("settings");
                    }
                });

        // Add both to the same row in top-right
        topRightTable.add(settingsIcon).pad(20f).size(48f, 48f);
        topRightTable.add(exitBtn).pad(20f);

        // Add tables to stage
        stage.addActor(mainTable);
        stage.addActor(topRightTable);
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
        mainTable.clear();
        topRightTable.clear();
        super.dispose();
    }
}
