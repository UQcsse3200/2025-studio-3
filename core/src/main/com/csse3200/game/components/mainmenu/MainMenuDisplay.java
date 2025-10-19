package com.csse3200.game.components.mainmenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import net.dermetfan.utils.Pair;
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

    int buttonWidth = 300;
    TextButton newGameBtn = ui.primaryButton("New Game", buttonWidth);
    TextButton loadBtn = ui.primaryButton("Load Game", buttonWidth);
    TextButton settingsBtn = ui.primaryButton("Settings", buttonWidth);
    TextButton exitBtn = ui.primaryButton("Exit Game", buttonWidth);
    Pair<Float, Float> buttonDimensions = ui.getScaledDimensions(buttonWidth);
    float uiScale = ui.getUIScale();

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
    table
        .add(title)
        .size(title.getWidth() * 0.5f * uiScale, title.getHeight() * 0.5f * uiScale)
        .top()
        .center()
        .padTop(20f * uiScale)
        .padBottom(60f * uiScale);
    table.row();

    // Get UI scale from settings

    table
        .add(newGameBtn)
        .width(buttonDimensions.getKey())
        .height(buttonDimensions.getValue())
        .padBottom(10f);
    table.row();
    table
        .add(loadBtn)
        .width(buttonDimensions.getKey())
        .height(buttonDimensions.getValue())
        .padBottom(10f);
    table.row();
    table
        .add(settingsBtn)
        .width(buttonDimensions.getKey())
        .height(buttonDimensions.getValue())
        .padBottom(10f);
    table.row();
    table
        .add(exitBtn)
        .width(buttonDimensions.getKey())
        .height(buttonDimensions.getValue())
        .padBottom(10f);

    // Add actors
    stage.addActor(table);
  }

  @Override
  public void resize() {
    super.resize();
    table.clear();
    addActors();
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
