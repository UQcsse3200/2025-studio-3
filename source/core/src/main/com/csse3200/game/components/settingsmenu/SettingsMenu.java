package com.csse3200.game.components.settingsmenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.GdxGame;
import com.csse3200.game.GdxGame.ScreenType;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import net.dermetfan.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Main settings menu component. */
public class SettingsMenu extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(SettingsMenu.class);
  private final GdxGame game;
  private Table rootTable;
  private Table exitBtn;

  /**
   * Constructor for SettingsMenu.
   *
   * @param game The game instance.
   */
  public SettingsMenu(GdxGame game) {
    super();
    this.game = game;
  }

  @Override
  public void create() {
    super.create();
    addActors();
    entity.getEvents().addListener("backtosettingsmenu", this::showMenu);
    entity.getEvents().addListener("gamesettings", this::hideMenu);
    entity.getEvents().addListener("displaysettings", this::hideMenu);
    entity.getEvents().addListener("audiosettings", this::hideMenu);
  }

  /** Add actors to the UI. */
  private void addActors() {
    makeCloseBtn();
    rootTable = new Table();
    rootTable.setFillParent(true);

    // Create title with proper UI scaling
    Label title = ui.title("Settings");
    float uiScale = ui.getUIScale();
    rootTable.add(title).expandX().center().padTop(30f * uiScale);
    rootTable.row().padTop(30f * uiScale);

    // Create main menu buttons using UIFactory with proper scaling
    int buttonWidth = 300;
    TextButton displayBtn = ui.primaryButton("Display Settings", buttonWidth);
    TextButton gameBtn = ui.primaryButton("Game Settings", buttonWidth);
    TextButton audioBtn = ui.primaryButton("Audio Settings", buttonWidth);

    // Get scaled dimensions for consistent button sizing
    Pair<Float, Float> buttonDimensions = ui.getScaledDimensions(buttonWidth);

    displayBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            entity.getEvents().trigger("displaysettings");
          }
        });

    gameBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            entity.getEvents().trigger("gamesettings");
          }
        });

    audioBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            entity.getEvents().trigger("audiosettings");
          }
        });

    // Add buttons to the main table with proper scaling
    rootTable
        .add(displayBtn)
        .width(buttonDimensions.getKey())
        .height(buttonDimensions.getValue())
        .padBottom(20f * uiScale);
    rootTable.row();
    rootTable
        .add(gameBtn)
        .width(buttonDimensions.getKey())
        .height(buttonDimensions.getValue())
        .padBottom(20f * uiScale);
    rootTable.row();
    rootTable.add(audioBtn).width(buttonDimensions.getKey()).height(buttonDimensions.getValue());

    // Center the table content
    rootTable.center();

    stage.addActor(rootTable);
  }

  /** Make the close button. */
  private void makeCloseBtn() {
    float uiScale = ui.getUIScale();
    exitBtn =
        new ImageButton(
            new TextureRegionDrawable(
                ServiceLocator.getGlobalResourceService()
                    .getAsset("images/ui/close-icon.png", Texture.class)));
    exitBtn.setSize(60f * uiScale, 60f * uiScale);
    exitBtn.setPosition(20f * uiScale, stage.getHeight() - 60f * uiScale - 20f * uiScale);
    exitBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Exit button clicked");
            exitMenu();
          }
        });
    stage.addActor(exitBtn);
  }

  /** Exit the menu. */
  private void exitMenu() {
    if (!ServiceLocator.getProfileService().isActive()) {
      game.setScreen(ScreenType.MAIN_MENU);
    } else {
      game.setScreen(ScreenType.WORLD_MAP);
    }
  }

  /** Show the menu. */
  private void showMenu() {
    rootTable.setVisible(true);
    exitBtn.setVisible(true);
  }

  /** Hide the menu. */
  private void hideMenu() {
    rootTable.setVisible(false);
    exitBtn.setVisible(false);
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // draw
  }

  @Override
  public void update() {
    super.update();
    float uiScale = ui.getUIScale();
    exitBtn.setPosition(20f * uiScale, stage.getHeight() - 60f * uiScale - 20f * uiScale);
  }

  @Override
  public void dispose() {
    rootTable.clear();
    super.dispose();
  }
}
