package com.csse3200.game.ui.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.SettingsService;
import com.csse3200.game.ui.UIComponent;
import net.dermetfan.utils.Pair;

/** Displays a step-by-step tutorial overlay for the World Map screen. */
public class WorldMapTutorial extends UIComponent {
  /** Full-screen dark overlay to dim the background. */
  private Image overlay;

  /** Dialog window containing the tutorial messages. */
  private Window dialogWindow;

  /** Label displaying the current tutorial message. */
  private Label messageLabel;

  /** Current tutorial step index. */
  private int step = 0;

  /** Whether the tutorial is active and listening for input. */
  private boolean active = true;

  /** Messages displayed in order. Populated using current key bindings. */
  private String[] tutorialMessages;

  /** Alpha for overlay color. */
  private static final float OVERLAY_ALPHA = 0.7f;

  /** Common literal used in UI copy. */
  private static final String PRESS = "Press ";

  /** Whether the tutorial is fading out. */
  private boolean fadingOut = false;

  /** Current alpha value for fade out effect. */
  private float alpha = 1.0f;

  /** Speed of the fade out effect. */
  private static final float FADE_SPEED = 2.0f;

  /** Main table container for the tutorial UI. */
  private Table table;

  /** Stack containing all tutorial UI elements. */
  private Stack stack;

