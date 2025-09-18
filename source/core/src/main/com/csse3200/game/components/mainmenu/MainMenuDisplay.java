package com.csse3200.game.components.mainmenu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
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
    ServiceLocator.getResourceService()
        .loadTextureAtlases(new String[] {"images/ui/btn-blue.atlas"});
    ServiceLocator.getResourceService().loadTextures(new String[] {"images/ui/settings_icon.png"});
    ServiceLocator.getResourceService().loadAll();

    addActors();
  }

  private TextButton makeTexturedButton(String text, TextureAtlas atlas, String region) {
    Drawable background = new TextureRegionDrawable(atlas.findRegion(region));

    BitmapFont font = new BitmapFont();
    font.getData().setScale(1.5f);
    font.setColor(Color.BLACK);

    TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
    style.up = background;
    style.down = background;
    style.over = background;
    style.font = font;

    TextButton button = new TextButton(text, style);
    button.getLabel().setColor(new Color(0.1f, 0.1f, 0.1f, 1f));
    return button;
  }

  private void addActors() {
    table = new Table();
    table.setFillParent(true);

    // Title
    Image title =
        new Image(
            ServiceLocator.getResourceService()
                .getAsset("images/backgrounds/bg-text.png", Texture.class));

    TextureAtlas buttonAtlas =
        ServiceLocator.getResourceService()
            .getAsset("images/ui/btn-blue.atlas", TextureAtlas.class);

    TextButton startBtn = makeTexturedButton("Start", buttonAtlas, "default");
    TextButton quickStartBtn = makeTexturedButton("Quick Start", buttonAtlas, "default");
    TextButton loadBtn = makeTexturedButton("Load", buttonAtlas, "default");
    TextButton miniGameBtn = makeTexturedButton("MiniGames", buttonAtlas, "default");
    TextButton cutBtn = makeTexturedButton("Cutscene", buttonAtlas, "default");
    TextButton exitBtn = makeTexturedButton("Exit", buttonAtlas, "default");

    // --- Settings gear icon ---
    Texture gearTexture =
        ServiceLocator.getResourceService().getAsset("images/ui/settings_icon.png", Texture.class);
    ImageButton.ImageButtonStyle gearStyle = new ImageButton.ImageButtonStyle();
    gearStyle.imageUp = new TextureRegionDrawable(gearTexture);

    ImageButton settingsIcon = new ImageButton(gearStyle);
    settingsIcon.setSize(90f, 90f);
    settingsIcon.setPosition(Gdx.graphics.getWidth() - 100f, Gdx.graphics.getHeight() - 120f);

    settingsIcon.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Settings icon clicked");
            entity.getEvents().trigger("settings");
          }
        });

    // Button listeners
    startBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Start button clicked");
            entity.getEvents().trigger("worldMap");
          }
        });

    quickStartBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Quick Start button clicked");
            entity.getEvents().trigger("quickStart");
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

    miniGameBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("MiniGames button clicked");
            entity.getEvents().trigger("minigame");
          }
        });

    cutBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Cutscene button clicked");
            entity.getEvents().trigger("Cutscene");
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

    table.add(startBtn).size(buttonWidth, buttonHeight).padBottom(5f);
    table.row();
    table.add(quickStartBtn).size(buttonWidth, buttonHeight).padBottom(5f);
    table.row();
    table.add(loadBtn).size(buttonWidth, buttonHeight).padBottom(5f);
    table.row();
    table.add(miniGameBtn).size(buttonWidth, buttonHeight).padBottom(5f); // moved into main flow
    table.row();
    table.add(cutBtn).size(buttonWidth, buttonHeight).padBottom(5f);
    table.row();
    table.add(exitBtn).size(buttonWidth, buttonHeight).padBottom(5f);

    // Add actors
    stage.addActor(table);
    stage.addActor(settingsIcon);
  }

  @Override
  public void draw(SpriteBatch batch) {}

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
