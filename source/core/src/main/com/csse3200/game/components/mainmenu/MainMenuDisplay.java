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

    TextButton newGameBtn = ui.primaryButton("New Game", 200f);
    TextButton loadBtn = ui.primaryButton("Load Game", 200f);
    TextButton settingsBtn = ui.primaryButton("Settings", 200f);
    TextButton exitBtn = ui.primaryButton("Exit Game", 200f);

    // Button listeners
    newGameBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.info("[MainMenuDisplay] New Game button clicked");
            entity.getEvents().trigger("start");
          }
        });

    loadBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.info("[MainMenuDisplay] Load Game button clicked");
            entity.getEvents().trigger("load");
          }
        });

    settingsBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.info("[MainMenuDisplay] Settings button clicked");
            entity.getEvents().trigger("settings");
          }
        });

    exitBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.info("[MainMenuDisplay] Exit Game button clicked");
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

    table.add(newGameBtn).padBottom(5f);
    table.row();
    table.add(loadBtn).padBottom(5f);
    table.row();
    table.add(settingsBtn).padBottom(5f);
    table.row();
    table.add(exitBtn).padBottom(5f);

    // Add actors
    stage.addActor(table);
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
