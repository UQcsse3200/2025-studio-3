package com.csse3200.game.components.settingsmenu;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.services.ResourceService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import java.util.ArrayList;
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
  private Map<String, Image> keyImages = new HashMap<>();
  private static final String PAUSE_KEY = "pause";
  private static final String SKIP_KEY = "skip";
  private static final String INTERACTION_KEY = "interaction";
  private static final String UP_KEY = "up";
  private static final String DOWN_KEY = "down";
  private static final String LEFT_KEY = "left";
  private static final String RIGHT_KEY = "right";
  private static final String ZOOM_IN_KEY = "zoomin";
  private static final String ZOOM_OUT_KEY = "zoomout";
  private static final ArrayList<Integer> ALLOWED_KEYS = new ArrayList<>();

  // Set Allowed Keys
  static {
    // Letters (A-Z)
    for (int l = Input.Keys.A; l <= Input.Keys.Z; l++) {
      ALLOWED_KEYS.add(l);
    }

    // Numbers 1-9 (0 sometimes causes issues as a keycode of 0 can represent Unknown)
    for (int n = Input.Keys.NUM_1; n <= Input.Keys.NUM_9; n++) {
      ALLOWED_KEYS.add(n);
    }

    // Special keys
    ALLOWED_KEYS.add(Input.Keys.SPACE);
    ALLOWED_KEYS.add(Input.Keys.ESCAPE);
    ALLOWED_KEYS.add(Input.Keys.UP);
    ALLOWED_KEYS.add(Input.Keys.DOWN);
    ALLOWED_KEYS.add(Input.Keys.LEFT);
    ALLOWED_KEYS.add(Input.Keys.RIGHT);
    ALLOWED_KEYS.add(Input.Keys.TAB);
  }

  /** Constructor for GameSettingsMenu. */
  public GameSettingsMenu() {
    super();
  }

  @Override
  public void create() {
    super.create();
    // Load key images
    ResourceService resourceService = ServiceLocator.getResourceService();
    for (int keycode : ALLOWED_KEYS) {
      String imagePath = "images/keys/" + Input.Keys.toString(keycode) + ".png";
      if (!resourceService.containsAsset(imagePath, Texture.class)) {
        resourceService.loadTextures(new String[] {imagePath});
        logger.info("Loaded key image: {}", imagePath);
      }
    }
    String blankKeyImagePath = "images/keys/Blank.png";
    if (!resourceService.containsAsset(blankKeyImagePath, Texture.class)) {
      resourceService.loadTextures(new String[] {blankKeyImagePath});
      logger.info("Loaded key image: {}", blankKeyImagePath);
    }
    resourceService.loadAll();

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

    // Check there were no issues with saved button settings
    checkButtonSettings();

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

    Label zoomInLabel = ui.subheading("Zoom In Key:");
    keybinds.put(ZOOM_IN_KEY, settings.getZoomInButton());
    TextField zoomInKeyText = ui.createTextField(Input.Keys.toString(settings.getZoomInButton()));
    zoomInKeyText.setName(ZOOM_IN_KEY);
    setupKeybindTextField(zoomInKeyText);

    Label zoomOutLabel = ui.subheading("Zoom Out Key:");
    keybinds.put(ZOOM_OUT_KEY, settings.getZoomOutButton());
    TextField zoomOutKeyText = ui.createTextField(Input.Keys.toString(settings.getZoomOutButton()));
    zoomOutKeyText.setName(ZOOM_OUT_KEY);
    setupKeybindTextField(zoomOutKeyText);

    Label difficultyLabel = ui.subheading("Difficulty:");
    difficultySelect = ui.createSelectBox(new String[] {"EASY", "NORMAL", "HARD"});
    difficultySelect.setSelected(settings.getDifficulty().toString());

    // Create apply button using UIFactory
    int buttonWidth = 150;
    TextButton applyBtn = ui.primaryButton("Apply", buttonWidth);
    Pair<Float, Float> buttonDimensions = ui.getScaledDimensions(buttonWidth);
    applyBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Apply button clicked");
            applyChanges();
            entity.getEvents().trigger("backtosettingsmenu");
          }
        });

    // Create reset button using UIFactory
    TextButton resetKeysBtn = ui.primaryButton("Reset Keys", buttonWidth);
    resetKeysBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Reset button clicked");
            resetKeyBinds();
            // Update keybind text fields
            pauseKeyText.setText(Input.Keys.toString(settings.getPauseButton()));
            skipKeyText.setText(Input.Keys.toString(settings.getSkipButton()));
            interactionKeyText.setText(Input.Keys.toString(settings.getInteractionButton()));
            upKeyText.setText(Input.Keys.toString(settings.getUpButton()));
            downKeyText.setText(Input.Keys.toString(settings.getDownButton()));
            leftKeyText.setText(Input.Keys.toString(settings.getLeftButton()));
            rightKeyText.setText(Input.Keys.toString(settings.getRightButton()));
            zoomInKeyText.setText(Input.Keys.toString(settings.getZoomInButton()));
            zoomOutKeyText.setText(Input.Keys.toString(settings.getZoomOutButton()));
            // Update keybind map
            keybinds.put(PAUSE_KEY, settings.getPauseButton());
            keybinds.put(SKIP_KEY, settings.getSkipButton());
            keybinds.put(INTERACTION_KEY, settings.getInteractionButton());
            keybinds.put(UP_KEY, settings.getUpButton());
            keybinds.put(DOWN_KEY, settings.getDownButton());
            keybinds.put(LEFT_KEY, settings.getLeftButton());
            keybinds.put(RIGHT_KEY, settings.getRightButton());
            keybinds.put(ZOOM_IN_KEY, settings.getZoomInButton());
            keybinds.put(ZOOM_OUT_KEY, settings.getZoomOutButton());
            // Update key images following reset
            setKeyImage(keyImages.get(PAUSE_KEY), keybinds.get(PAUSE_KEY));
            setKeyImage(keyImages.get(SKIP_KEY), keybinds.get(SKIP_KEY));
            setKeyImage(keyImages.get(INTERACTION_KEY), keybinds.get(INTERACTION_KEY));
            setKeyImage(keyImages.get(UP_KEY), keybinds.get(UP_KEY));
            setKeyImage(keyImages.get(DOWN_KEY), keybinds.get(DOWN_KEY));
            setKeyImage(keyImages.get(LEFT_KEY), keybinds.get(LEFT_KEY));
            setKeyImage(keyImages.get(RIGHT_KEY), keybinds.get(RIGHT_KEY));
            setKeyImage(keyImages.get(ZOOM_IN_KEY), keybinds.get(ZOOM_IN_KEY));
            setKeyImage(keyImages.get(ZOOM_OUT_KEY), keybinds.get(ZOOM_OUT_KEY));
          }
        });

    // Layout with proper UI scaling (including creating Stacks for key textfield and image pairs)
    rootTable.add(pauseLabel).left().padRight(20f * uiScale);
    // Use set width/height for key fields
    float width = 150f * uiScale;
    float height = 40f * uiScale;
    rootTable
        .add(makeKeyImageStack(pauseKeyText, settings.getPauseButton()))
        .size(width, height)
        .center();
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(skipLabel).left().padRight(25f * uiScale);
    rootTable
        .add(makeKeyImageStack(skipKeyText, settings.getSkipButton()))
        .size(width, height)
        .center();
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(interactionLabel).left().padRight(25f * uiScale);
    rootTable
        .add(makeKeyImageStack(interactionKeyText, settings.getInteractionButton()))
        .size(width, height)
        .center();
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(upLabel).left().padRight(25f * uiScale);
    rootTable
        .add(makeKeyImageStack(upKeyText, settings.getUpButton()))
        .size(width, height)
        .center();
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(downLabel).left().padRight(25f * uiScale);
    rootTable
        .add(makeKeyImageStack(downKeyText, settings.getDownButton()))
        .size(width, height)
        .center();
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(leftLabel).left().padRight(25f * uiScale);
    rootTable
        .add(makeKeyImageStack(leftKeyText, settings.getLeftButton()))
        .size(width, height)
        .center();
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(rightLabel).left().padRight(25f * uiScale);
    rootTable
        .add(makeKeyImageStack(rightKeyText, settings.getRightButton()))
        .size(width, height)
        .center();
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(zoomInLabel).left().padRight(25f * uiScale);
    rootTable
        .add(makeKeyImageStack(zoomInKeyText, settings.getZoomInButton()))
        .size(width, height)
        .center();
    rootTable.row().padTop(10f * uiScale);

    rootTable.add(zoomOutLabel).left().padRight(25f * uiScale);
    rootTable
        .add(makeKeyImageStack(zoomOutKeyText, settings.getZoomOutButton()))
        .size(width, height)
        .center();
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
        .center()
        .padRight(20f * uiScale);
    bottomRow
        .add(resetKeysBtn)
        .width(buttonDimensions.getKey())
        .height(buttonDimensions.getValue());
    stage.addActor(bottomRow);
  }

  /**
   * Validates the button settings to ensure they use allowed key bindings and none are Unknown. If
   * any button is found to have a key that is not allowed, key bindings are set to their default
   * values.
   */
  private void checkButtonSettings() {
    // Get all currently saved button keys
    Settings settings = ServiceLocator.getSettingsService().getSettings();
    ArrayList<Integer> buttons = new ArrayList<>();
    buttons.add(settings.getPauseButton());
    buttons.add(settings.getSkipButton());
    buttons.add(settings.getInteractionButton());
    buttons.add(settings.getUpButton());
    buttons.add(settings.getDownButton());
    buttons.add(settings.getLeftButton());
    buttons.add(settings.getRightButton());
    buttons.add(settings.getZoomInButton());
    buttons.add(settings.getZoomOutButton());
    logger.info("Current button keys: {}", buttons);

    // Check each button key is valid
    for (int button : buttons) {
      if (!ALLOWED_KEYS.contains(button)) { // also ensures none are Unknown (0)
        // If any is invalid, reset all to default
        resetKeyBinds();
        logger.info("Invalid button key found, resetting to defaults.");
        return;
      }
    }
    logger.info("All button keys valid.");
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
              // Set key image to blank key
              setKeyImage(keyImages.get(textField.getName()), -1);

              // If user clicked away without setting a new key, restore previous text/key image
            } else if (textField.getText() == null || textField.getText().isEmpty()) {
              textField.setText(Input.Keys.toString(keybinds.get(textField.getName())));
              setKeyImage(keyImages.get(textField.getName()), keybinds.get(textField.getName()));
            }
          }
        });

    // Add key input listener to capture single key presses
    textField.addListener(
        new InputListener() {
          @Override
          public boolean keyDown(InputEvent event, int keycode) {
            if (textField.hasKeyboardFocus()) {
              // Reject invalid key codes
              if (!ALLOWED_KEYS.contains(keycode)) {
                ServiceLocator.getDialogService()
                    .error("Invalid Key", "That key cannot be used for keybinds.");
                // Restore previous text/key image
                textField.setText(Input.Keys.toString(keybinds.get(textField.getName())));
                setKeyImage(keyImages.get(textField.getName()), keybinds.get(textField.getName()));
                textField.setFocusTraversal(false);
                stage.setKeyboardFocus(null);
                return false;
              }

              // Update the text field with the new key (do not error if same key as previous is
              // re-entered)
              if (keybinds.containsValue(keycode)
                  && (keybinds.get(textField.getName()) != keycode)) {
                logger.info("Keybind conflict: {}", keycode);
                ServiceLocator.getDialogService()
                    .error("Keybind conflict", "This key is already in use by another action.");
                // Set key text/image back
                textField.setText(Input.Keys.toString(keybinds.get(textField.getName())));
                setKeyImage(keyImages.get(textField.getName()), keybinds.get(textField.getName()));
                textField.setFocusTraversal(false);
                stage.setKeyboardFocus(null);
                return false;
              }
              logger.info("Keybind not conflict: {}", keycode);
              keybinds.put(textField.getName(), keycode);
              // Set new key text/image
              textField.setText(Input.Keys.toString(keycode));
              setKeyImage(keyImages.get(textField.getName()), keybinds.get(textField.getName()));
              textField.setFocusTraversal(false);
              stage.setKeyboardFocus(null);
              return true;
            }
            return false;
          }
        });
  }

  /**
   * Creates a Stack containing a key image and text field for the keybind input. The stack
   * positions the image behind the text field (which is transparent), so the text field handles
   * clicks and focus, but the image is shown to the user.
   *
   * @param textField the TextField set up for the keybind input.
   * @param keycode the keycode used to generate the relevant key image to start.
   * @return a Stack containing the key image and the text field (configured and sized).
   */
  private Stack makeKeyImageStack(TextField textField, int keycode) {
    Image keyImage = new Image();
    // Save the key image instance that will be part of the Stack (in order to access/change it
    // later)
    keyImages.put(textField.getName(), keyImage);

    setKeyImage(keyImage, keycode);
    keyImage.setScaling(Scaling.fillY);

    textField.setColor(1f, 1f, 1f, 0f); // fully transparent
    textField.setSize(150f * ui.getUIScale(), 40f * ui.getUIScale());

    // Create key stack
    Stack stack = new Stack();
    stack.setSize(150f * ui.getUIScale(), 40f * ui.getUIScale());
    stack.add(keyImage);
    stack.add(textField); // sits on top to receive clicks/focus
    return stack;
  }

  /**
   * Updates the displayed key image associated with a specific key binding. The method sets the
   * appropriate image based on the provided keycode. If the keycode is -1, a blank image is used as
   * a placeholder.
   *
   * @param keyImage the Image object representing the key binding display (part of the Stack set up
   *     upon creation).
   * @param keycode the keycode representing the specific key (used to determine the correct key
   *     image).
   */
  private void setKeyImage(Image keyImage, int keycode) {
    Texture texture;
    ResourceService resourceService = ServiceLocator.getResourceService();

    if (keycode == -1) {
      texture = resourceService.getAsset("images/keys/Blank.png", Texture.class);
    } else {
      texture =
          resourceService.getAsset(
              "images/keys/" + Input.Keys.toString(keycode) + ".png", Texture.class);
    }

    // Pick nearest texel when scaling, don't blur (keeps pixelated look)
    texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

    // Wrap in Texture Region to define drawable area, then in Drawable type that Image can render,
    // and assign to Image
    keyImage.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));

    // Scale to height, but maintain aspect ratio, and center
    keyImage.setScaling(Scaling.fillY);
    keyImage.setAlign(Align.center);
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
            keybinds.get(RIGHT_KEY),
            keybinds.get(ZOOM_IN_KEY),
            keybinds.get(ZOOM_OUT_KEY));
    logger.info("[GameSettingsMenu] New Keybinds: {}", keybinds);
    ServiceLocator.getSettingsService().saveSettings();
    logger.info("[GameSettingsMenu] Game settings applied");

    // Make last TextField not show as still selected after applying changes (if/when re-entering
    // Game Settings Menu)
    stage.setKeyboardFocus(null);
  }

  /** Reset keybinds to default keys. */
  private void resetKeyBinds() {
    ServiceLocator.getSettingsService().resetKeyBinds();
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
