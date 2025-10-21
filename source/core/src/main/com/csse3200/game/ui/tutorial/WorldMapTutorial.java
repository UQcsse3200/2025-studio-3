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

  /**
   * Initialises the tutorial UI components, matching the style used by {@link LevelMapTutorial}.
   */
  @Override
  public void create() {
    super.create();

    // Build messages with current key bindings
    SettingsService settingsService = ServiceLocator.getSettingsService();
    String upKeyName = Input.Keys.toString(settingsService.getSettings().getUpButton());
    String downKeyName = Input.Keys.toString(settingsService.getSettings().getDownButton());
    String leftKeyName = Input.Keys.toString(settingsService.getSettings().getLeftButton());
    String rightKeyName = Input.Keys.toString(settingsService.getSettings().getRightButton());
    String interactKeyName =
        Input.Keys.toString(settingsService.getSettings().getInteractionButton());
    // Zoom currently bound to fixed keys
    String zoomOutKeyName = Input.Keys.toString(Input.Keys.Q);
    String zoomInKeyName = Input.Keys.toString(Input.Keys.K);

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

    int skipKey = ServiceLocator.getSettingsService().getSettings().getSkipButton();
    String continueKeyName = Input.Keys.toString(skipKey);
    String titleText = (PRESS + continueKeyName + " to continue").toUpperCase();

    dialogWindow = ui.createWindow(titleText);
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

    Table rootTable = new Table();
    rootTable.setFillParent(false);
    rootTable.align(Align.bottom);
    rootTable.add(dialogWindow).width(600f).height(200f).padBottom(80f);
    stack.add(rootTable);
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

    int skipKey = ServiceLocator.getSettingsService().getSettings().getSkipButton();
    if (Gdx.input.isKeyJustPressed(skipKey)) {
      if (step < tutorialMessages.length - 1) {
        nextStep();
      } else {
        endTutorial();
      }
    }
  }

  @Override
  public void dispose() {
    super.dispose();
    stage.clear();
  }
}
