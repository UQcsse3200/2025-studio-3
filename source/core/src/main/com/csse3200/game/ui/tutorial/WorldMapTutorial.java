package com.csse3200.game.ui.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.ui.TypographyFactory;
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
        20f, Gdx.graphics.getHeight() - table.getHeight() - 100f); // top-left with padding
    table.pad(20f); // inner padding for the label

    this.moveLabel = TypographyFactory.createSubtitle("Use W/A/S/D to move", Color.WHITE);
    this.interactLabel = TypographyFactory.createSubtitle("Press E to interact", Color.WHITE);
    this.zoomLabel = TypographyFactory.createSubtitle("Press Q/K to zoom", Color.WHITE);
    currentLabel = moveLabel;

    table.add(currentLabel).left();
    moveLabel.setVisible(true); // set first label to visible and the rest invisible

    stage.addActor(table);

    // Toggle button
    TextButton toggleButton = new TextButton("Tutorial", skin);
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
    buttonTable.top().left().pad(20f);
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
      table.add(moveLabel).left().padBottom(20f).row();
      table.add(interactLabel).left().padBottom(20f).row();
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

    if (!active) return;

    if (displayAllLabels) return;

    switch (step) {
      case 0 -> {
        if (Gdx.input.isKeyPressed(Input.Keys.W)
            || Gdx.input.isKeyPressed(Input.Keys.A)
            || Gdx.input.isKeyPressed(Input.Keys.S)
            || Gdx.input.isKeyPressed(Input.Keys.D)) {
          currentLabel = interactLabel;
          currentLabel.getStyle().fontColor.a = 1f;
          currentLabel.setVisible(true);
          table.clearChildren();
          table.add(interactLabel).left();
          step++;
        }
      }
      case 1 -> {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
          currentLabel = zoomLabel;
          currentLabel.getStyle().fontColor.a = 1f;
          currentLabel.setVisible(true);
          table.clearChildren();
          table.add(currentLabel).left();
          step++;
        }
      }
      case 2 -> {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q) || Gdx.input.isKeyJustPressed(Input.Keys.K)) {
          fadingOut = true;
        }
      }
    }

    if (fadingOut) {
      alpha -= Gdx.graphics.getDeltaTime() * 0.6f;
      if (alpha <= 0) {
        alpha = 0;
        active = false;
        table.setVisible(false);
        table.clearChildren();
      } else {
        table.getColor().a = alpha;
      }
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
