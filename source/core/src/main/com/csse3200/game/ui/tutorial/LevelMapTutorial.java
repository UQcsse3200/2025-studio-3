package com.csse3200.game.ui.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

/**
 * Displays a step-by-step tutorial overlay for the Level Map screen.
 *
 * <p>This tutorial introduces players to core gameplay mechanics such as placing defence units,
 * resource generation and combat behaviour. It appears as a dialog box with a darkened background
 * overlay and progresses through predefined messages when the player presses the space bar or
 * clicks on the 'next' button which is displayed as a white arrow on the bottom-right of the dialog
 * box.
 *
 * <p>The tutorial ends either after the final message or when the user clicks the "Skip Tutorial"
 * button displayed on the bottom-right of the screen.
 */
public class LevelMapTutorial extends UIComponent {
  /** Table containing the instructional dialog and next button. */
  private Table dialogTable;

  /** Table containing the hint message to progress through the tutorial messages. */
  private Table messageTable;

  /** Table containing the "Skip Tutorial" button. */
  private Table skipTable;

  /** Full-screen dark overlay to dim the background. */
  private Image overlay;

  /** Label displaying the current tutorial message. */
  private Label messageLabel;

  /** Current tutorial step index. */
  private int step = 0;

  /** Bottom padding for hint message. */
  private static final float HINT_BOTTOM_PAD = 20f;

  /** Boolean to determine whether the tutorial is active and listening for inputs. */
  private boolean active = true;

  /** Array of tutorial messages shown in sequence. */
  private final String[] tutorialMessages = {
    "Welcome to Level 1!",
    "Drag defence units from the hot-bar onto the grid.",
    "Furnaces produce scrap metal which can be used to recruit more human defenders.",
    "Human defenders attack incoming robot enemies."
  };

  /** Alpha transparency value for the overlay. */
  private static final float OVERLAY_ALPHA = 0.7f;

  /** Dialog width padding. */
  private static final float DIALOG_WIDTH_PAD = 100f;

  /** Traverse name constant. */
  private static final String TRAVERSE = "traverse";

  /**
   * Initialises the tutorial UI components, including the overlay, dialog box, message label, next
   * button and skip tutorial button. Pauses the game while active.
   */
  @Override
  public void create() {
    super.create();
    traverseTutorial();

    Stage screenSize = ServiceLocator.getRenderService().getStage();

    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888); // create dark overlay
    pixmap.setColor(0, 0, 0, 1); // Solid black
    pixmap.fill();
    Texture blackTex = new Texture(pixmap);
    pixmap.dispose(); // clean up after creating texture

    overlay = new Image(new TextureRegionDrawable(blackTex));
    overlay.setSize(screenSize.getWidth(), screenSize.getHeight());
    overlay.setColor(0, 0, 0, OVERLAY_ALPHA);
    overlay.setVisible(true);
    stage.addActor(overlay);

    Texture dialogTex = new Texture(Gdx.files.internal("images/ui/dialog.png"));
    TextureRegionDrawable dialogDrawable = new TextureRegionDrawable(dialogTex);

    dialogTable = new Table();
    dialogTable.setBackground(dialogDrawable);
    dialogTable.setSize(
        Math.floorDiv((int) screenSize.getWidth(), 3),
        Math.floorDiv((int) screenSize.getHeight(), 5));
    dialogTable.setPosition(
        (screenSize.getWidth() - dialogTable.getWidth()) / 2f,
        (screenSize.getHeight() - dialogTable.getHeight()) / 5f);

    Table contentTable = new Table();
    contentTable.setFillParent(true);

    messageLabel = ui.text(tutorialMessages[step]);
    messageLabel.setWrap(true);
    messageLabel.setAlignment(Align.center);

    contentTable
        .add(messageLabel)
        .width(dialogTable.getWidth() - DIALOG_WIDTH_PAD)
        .expand()
        .center()
        .row();
    dialogTable.add(contentTable).expand().fill();
    dialogTable.align(Align.center);

    stage.addActor(dialogTable);
    TextButton nextButton = ui.primaryButton("CONTINUE", 150);
    TextButton previousButton = ui.primaryButton("PREVIOUS", 150);
    TextButton skipButton = ui.primaryButton("SKIP TUTORIAL", 100);

    messageTable = new Table();
    messageTable.setFillParent(true);
    messageTable.add(previousButton).expandY().bottom().padBottom(HINT_BOTTOM_PAD).padRight(10);
    messageTable.add(nextButton).expandY().bottom().padBottom(HINT_BOTTOM_PAD).padLeft(10);

    stage.addActor(messageTable);

    skipButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            skipTutorial();
          }
        });

    // Listener for the "continue" button
    nextButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            entity.getEvents().trigger(TRAVERSE, true);
          }
        });

    // Listener for the "previous" button
    previousButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            entity.getEvents().trigger(TRAVERSE, false);
          }
        });

    skipTable = new Table();
    skipTable.bottom().right().pad(20f);
    skipTable.setFillParent(true);
    skipTable.add(skipButton);
    stage.addActor(skipTable);

    ServiceLocator.getTimeSource().setTimeScale(0.01f);
  }

  /**
   * Changes the tutorial message depending on which button the user presses.
   *
   * @param forward determines whether the user progresses through the tutorial or backwards.
   */
  public void changeText(boolean forward) {
    if (!active) return;

    if (forward) {
      if (step < tutorialMessages.length - 1) {
        nextStep();
      } else {
        endTutorial();
      }
    } else {
      if (step > 0) {
        previousStep();
      }
    }
  }

  /**
   * Advances to the next tutorial message. If the final message has been shown, end the tutorial.
   */
  private void nextStep() {
    step++;
    if (step < tutorialMessages.length) {
      messageLabel.setText(tutorialMessages[step]);
    } else {
      endTutorial();
    }
  }

  /**
   * Shows the previous tutorial message. If it is pressed on the first tutorial message, nothing
   * will happen.
   */
  private void previousStep() {
    step--;
    if (step >= 0) {
      messageLabel.setText(tutorialMessages[step]);
    }
  }

  /** Ends the tutorial and resumes gameplay. Hides all tutorial UI elements. */
  private void endTutorial() {
    active = false;
    overlay.setVisible(false);
    dialogTable.setVisible(false);
    messageTable.setVisible((false));
    skipTable.setVisible(false);
    ServiceLocator.getTimeSource().setTimeScale(1f);
  }

  /**
   * Skips the tutorial immediately and resumes gameplay. Equivalent to ending the tutorial early.
   */
  private void skipTutorial() {
    endTutorial();
  }

  /** A listener that determines whether the player clicks on the next tutorial message or not. */
  private void traverseTutorial() {
    entity
        .getEvents()
        .addListener(
            TRAVERSE,
            input -> {
              boolean value = (boolean) input;
              changeText(value);
            });
  }

  /** Cleans up tutorial UI elements and removes them from the stage. */
  @Override
  public void dispose() {
    super.dispose();
    overlay.remove();
    dialogTable.remove();
    messageTable.remove();
    skipTable.remove();
  }

  /**
   * Draw method override from {@link UIComponent}. No manual drawing is required as Scene2D handles
   * rendering.
   *
   * @param batch Batch that the SpriteBatch used for rendering.
   */
  @Override
  protected void draw(SpriteBatch batch) {
    // Scene2D actors handle their own drawing
  }
}
