package com.csse3200.game.profile;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A UI component for displaying the Profile page with navigation buttons.
 */
public class ProfileDisplay extends UIComponent {
    private static final Logger logger = LoggerFactory.getLogger(ProfileDisplay.class);
    private static final float Z_INDEX = 2f;
    private static final int BUTTON_SIZE = 120;
    private static final int CORNER_BUTTON_SIZE = 80;

    @Override
    public void create() {
        super.create();
        addActors();
    }

    private void addActors() {
        // Create charcoal background
        createBackground();
        
        // Create title
        createTitle();
        
        // Create main profile buttons
        createProfileButtons();
        
        // Create corner buttons
        createCornerButtons();
    }

    private void createBackground() {
        // Create charcoal background
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        pixmap.setColor(0.2f, 0.2f, 0.2f, 1f); // Charcoal color
        pixmap.fill();
        
        Texture backgroundTexture = new Texture(pixmap);
        pixmap.dispose();
        
        Image background = new Image(backgroundTexture);
        background.setFillParent(true);
        stage.addActor(background);
    }

    private void createTitle() {
        Label titleLabel = new Label("Profile", skin, "title");
        
        // Center title above the buttons
        float titleX = (stage.getWidth() - titleLabel.getWidth()) / 2f;
        float titleY = stage.getHeight() / 2f + 100f;
        
        titleLabel.setPosition(titleX, titleY);
        stage.addActor(titleLabel);
    }

    private void createProfileButtons() {
        String[] buttonLabels = {"Inventory", "Achievements", "Skills", "Stats"};
        String[] buttonEvents = {"inventory", "achievements", "skills", "stats"};
        
        float centerY = stage.getHeight() / 2f;
        float totalWidth = (BUTTON_SIZE * 4) + (30f * 3); // 4 buttons + 3 gaps of 30px
        float startX = (stage.getWidth() - totalWidth) / 2f;
        
        for (int i = 0; i < 4; i++) {
            TextButton button = createStyledButton(buttonLabels[i]);
            
            float buttonX = startX + (i * (BUTTON_SIZE + 30f));
            float buttonY = centerY - (BUTTON_SIZE / 2f);
            
            button.setPosition(buttonX, buttonY);
            button.setSize(BUTTON_SIZE, BUTTON_SIZE);
            
            // Add click listener
            final String eventName = buttonEvents[i];
            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent changeEvent, Actor actor) {
                    logger.debug("{} button clicked", eventName);
                    entity.getEvents().trigger("profile_" + eventName);
                }
            });
            
            stage.addActor(button);
        }
    }

    private void createCornerButtons() {
        // Back button (top left)
        TextButton backBtn = createStyledButton("Back");
        backBtn.setPosition(20f, stage.getHeight() - CORNER_BUTTON_SIZE - 20f);
        backBtn.setSize(CORNER_BUTTON_SIZE, CORNER_BUTTON_SIZE);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                logger.debug("Back button clicked");
                entity.getEvents().trigger("profile_back");
            }
        });
        stage.addActor(backBtn);

        // Exit button (top right)
        TextButton exitBtn = createStyledButton("Exit");
        exitBtn.setPosition(stage.getWidth() - CORNER_BUTTON_SIZE - 20f, 
                           stage.getHeight() - CORNER_BUTTON_SIZE - 20f);
        exitBtn.setSize(CORNER_BUTTON_SIZE, CORNER_BUTTON_SIZE);
        exitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                logger.debug("Exit button clicked");
                entity.getEvents().trigger("profile_exit");
            }
        });
        stage.addActor(exitBtn);

        // Save button (bottom left)
        TextButton saveBtn = createStyledButton("Save");
        saveBtn.setPosition(20f, 20f);
        saveBtn.setSize(CORNER_BUTTON_SIZE, CORNER_BUTTON_SIZE);
        saveBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                logger.debug("Save button clicked");
                entity.getEvents().trigger("profile_save");
            }
        });
        stage.addActor(saveBtn);

        // Settings button (bottom right)
        TextButton settingsBtn = createStyledButton("Settings");
        settingsBtn.setPosition(stage.getWidth() - CORNER_BUTTON_SIZE - 20f, 20f);
        settingsBtn.setSize(CORNER_BUTTON_SIZE, CORNER_BUTTON_SIZE);
        settingsBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                logger.debug("Settings button clicked");
                entity.getEvents().trigger("profile_settings");
            }
        });
        stage.addActor(settingsBtn);
    }

    private TextButton createStyledButton(String text) {
        // Create light grey background for buttons
        // Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
        // pixmap.setColor(0.7f, 0.7f, 0.7f, 1f); // Light grey
        // pixmap.fill();
        
        // Texture buttonTexture = new Texture(pixmap);
        // pixmap.dispose();
        
        // TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        // buttonStyle.up = new TextureRegionDrawable(buttonTexture);
        // buttonStyle.fontColor = Color.WHITE;
        
        return new TextButton(text, skin);
    }

    @Override
    public void draw(SpriteBatch batch) {
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