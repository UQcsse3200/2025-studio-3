package com.csse3200.game.components.settingsmenu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import java.util.HashMap;
import java.util.Map;
import net.dermetfan.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Game settings menu component. */
public class GameSettingsMenu extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(GameSettingsMenu.class);
  private Table rootTable;
  private Table bottomRow;
  private SelectBox<String> difficultySelect;
  private Map<String, Integer> keybinds = new HashMap<>();
  private static final String PAUSE_KEY = "pause";
  private static final String SKIP_KEY = "skip";
  private static final String INTERACTION_KEY = "interaction";
  private static final String UP_KEY = "up";
  private static final String DOWN_KEY = "down";
  private static final String LEFT_KEY = "left";
  private static final String RIGHT_KEY = "right";

  /** Constructor for GameSettingsMenu. */
  public GameSettingsMenu() {
    super();
  }

  @Override
  public void create() {
    super.create();
    addActors();
    entity.getEvents().addListener("backtosettingsmenu", this::hideMenu);
    entity.getEvents().addListener("gamesettings", this::showMenu);
    entity.getEvents().addListener("displaysettings", this::hideMenu);
    entity.getEvents().addListener("audiosettings", this::hideMenu);
    bottomRow.setVisible(false);
    rootTable.setVisible(false);
  }

  /** Add actors to the UI. */
  private void addActors() {
    rootTable = new Table();
    rootTable.setFillParent(true);
    rootTable.center(); // Center the entire table content

    // Create title with proper UI scaling
    Label title = ui.title("Game Settings");
    float uiScale = ui.getUIScale();
    rootTable.add(title).padTop(30f * uiScale).center().colspan(2);
    rootTable.row().padTop(30f * uiScale);

    Settings settings = ServiceLocator.getSettingsService().getSettings();

    // Create labels using UIFactory
    Label pauseLabel = ui.subheading("Pause Key:");
    keybinds.put(PAUSE_KEY, settings.getPauseButton());
    TextField pauseKeyText = ui.createTextField(Input.Keys.toString(settings.getPauseButton()));
    pauseKeyText.setName(PAUSE_KEY);
    setupKeybindTextField(pauseKeyText);

    Label skipLabel = ui.subheading("Skip Key:");
    keybinds.put(SKIP_KEY, settings.getSkipButton());
    TextField skipKeyText = ui.createTextField(Input.Keys.toString(settings.getSkipButton()));
    skipKeyText.setName(SKIP_KEY);
    setupKeybindTextField(skipKeyText);

    Label interactionLabel = ui.subheading("Interaction Key:");
    keybinds.put(INTERACTION_KEY, settings.getInteractionButton());
    TextField interactionKeyText =
        ui.createTextField(Input.Keys.toString(settings.getInteractionButton()));
    interactionKeyText.setName(INTERACTION_KEY);
    setupKeybindTextField(interactionKeyText);

    Label upLabel = ui.subheading("Up Key:");
    keybinds.put(UP_KEY, settings.getUpButton());
    TextField upKeyText = ui.createTextField(Input.Keys.toString(settings.getUpButton()));
    upKeyText.setName(UP_KEY);
    setupKeybindTextField(upKeyText);

    Label downLabel = ui.subheading("Down Key:");
    keybinds.put(DOWN_KEY, settings.getDownButton());
    TextField downKeyText = ui.createTextField(Input.Keys.toString(settings.getDownButton()));
    downKeyText.setName(DOWN_KEY);
    setupKeybindTextField(downKeyText);

    Label leftLabel = ui.subheading("Left Key:");
    keybinds.put(LEFT_KEY, settings.getLeftButton());
    TextField leftKeyText = ui.createTextField(Input.Keys.toString(settings.getLeftButton()));
    leftKeyText.setName(LEFT_KEY);
    setupKeybindTextField(leftKeyText);

    Label rightLabel = ui.subheading("Right Key:");
    keybinds.put(RIGHT_KEY, settings.getRightButton());
    TextField rightKeyText = ui.createTextField(Input.Keys.toString(settings.getRightButton()));
    rightKeyText.setName(RIGHT_KEY);
    setupKeybindTextField(rightKeyText);

    Label difficultyLabel = ui.subheading("Difficulty:");
    difficultySelect = ui.createSelectBox(new String[] {"EASY", "NORMAL", "HARD"});
    difficultySelect.setSelected(settings.getDifficulty().toString());

    // Create apply button using UIFactory
    int buttonWidth = 150;
    TextButton applyBtn = ui.primaryButton("Apply", buttonWidth);
    Pair<Float, Float> buttonDimensions = ui.getScaledDimensions(buttonWidth);
    applyBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            logger.debug("Apply button clicked");
            applyChanges();
            entity.getEvents().trigger("backtosettingsmenu");
          }
        });

    // Layout with proper UI scaling
    rootTable.add(pauseLabel).left().padRight(20f * uiScale);
    rootTable.add(pauseKeyText).width(150f * uiScale).center();
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(skipLabel).left().padRight(25f * uiScale);
    rootTable.add(skipKeyText).width(150f * uiScale).center();
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(interactionLabel).left().padRight(25f * uiScale);
    rootTable.add(interactionKeyText).width(150f * uiScale).center();
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(upLabel).left().padRight(25f * uiScale);
    rootTable.add(upKeyText).width(150f * uiScale).center();
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(downLabel).left().padRight(25f * uiScale);
    rootTable.add(downKeyText).width(150f * uiScale).center();
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(leftLabel).left().padRight(25f * uiScale);
    rootTable.add(leftKeyText).width(150f * uiScale).center();
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(rightLabel).left().padRight(25f * uiScale);
    rootTable.add(rightKeyText).width(150f * uiScale).center();
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(difficultyLabel).left().padRight(25f * uiScale);
    rootTable.add(difficultySelect).width(150f * uiScale).center();
    rootTable.row().padTop(20f * uiScale);
    stage.addActor(rootTable);

    bottomRow = new Table();
    bottomRow.setFillParent(true);
    bottomRow.bottom().padBottom(20f * uiScale);
    bottomRow
        .add(applyBtn)
        .width(buttonDimensions.getKey())
        .height(buttonDimensions.getValue())
        .center();
    stage.addActor(bottomRow);
  }

  /**
   * Setup a TextField for keybind input with focus and key listeners.
   *
   * @param textField The TextField to setup
   */
  private void setupKeybindTextField(TextField textField) {
    // Add focus listener to clear text when focused
    textField.addListener(
        new FocusListener() {
          @Override
          public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
            if (focused) {
              textField.setText("");
            }
          }
        });

    // Add key input listener to capture single key presses
    textField.addListener(
        new InputListener() {
          @Override
          public boolean keyDown(InputEvent event, int keycode) {
            if (textField.hasKeyboardFocus()) {
              // Update the text field with the new key
              if (keybinds.containsValue(keycode)) {
                logger.info("Keybind conflict: {}", keycode);
                ServiceLocator.getDialogService()
                    .error("Keybind conflict", "This key is already in use by another action.");
                textField.setText(Input.Keys.toString(keybinds.get(textField.getName())));
                textField.setFocusTraversal(false);
                stage.setKeyboardFocus(null);
                return false;
              }
              logger.info("Keybind not conflict: {}", keycode);
              // to fix
              keybinds.put(textField.getName(), keycode);
              textField.setText(Input.Keys.toString(keycode));
              textField.setFocusTraversal(false);
              stage.setKeyboardFocus(null);
              return true;
            }
            return false;
          }
        });
  }

  /** Apply changes to the game settings. */
  private void applyChanges() {
    logger.info("[GameSettingsMenu] Applying game settings");

    Settings settings = ServiceLocator.getSettingsService().getSettings();

    // Apply difficulty changes
    if (difficultySelect != null) {
      String difficulty = difficultySelect.getSelected();
      switch (difficulty) {
        case "EASY":
          settings.setDifficulty(Settings.Difficulty.EASY);
          break;
        case "NORMAL":
          settings.setDifficulty(Settings.Difficulty.NORMAL);
          break;
        case "HARD":
          settings.setDifficulty(Settings.Difficulty.HARD);
          break;
        default:
          break;
      }
    }

    ServiceLocator.getSettingsService()
        .changeKeybinds(
            keybinds.get(PAUSE_KEY),
            keybinds.get(SKIP_KEY),
            keybinds.get(INTERACTION_KEY),
            keybinds.get(UP_KEY),
            keybinds.get(DOWN_KEY),
            keybinds.get(LEFT_KEY),
            keybinds.get(RIGHT_KEY));
    logger.info("[GameSettingsMenu] New Keybinds: {}", keybinds);
    ServiceLocator.getSettingsService().saveSettings();
    logger.info("[GameSettingsMenu] Game settings applied");
  }

  /** Show the game settings menu. */
  private void showMenu() {
    rootTable.setVisible(true);
    bottomRow.setVisible(true);
  }

  /** Hide the game settings menu. */
  private void hideMenu() {
    rootTable.setVisible(false);
    bottomRow.setVisible(false);
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  @Override
  public void dispose() {
    rootTable.clear();
    bottomRow.clear();
    super.dispose();
  }
}
