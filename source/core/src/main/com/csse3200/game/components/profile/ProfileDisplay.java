package com.csse3200.game.components.profile;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** A UI component for displaying the Profile page with navigation buttons. */
public class ProfileDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(ProfileDisplay.class);
  private static final float Z_INDEX = 2f;

  // Use relative sizing for responsiveness
  private static final float MAIN_BUTTON_WIDTH_RATIO = 0.16f;
  private static final float MAIN_BUTTON_HEIGHT_RATIO = 0.2f;
  private static final float CORNER_BUTTON_WIDTH_RATIO = 0.1f;
  private static final float CORNER_BUTTON_HEIGHT_RATIO = 0.07f;
  private static final float PADDING_RATIO = 0.02f;
  private static final float BUTTON_SPACING_RATIO = 0.03f;

  private Table mainTable;
  private Table cornerTable;

  @Override
  public void create() {
    super.create();
    addActors();
  }

  private void addActors() {
    // Create charcoal background
    createBackground();

    // Create main container table
    mainTable = new Table();
    mainTable.setFillParent(true);
    stage.addActor(mainTable);

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
    titleLabel.setAlignment(Align.center);

    mainTable.add(titleLabel).padBottom(stage.getHeight() * 0.1f).expandX().row();
  }

  private void createProfileButtons() {
    String[] buttonLabels = {"Inventory", "Achievements", "Skills", "Stats", "Shop", "Dossier"};
    String[] buttonEvents = {"inventory", "achievements", "skills", "stats", "shop", "dossier"};

    Table buttonTable = new Table();
    buttonTable.defaults().spaceBottom(stage.getHeight() * BUTTON_SPACING_RATIO);

    // Calculate button dimensions based on screen size
    float buttonWidth = stage.getWidth() * MAIN_BUTTON_WIDTH_RATIO;
    float buttonHeight = stage.getHeight() * MAIN_BUTTON_HEIGHT_RATIO;

    for (int i = 0; i < buttonLabels.length; i++) {
      TextButton button = new TextButton(buttonLabels[i], skin);
      button.getLabel().setWrap(true);

      // Add click listener
      final String eventName = buttonEvents[i];
      button.addListener(
          new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
              logger.debug("{} button clicked", eventName);
              entity.getEvents().trigger("profile_" + eventName);
            }
          });

      // Add button to table with spacing
      buttonTable
          .add(button)
          .width(buttonWidth - 50)
          .height(buttonHeight - 50)
          .padRight(i < buttonLabels.length - 1 ? stage.getWidth() * BUTTON_SPACING_RATIO : 0);
    }

    mainTable.add(buttonTable).center().row();
    mainTable.debug();
  }

  private void createCornerButtons() {
    cornerTable = new Table();
    cornerTable.setFillParent(true);
    stage.addActor(cornerTable);

    // Calculate button dimensions based on screen size
    float buttonWidth = stage.getWidth() * CORNER_BUTTON_WIDTH_RATIO;
    float buttonHeight = stage.getHeight() * CORNER_BUTTON_HEIGHT_RATIO;
    float padding = stage.getWidth() * PADDING_RATIO;

    // Back button (top left)
    TextButton backBtn = new TextButton("Back", skin);
    backBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Back button clicked");
            entity.getEvents().trigger("profile_back");
          }
        });

    // Exit button (top right)
    TextButton exitBtn = new TextButton("Exit", skin);
    exitBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Exit button clicked");
            entity.getEvents().trigger("profile_exit");
          }
        });

    // Save button (bottom left)
    TextButton saveBtn = new TextButton("Save", skin);
    saveBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Save button clicked");
            entity.getEvents().trigger("profile_save");
          }
        });

    // Settings button (bottom right)
    TextButton settingsBtn = new TextButton("Settings", skin);
    settingsBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Settings button clicked");
            entity.getEvents().trigger("profile_settings");
          }
        });

    // Position buttons in corners using the table
    cornerTable.add(backBtn).width(buttonWidth).height(buttonHeight).pad(padding).top().left();
    cornerTable.add().expandX();
    cornerTable.add(exitBtn).width(buttonWidth).height(buttonHeight).pad(padding).top().right();
    cornerTable.row();
    cornerTable.add().expandY();
    cornerTable.row();
    cornerTable.add(saveBtn).width(buttonWidth).height(buttonHeight).pad(padding).bottom().left();
    cornerTable.add().expandX();
    cornerTable
        .add(settingsBtn)
        .width(buttonWidth)
        .height(buttonHeight)
        .pad(padding)
        .bottom()
        .right();
  }

  @Override
  public void draw(SpriteBatch batch) {
    // Do nothing, handled by the stage
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void dispose() {
    if (mainTable != null) {
      mainTable.clear();
    }
    if (cornerTable != null) {
      cornerTable.clear();
    }
    super.dispose();
  }

  /** Call this method when the screen is resized to update UI elements */
  public void resize() {
    if (mainTable != null) {
      mainTable.clear();
    }
    if (cornerTable != null) {
      cornerTable.clear();
    }
    stage.clear();
    addActors();
  }
}
