package com.csse3200.game.components.settingsmenu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.ButtonFactory;
import com.csse3200.game.ui.TypographyFactory;
import com.csse3200.game.ui.UIComponent;
import java.util.HashMap;
import java.util.Map;
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

    // Create title
    Label title = TypographyFactory.createTitle("Game Settings");
    rootTable.add(title).padTop(30f).center().colspan(2);
    rootTable.row().padTop(30f);

    Settings settings = ServiceLocator.getSettingsService().getSettings();

    Label pauseLabel = new Label("Pause Key:", skin);
    keybinds.put(PAUSE_KEY, settings.getPauseButton());
    TextField pauseKeyText = new TextField(Input.Keys.toString(settings.getPauseButton()), skin);
    pauseKeyText.setName(PAUSE_KEY);
    whiten(pauseLabel);
    setupKeybindTextField(pauseKeyText);

    Label skipLabel = new Label("Skip Key:", skin);
    keybinds.put(SKIP_KEY, settings.getSkipButton());
    TextField skipKeyText = new TextField(Input.Keys.toString(settings.getSkipButton()), skin);
    skipKeyText.setName(SKIP_KEY);
    whiten(skipLabel);
    setupKeybindTextField(skipKeyText);

    Label interactionLabel = new Label("Interaction Key:", skin);
    keybinds.put(INTERACTION_KEY, settings.getInteractionButton());
    TextField interactionKeyText =
        new TextField(Input.Keys.toString(settings.getInteractionButton()), skin);
    interactionKeyText.setName(INTERACTION_KEY);
    whiten(interactionLabel);
    setupKeybindTextField(interactionKeyText);

    Label upLabel = new Label("Up Key:", skin);
    keybinds.put(UP_KEY, settings.getUpButton());
    TextField upKeyText = new TextField(Input.Keys.toString(settings.getUpButton()), skin);
    upKeyText.setName(UP_KEY);
    whiten(upLabel);
    setupKeybindTextField(upKeyText);

    Label downLabel = new Label("Down Key:", skin);
    keybinds.put(DOWN_KEY, settings.getDownButton());
    TextField downKeyText = new TextField(Input.Keys.toString(settings.getDownButton()), skin);
    downKeyText.setName(DOWN_KEY);
    whiten(downLabel);
    setupKeybindTextField(downKeyText);

    Label leftLabel = new Label("Left Key:", skin);
    keybinds.put(LEFT_KEY, settings.getLeftButton());
    TextField leftKeyText = new TextField(Input.Keys.toString(settings.getLeftButton()), skin);
    leftKeyText.setName(LEFT_KEY);
    whiten(leftLabel);
    setupKeybindTextField(leftKeyText);

    Label rightLabel = new Label("Right Key:", skin);
    keybinds.put(RIGHT_KEY, settings.getRightButton());
    TextField rightKeyText = new TextField(Input.Keys.toString(settings.getRightButton()), skin);
    rightKeyText.setName(RIGHT_KEY);
    whiten(rightLabel);
    setupKeybindTextField(rightKeyText);

    Label difficultyLabel = new Label("Difficulty:", skin);
    difficultySelect = new SelectBox<>(skin);
    difficultySelect.setItems("EASY", "NORMAL", "HARD");
    difficultySelect.setSelected(settings.getDifficulty().toString());
    whiten(difficultyLabel);

    TextButton applyBtn = ButtonFactory.createButton("Apply");
    applyBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Apply button clicked");
            applyChanges();
            entity.getEvents().trigger("backtosettingsmenu");
          }
        });

    rootTable.add(pauseLabel).left().padRight(20f);
    rootTable.add(pauseKeyText).width(150f).center();
    rootTable.row().padTop(10f);

    rootTable.add(skipLabel).left().padRight(25f);
    rootTable.add(skipKeyText).width(150f).center();
    rootTable.row().padTop(10f);

    rootTable.add(interactionLabel).left().padRight(25f);
    rootTable.add(interactionKeyText).width(150f).center();
    rootTable.row().padTop(10f);

    rootTable.add(upLabel).left().padRight(25f);
    rootTable.add(upKeyText).width(150f).center();
    rootTable.row().padTop(10f);

    rootTable.add(downLabel).left().padRight(25f);
    rootTable.add(downKeyText).width(150f).center();
    rootTable.row().padTop(10f);

    rootTable.add(leftLabel).left().padRight(25f);
    rootTable.add(leftKeyText).width(150f).center();
    rootTable.row().padTop(10f);

    rootTable.add(rightLabel).left().padRight(25f);
    rootTable.add(rightKeyText).width(150f).center();
    rootTable.row().padTop(10f);

    rootTable.add(difficultyLabel).left().padRight(25f);
    rootTable.add(difficultySelect).width(150f).center();
    rootTable.row().padTop(20f);
    stage.addActor(rootTable);

    bottomRow = new Table();
    bottomRow.setFillParent(true);
    bottomRow.bottom().padBottom(20f);
    bottomRow.add(applyBtn).size(150f, 50f).center();
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

  /**
   * Whiten the label.
   *
   * @param label The label to whiten.
   */
  private static void whiten(Label label) {
    Label.LabelStyle st = new Label.LabelStyle(label.getStyle());
    st.fontColor = Color.WHITE;
    label.setStyle(st);
    logger.debug("Labels are white");
  }
}
