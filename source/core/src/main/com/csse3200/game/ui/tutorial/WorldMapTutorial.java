package com.csse3200.game.ui.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.SettingsService;
import com.csse3200.game.ui.UIComponent;

/**
 * Displays an interactive tutorial overlay for the World Map screen.
 *
 * <p>The world map tutorial guides the player through basic controls such as movement, interaction
 * and zooming. It appears as a dialog box in the top-left corner of the screen and progresses based
 * on user inputs. A toggle button allows the player to show or hide the tutorial, and optionally
 * displays all steps at once. The tutorial fades out automatically after completion unless manually
 * toggled on again.
 *
 * <p>Tutorial steps: 1. Move using W/A/S/D 2. Interact using E 3. Zoom using Q/K
 */
public class WorldMapTutorial extends UIComponent {
  /** The main container for tutorial labels */
  private Table table;

  /** The label that is currently displayed during single step display */
  private Label currentLabel;

  /** Label for movement instructions */
  private Label moveLabel;

  /** Label for interaction instructions */
  private Label interactLabel;

  /** Label for zoom instructions */
  private Label zoomLabel;

  /** Boolean to determine whether to display all tutorial labels at once */
  private boolean displayAllLabels = false;

  /** Current alpha transparency for fading effect */
  private float alpha = 1f;

  /** Boolean to determine whether the tutorial is currently fading out */
  private boolean fadingOut = false;

  /** Whether the tutorial is active and listening for input */
  private boolean active = true;

  /** Current tutorial step index */
  private int step = 0;

  /** Fade out speed for the tutorial. */
  private static final float FADE_SPEED = 0.6f;

  /** Table padding value. */
  private static final float TABLE_PAD = 20f;

  /** Label bottom padding. */
  private static final float LABEL_BOTTOM_PAD = 20f;

  /** Table position offset from top. */
  private static final float TABLE_TOP_OFFSET = 100f;

