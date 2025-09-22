package com.csse3200.game.components.mainmenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.ButtonFactory;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainMenuDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(MainMenuDisplay.class);
  private static final float Z_INDEX = 2f;
  private Table table;

  @Override
  public void create() {
    super.create();

    // Load assets
    ServiceLocator.getResourceService().loadAll();

    addActors();
  }

  private void addActors() {
    table = new Table();
    table.setFillParent(true);

    // Title
    Image title =
        new Image(
            ServiceLocator.getResourceService()
                .getAsset("images/backgrounds/bg-text.png", Texture.class));

    TextButton newGameBtn = ButtonFactory.createButton("New Game");
    TextButton loadBtn = ButtonFactory.createButton("Load Game");
    TextButton settingsBtn = ButtonFactory.createButton("Settings");
    TextButton exitBtn = ButtonFactory.createButton("Exit Game");

    // Button listeners
    newGameBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("[MainMenuDisplay] New Game button clicked");
            entity.getEvents().trigger("start");
          }
        });

    loadBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("[MainMenuDisplay] Load Game button clicked");
            entity.getEvents().trigger("load");
          }
        });

    settingsBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("[MainMenuDisplay] Settings button clicked");
            entity.getEvents().trigger("settings");
          }
        });

    exitBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("[MainMenuDisplay] Exit Game button clicked");
            entity.getEvents().trigger("exit");
          }
        });

    // Layout
    table.center();
    float xf = 0.40f;
    table
        .add(title)
        .size(title.getWidth() * xf, title.getHeight() * xf)
        .top()
        .center()
        .padTop(20f)
        .padBottom(20f);
    table.row();

    float buttonWidth = 200f;
    float buttonHeight = 50f;

    table.add(newGameBtn).size(buttonWidth, buttonHeight).padBottom(5f);
    table.row();
    table.add(loadBtn).size(buttonWidth, buttonHeight).padBottom(5f);
    table.row();
    table.add(settingsBtn).size(buttonWidth, buttonHeight).padBottom(5f);
    table.row();
    table.add(exitBtn).size(buttonWidth, buttonHeight).padBottom(5f);

    // Add actors
    stage.addActor(table);
  }

  @Override
  public void draw(SpriteBatch batch) {
    // Drawing is handled by the stage
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
