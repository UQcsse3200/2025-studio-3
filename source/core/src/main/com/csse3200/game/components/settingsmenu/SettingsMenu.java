package com.csse3200.game.components.settingsmenu;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.csse3200.game.GdxGame;
import com.csse3200.game.GdxGame.ScreenType;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import net.dermetfan.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Main settings menu component. */
public class SettingsMenu extends UIComponent {
  private final GdxGame game;
  private Table rootTable;
  private TextButton exitBtn;
  private static final Logger logger = LoggerFactory.getLogger(SettingsMenu.class);

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
    entity.getEvents().addListener("back", this::exitMenu);
    entity.getEvents().addListener("backtosettingsmenu", this::showMenu);
    entity.getEvents().addListener("gamesettings", this::hideMenu);
    entity.getEvents().addListener("displaysettings", this::hideMenu);
    entity.getEvents().addListener("audiosettings", this::hideMenu);
    entity.getEvents().addListener("displayneedsupdate", this::rebuild);
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
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            entity.getEvents().trigger("displaysettings");
          }
        });

    gameBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            entity.getEvents().trigger("gamesettings");
          }
        });

    audioBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
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
    exitBtn = ui.createBackButton(entity.getEvents(), stage.getHeight());
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

  /**
   * Reinitialises and updates the settings menu by disposing of the current Settings screen and
   * setting it to a new Settings screen. This rebuilds the screen with the new Display settings.
   */
  private void rebuild() {
    game.setScreen(ScreenType.SETTINGS);
    logger.info("Set screen to new Settings Screen with updated Display settings");
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // draw
  }

  @Override
  public void dispose() {
    rootTable.clear();
    stage.dispose();
    super.dispose();
  }
}