  /**
   * Initialises the tutorial UI, including the dialog box and toggle button. Sets up the initial
   * label and input listener for toggling visibility.
   */
  @Override
  public void create() {
    super.create();

    Texture overlayTexture = new Texture(Gdx.files.internal("images/ui/dialog.png"));
    TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(overlayTexture);

    // Create a table with the image as background
    table = new Table();
    table.setBackground(backgroundDrawable);
    table.setSize(
        Math.floorDiv(Gdx.graphics.getWidth(), 5), Math.floorDiv(Gdx.graphics.getHeight(), 5));
    table.setPosition(
        TABLE_PAD,
        Gdx.graphics.getHeight() - table.getHeight() - TABLE_TOP_OFFSET); // top-left with padding
    table.pad(TABLE_PAD); // inner padding for the label

    SettingsService settingsService = ServiceLocator.getSettingsService();
    String upKeyName = Input.Keys.toString(settingsService.getSettings().getUpButton());
    String downKeyName = Input.Keys.toString(settingsService.getSettings().getDownButton());
    String leftKeyName = Input.Keys.toString(settingsService.getSettings().getLeftButton());
    String rightKeyName = Input.Keys.toString(settingsService.getSettings().getRightButton());
    String interactKeyName =
        Input.Keys.toString(settingsService.getSettings().getInteractionButton());
    // For zoom, re-use current Q/K bindings for now (no settings provided for zoom)
    String zoomOutKeyName = Input.Keys.toString(Input.Keys.Q);
    String zoomInKeyName = Input.Keys.toString(Input.Keys.K);

    this.moveLabel =
        ui.text(
            "Use "
                + upKeyName
                + "/"
                + leftKeyName
                + "/"
                + downKeyName
                + "/"
                + rightKeyName
                + " to move");
    this.interactLabel = ui.text("Press " + interactKeyName + " to interact");
    this.zoomLabel = ui.text("Press " + zoomOutKeyName + "/" + zoomInKeyName + " to zoom");
    currentLabel = moveLabel;

    table.add(currentLabel).left();
    moveLabel.setVisible(true); // set first label to visible and the rest invisible

    stage.addActor(table);

    // Toggle button
    TextButton toggleButton = ui.primaryButton("TUTORIAL", 150);
    toggleButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            boolean visible = table.isVisible();

            if (!visible) {
              displayAllLabels = true;
              fadingOut = false;
              alpha = 1f;
              table.getColor().a = 1f;
              resetTutorial();
              table.setVisible(true);
            } else {
              table.setVisible(false);
            }
          }
        });

    Table buttonTable = new Table();
    buttonTable.top().left().pad(TABLE_PAD);
    buttonTable.setFillParent(true);
    buttonTable.add(toggleButton).left();
    stage.addActor(buttonTable);
  }

  /**
   * Resets the tutorial state and label visibility. If {@code displayAllLabels} is true, all steps
   * are shown simultaneously. Otherwise, only display the current step.
   */
  private void resetTutorial() {
    step = 0;
    active = true;
    alpha = 1f;
    fadingOut = false;

    // Reset label visibilities
    moveLabel.setVisible(true);
    interactLabel.setVisible(true);
    zoomLabel.setVisible(true);

    moveLabel.getStyle().fontColor.a = 1f;
    interactLabel.getStyle().fontColor.a = 1f;
    zoomLabel.getStyle().fontColor.a = 1f;

    table.clearChildren();

    if (displayAllLabels) {
      moveLabel.setVisible(true);
      interactLabel.setVisible(true);
      zoomLabel.setVisible(true);
      table.add(moveLabel).left().padBottom(LABEL_BOTTOM_PAD).row();
      table.add(interactLabel).left().padBottom(LABEL_BOTTOM_PAD).row();
      table.add(zoomLabel).left();
    } else {
      moveLabel.setVisible(true);
      interactLabel.setVisible(false);
      zoomLabel.setVisible(false);
      currentLabel = moveLabel;
      table.add(currentLabel).left();
    }
  }

  /**
   * Updates the tutorial logic based on player input. Advances through tutorial steps and triggers
   * fade-out of tutorial when complete. Skips updates if inactive or displaying all labels.
   */
  @Override
  public void update() {
    if (currentLabel != null) {
      currentLabel.getStyle().fontColor.a = 1f;
    }

    if (!active || displayAllLabels) {
      return;
    }

    updateTutorialStep();
    updateFadeOut();
  }

  /** Updates the current tutorial step based on user input. */
  private void updateTutorialStep() {
    switch (step) {
      case 0 -> handleMovementStep();
      case 1 -> handleInteractionStep();
      case 2 -> handleZoomStep();
      default -> {
        // No action needed for unknown steps
      }
    }
  }

  /** Handles the movement tutorial step (step 0). */
  private void handleMovementStep() {
    if (isMovementKeyPressed()) {
      advanceToStep(interactLabel, 1);
    }
  }

  /** Handles the interaction tutorial step (step 1). */
  private void handleInteractionStep() {
    int interactKey = ServiceLocator.getSettingsService().getSettings().getInteractionButton();
    if (Gdx.input.isKeyJustPressed(interactKey)) {
      advanceToStep(zoomLabel, 2);
    }
  }

  /** Handles the zoom tutorial step (step 2). */
  private void handleZoomStep() {
    if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || Gdx.input.isKeyJustPressed(Input.Keys.K)) {
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
   * @param newLabel the label to display for the next step
   * @param nextStep the step number to advance to
   */
  private void advanceToStep(Label newLabel, int nextStep) {
    currentLabel = newLabel;
    currentLabel.getStyle().fontColor.a = 1f;
    currentLabel.setVisible(true);
    table.clearChildren();
    table.add(currentLabel).left();
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
      table.setVisible(false);
      table.clearChildren();
    } else {
      table.getColor().a = alpha;
    }
  }

  /** Cleans up the tutorial UI and resources. Clears the table and call superclass disposal. */
  @Override
  public void dispose() {
    table.clear();
    super.dispose();
  }

  /**
   * Draw method overridden from {@link UIComponent}. No manual drawing is required as Scene2D
   * handles rendering.
   *
   * @param batch Batch that the SpriteBatch used for rendering.
   */
  @Override
  protected void draw(SpriteBatch batch) {
    // No manual drawing required
  }
}
