package com.csse3200.game.ui.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.services.SettingsService;
import com.csse3200.game.ui.UIComponent;
import net.dermetfan.utils.Pair;

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
  /** Full-screen dark overlay to dim the background. */
  private Image overlay;

  /** Label displaying the current tutorial message. */
  private Label messageLabel;

  /** Current tutorial step index. */
  private int step = 0;

  /** Boolean to determine whether the tutorial is active and listening for inputs. */
  private boolean active = true;

  /** Dialog window containing the tutorial messages. */
  private Window dialogWindow;

  /** Array of tutorial messages shown in sequence. */
  private final String[] tutorialMessages = {
    "Welcome to Level 1!",
    "Drag defence units from the hot-bar onto the grid.",
    "Generators produce scrap metal which can be used to place defences.",
    "Human defenders attack incoming robot enemies."
  };

  /** Alpha transparency value for the overlay. */
  private static final float OVERLAY_ALPHA = 0.7f;

  /**
   * Initialises the tutorial UI components, including the overlay, dialog box, message label, next
   * button and skip tutorial button. Pauses the game while active.
   */
  @Override
  public void create() {
    super.create();

    Stack stack = new Stack();
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

    messageLabel = ui.subheading(tutorialMessages[step].toUpperCase());
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

    Table rootTable = new Table();
    rootTable.setFillParent(false);
    rootTable.align(Align.bottom);
    rootTable.add(dialogWindow).width(600f).height(200f).padBottom(80f);
    stack.add(rootTable);
    stage.addActor(stack);
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
      messageLabel.setText(tutorialMessages[step].toUpperCase());
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
    dialogWindow.setVisible(false);
    // Mark the tutorial as played when it actually ends
    ServiceLocator.getProfileService().getProfile().setPlayedLevelTutorial();
  }

  /** Updates the tutorial every frame, checking for keyboard input to advance through steps. */
  @Override
  public void update() {
    if (!active) return;

    SettingsService settingsService = ServiceLocator.getSettingsService();
    int skipKey = settingsService.getSettings().getSkipButton();

    if (Gdx.input.isKeyJustPressed(skipKey)) {
      if (step < tutorialMessages.length - 1) {
        nextStep();
      } else {
        endTutorial();
      }
    }
  }

  /** Cleans up tutorial UI elements and removes them from the stage. */
  @Override
  public void dispose() {
    super.dispose();
    stage.clear();
  }
}