  /**
   * Initialises the tutorial UI components, matching the style used by {@link LevelMapTutorial}.
   */
  @Override
  public void create() {
    super.create();

    // Build messages with current key bindings
    Settings settings = ServiceLocator.getSettingsService().getSettings();
    settings.checkButtonSettings();
    String upKeyName = Input.Keys.toString(settings.getUpButton());
    String downKeyName = Input.Keys.toString(settings.getDownButton());
    String leftKeyName = Input.Keys.toString(settings.getLeftButton());
    String rightKeyName = Input.Keys.toString(settings.getRightButton());
    String interactKeyName = Input.Keys.toString(settings.getInteractionButton());
    String zoomOutKeyName = Input.Keys.toString(settings.getZoomOutButton());
    String zoomInKeyName = Input.Keys.toString(settings.getZoomInButton());

    tutorialMessages =
        new String[] {
          ("Use "
                  + upKeyName
                  + "/"
                  + leftKeyName
                  + "/"
                  + downKeyName
                  + "/"
                  + rightKeyName
                  + " to move")
              .toUpperCase(),
          (PRESS + interactKeyName + " to interact").toUpperCase(),
          (PRESS + zoomOutKeyName + "/" + zoomInKeyName + " to zoom").toUpperCase()
        };

    // Root stack with overlay behind UI
    stack = new Stack();
    stack.setFillParent(true);

    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    pixmap.setColor(0, 0, 0, 1);
    pixmap.fill();
    Texture blackTex = new Texture(pixmap);
    pixmap.dispose();
    overlay = new Image(new TextureRegionDrawable(blackTex));
    overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    overlay.setColor(0, 0, 0, OVERLAY_ALPHA);
    stack.add(overlay);

    dialogWindow = ui.createWindow("TUTORIAL");
    dialogWindow.setModal(true);
    dialogWindow.setMovable(false);
    dialogWindow.setResizable(false);
    dialogWindow.getTitleLabel().setAlignment(Align.center);
    dialogWindow.setSize(800f, 400f);

    messageLabel = ui.subheading(tutorialMessages[step]);
    messageLabel.setWrap(true);
    messageLabel.setAlignment(Align.center);

    TextButton skipButton = ui.primaryButton("Skip", 150f);
    TextButton continueButton = ui.primaryButton("Continue", 150f);
    Pair<Float, Float> buttonDimensions = ui.getScaledDimensions(150f);

    skipButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            endTutorial();
          }
        });

    continueButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            nextStep();
          }
        });

    Table content = new Table();
    content.add(messageLabel).expand().fillX().pad(20f).row();

    Table buttonRow = new Table();
    buttonRow
        .add(skipButton)
        .width(buttonDimensions.getKey())
        .height(buttonDimensions.getValue())
        .padRight(20f);
    buttonRow
        .add(continueButton)
        .width(buttonDimensions.getKey())
        .height(buttonDimensions.getValue());

    content.add(buttonRow).expandY().bottom().padBottom(20f);
    dialogWindow.add(content).expand().fill();

    table = new Table();
    table.setFillParent(false);
    table.align(Align.bottom);
    table.add(dialogWindow).width(600f).height(200f).padBottom(80f);
    stack.add(table);
    stage.addActor(stack);
  }

  /** Advances to the next tutorial message, or ends when finished. */
  private void nextStep() {
    step++;
    if (step < tutorialMessages.length) {
      messageLabel.setText(tutorialMessages[step]);
    } else {
      endTutorial();
    }
  }

  /** Ends the tutorial and hides UI elements. */
  private void endTutorial() {
    active = false;
    overlay.setVisible(false);
    dialogWindow.setVisible(false);
    // Mark the tutorial as played when it actually ends
    ServiceLocator.getProfileService().getProfile().setPlayedMapTutorial();
  }

  @Override
  public void update() {
    if (!active) return;

    updateFadeOut();

    // Allow skip button to advance or end tutorial
    int skipKey = ServiceLocator.getSettingsService().getSettings().getSkipButton();
    if (Gdx.input.isKeyJustPressed(skipKey)) {
      if (step < tutorialMessages.length - 1) {
        nextStep();
      } else {
        endTutorial();
      }
    }

    // Handle interactive tutorial progression based on current step
    switch (step) {
      case 0:
        handleMovementStep();
        break;
      case 1:
        handleInteractionStep();
        break;
      case 2:
        handleZoomStep();
        break;
      default:
        break;
    }
  }

  /** Handles the movement tutorial step (step 0). */
  private void handleMovementStep() {
    if (isMovementKeyPressed()) {
      advanceToStep(1);
    }
  }

  /** Handles the interaction tutorial step (step 1). */
  private void handleInteractionStep() {
    int interactKey = ServiceLocator.getSettingsService().getSettings().getInteractionButton();
    if (Gdx.input.isKeyJustPressed(interactKey)) {
      advanceToStep(2);
    }
  }

  /** Handles the zoom tutorial step (step 2). */
  private void handleZoomStep() {
    if (Gdx.input.isKeyJustPressed(
            ServiceLocator.getSettingsService().getSettings().getZoomInButton())
        || Gdx.input.isKeyJustPressed(
            ServiceLocator.getSettingsService().getSettings().getZoomOutButton())) {
      fadingOut = true;
    }
  }

  /**
   * Checks if any movement key is currently pressed.
   *
   * @return true if a movement key is pressed, false otherwise
   */
  private boolean isMovementKeyPressed() {
    SettingsService settingsService = ServiceLocator.getSettingsService();
    int up = settingsService.getSettings().getUpButton();
    int down = settingsService.getSettings().getDownButton();
    int left = settingsService.getSettings().getLeftButton();
    int right = settingsService.getSettings().getRightButton();
    return Gdx.input.isKeyPressed(up)
        || Gdx.input.isKeyPressed(left)
        || Gdx.input.isKeyPressed(down)
        || Gdx.input.isKeyPressed(right);
  }

  /**
   * Advances to the next tutorial step with the given label.
   *
   * @param nextStep the step number to advance to
   */
  private void advanceToStep(int nextStep) {
    messageLabel.setText(tutorialMessages[nextStep]);
    messageLabel.setAlignment(Align.center);
    messageLabel.setWrap(true);
    dialogWindow.clearChildren();
    dialogWindow.add(messageLabel).expand().fill();
    step = nextStep;
  }

  /** Updates the fade-out effect if active. */
  private void updateFadeOut() {
    if (!fadingOut) {
      return;
    }

    alpha -= Gdx.graphics.getDeltaTime() * FADE_SPEED;
    if (alpha <= 0) {
      alpha = 0;
      active = false;
      stack.setVisible(false);
      table.clearChildren();
      // Mark the tutorial as complete when fade-out finishes
      ServiceLocator.getProfileService().getProfile().setPlayedMapTutorial();
    } else {
      // Fade out both the overlay and the dialog elements
      overlay.getColor().a = OVERLAY_ALPHA * alpha;
      table.getColor().a = alpha;
      dialogWindow.getColor().a = alpha;
    }
  }

  /** Cleans up the tutorial UI and resources. Clears the table and call superclass disposal. */
  @Override
  public void dispose() {
    super.dispose();
    stage.clear();
  }
}
