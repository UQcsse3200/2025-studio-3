package com.csse3200.game.ui.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.csse3200.game.services.GameTime;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.TypographyFactory;
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

  /** Table containing the "Skip Tutorial" button. */
  private Table skipTable;

  /** Full-screen dark overlay to dim the background. */
  private Image overlay;

  /** Label displaying the current tutorial message. */
  private Label messageLabel;

  /** Reference to the game's time controller for pausing and resuming the game. */
  private final GameTime gameTime;

  /** Current tutorial step index. */
  private int step = 0;

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

  /** Button size for the next button. */
  private static final float BUTTON_SIZE = 30f;

  /** Right padding for the next button. */
  private static final float BUTTON_RIGHT_PAD = 60f;

  /** Bottom padding for the next button. */
  private static final float BUTTON_BOTTOM_PAD = 50f;

  /** Dialog width padding. */
  private static final float DIALOG_WIDTH_PAD = 100f;

  /** Constructs a new LevelMapTutorial with a reference to the game time controller. */
  public LevelMapTutorial() {
    this.gameTime = ServiceLocator.getTimeSource();
    this.gameTime.setTimeScale(0);
  }

  /**
   * Initialises the tutorial UI components, including the overlay, dialog box, message label, next
   * button and skip tutorial button. Pauses the game while active.
   */
  @Override
  public void create() {
    super.create();

    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888); // create dark overlay
    pixmap.setColor(0, 0, 0, 1); // Solid black
    pixmap.fill();
    Texture blackTex = new Texture(pixmap);
    pixmap.dispose(); // clean up after creating texture

    overlay = new Image(new TextureRegionDrawable(blackTex));

    overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    overlay.setColor(0, 0, 0, OVERLAY_ALPHA);
    overlay.setVisible(true);
    stage.addActor(overlay);

    Texture dialogTex = new Texture(Gdx.files.internal("images/ui/dialog.png"));
    TextureRegionDrawable dialogDrawable = new TextureRegionDrawable(dialogTex);

    dialogTable = new Table();
    dialogTable.setBackground(dialogDrawable);
    dialogTable.setSize(
        Math.floorDiv(Gdx.graphics.getWidth(), 3), Math.floorDiv(Gdx.graphics.getHeight(), 5));
    dialogTable.setPosition(
        (Gdx.graphics.getWidth() - dialogTable.getWidth()) / 2f,
        (Gdx.graphics.getHeight() - dialogTable.getHeight()) / 5f);

    Table contentTable = new Table();
    contentTable.setFillParent(true);

    messageLabel =
        TypographyFactory.createSubtitle(
            tutorialMessages[step], com.badlogic.gdx.graphics.Color.WHITE);
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

    // next button
    Texture nextTexture = new Texture(Gdx.files.internal("images/ui/skip-icon.png"));
    Drawable nextDrawable = new TextureRegionDrawable(nextTexture);
    ImageButton nextButton = new ImageButton(nextDrawable);
    nextButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            nextStep();
          }
        });

    dialogTable
        .add(nextButton)
        .size(BUTTON_SIZE, BUTTON_SIZE)
        .expandX()
        .right()
        .bottom()
        .padRight(BUTTON_RIGHT_PAD)
        .padBottom(BUTTON_BOTTOM_PAD);
    stage.addActor(dialogTable);

    // button to skip tutorial
    TextButton skipButton = new TextButton("Skip Tutorial", skin);
    skipButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            skipTutorial();
          }
        });

    skipTable = new Table();
    skipTable.bottom().right().pad(20f);
    skipTable.setFillParent(true);
    skipTable.add(skipButton);
    stage.addActor(skipTable);

    pauseGame();
  }

  /**
   * Updates the tutorial logic based on user input. Advances the tutorial when the space bar or the
   * 'next' button is pressed. Ends the tutorial after the final step.
   */
  @Override
  public void update() {
    if (!active) return;

    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
      if (step < tutorialMessages.length - 1) {
        nextStep(); // press space bar to move onto next message
      } else {
        endTutorial(); // press space bar to end tutorial
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

  /** Ends the tutorial and resumes gameplay. Hides all tutorial UI elements. */
  private void endTutorial() {
    active = false;
    overlay.setVisible(false);
    dialogTable.setVisible(false);
    skipTable.setVisible(false);
    this.gameTime.setTimeScale(1);
    resumeGame();
  }

  /**
   * Skips the tutorial immediately and resumes gameplay. Equivalent to ending the tutorial early.
   */
  private void skipTutorial() {
    endTutorial();
  }

  /** Pauses the game by setting the timescale to zero. */
  private void pauseGame() {
    gameTime.setTimeScale(0f);
  }

  /** Resumes the game by restoring the timescale to normal. */
  private void resumeGame() {
    gameTime.setTimeScale(1f);
  }

  /** Cleans up tutorial UI elements and removes them from the stage. */
  @Override
  public void dispose() {
    super.dispose();
    overlay.remove();
    dialogTable.remove();
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
