package com.csse3200.game.components.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.csse3200.game.services.DialogService.DialogType;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import java.util.function.Consumer;
import net.dermetfan.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A flexible dialog component that can display warning, error, or info messages. Supports
 * customizable positioning, actions, and animations.
 */
public class DialogComponent extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(DialogComponent.class);
  private static final float Z_INDEX = 100f;
  private static final float ANIMATION_DURATION = 0.3f;
  private Window dialog;
  private DialogType dialogType;
  private String title;
  private String message;
  private boolean isVisible = false;
  private Consumer<DialogComponent> onConfirm;
  private Consumer<DialogComponent> onCancel;
  private Consumer<DialogComponent> onClose;
  private float uiScale = ui.getUIScale();
  private float defaultWidth = 704f * uiScale;
  private float defaultHeight = 352f * uiScale;

  /**
   * Creates a new dialog component with the specified type, title, and message.
   *
   * @param dialogType the type of dialog (INFO, WARNING, ERROR)
   * @param title the title of the dialog
   * @param message the message content of the dialog
   */
  public DialogComponent(DialogType dialogType, String title, String message) {
    this.dialogType = dialogType;
    this.title = title;
    this.message = message;
  }

  @Override
  public void create() {
    super.create();
    createDialog();
  }

  /** Creates the dialog window with appropriate styling based on the dialog type. */
  private void createDialog() {
    dialog = new Window("", skin);
    dialog.setSize(defaultWidth, defaultHeight);
    dialog.setMovable(false);
    dialog.setModal(true);

    // Apply type-specific styling
    applyDialogTypeStyling();

    // Create content table & title label
    Table contentTable = new Table();
    contentTable.pad(50f * uiScale);
    Color titleColor = getTextColor();
    Label titleLabel = ui.heading(title);
    titleLabel.setColor(titleColor);
    titleLabel.setAlignment(Align.center);
    contentTable.add(titleLabel).width(defaultWidth - (120f * uiScale)).center().padBottom(20f * uiScale).row();

    // Add message label
    Label messageLabel = ui.text(message);
    messageLabel.setWrap(true);
    messageLabel.setAlignment(Align.center);
    contentTable.add(messageLabel).width(defaultWidth - (120f * uiScale)).center().padBottom(20f*uiScale).row();
    contentTable.center();

    // Add buttons based on dialog type
    addButtons(contentTable);
    dialog.add(contentTable);
    dialog.pack();
    dialog.setSize(defaultWidth, defaultHeight);

    // Center the dialog
    centerDialog();

    // Fade in animation
    dialog.setVisible(false);
    dialog.setColor(1, 1, 1, 0);
    stage.addActor(dialog);
  }

  /** Applies styling specific to the dialog type. */
  private void applyDialogTypeStyling() {
    Window.WindowStyle windowStyle = new Window.WindowStyle(skin.get(Window.WindowStyle.class));

    // Set the dialog background image
    try {
      Texture dialogTexture =
          ServiceLocator.getGlobalResourceService().getAsset("images/ui/dialog_new_new.png", Texture.class);
      if (dialogTexture != null) {
        TextureRegion dialogRegion = new TextureRegion(dialogTexture);
        Drawable dialogDrawable = new TextureRegionDrawable(dialogRegion);
        windowStyle.background = dialogDrawable;
      }
    } catch (Exception e) {
      logger.warn(
          "[DialogComponent] Could not load dialog background image: {}", e.getMessage());
    }
    dialog.setStyle(windowStyle);
  }

  /** Gets the appropriate text color for the dialog type. */
  private Color getTextColor() {
    switch (dialogType) {
      case INFO:
        return Color.CYAN;
      case WARNING:
        return Color.ORANGE;
      case ERROR:
        return Color.RED;
      case SKILL:
        return Color.GOLD;
      case GAME_OVER:
        return Color.RED;
      case WIN_GAME:
        return Color.CYAN;
      default:
        return Color.WHITE;
    }
  }

  /** Adds appropriate buttons to the dialog based on its type. */
  private void addButtons(Table contentTable) {
    Table buttonTable = new Table();

    switch (dialogType) {
      case INFO:
        addInfoButtons(buttonTable);
        break;
      case WARNING:
        addWarningButtons(buttonTable);
        break;
      case ERROR:
        addErrorButtons(buttonTable);
        break;
      case SKILL:
        addSkillButtons(buttonTable);
        break;
      case GAME_OVER:
        addGameOverButtons(buttonTable);
        break;
      case WIN_GAME:
        addWinGameButtons(buttonTable);
        break;
    }

    contentTable.add(buttonTable).center();
  }

  /** Adds buttons for INFO dialog type. */
  private void addInfoButtons(Table buttonTable) {
    Pair<Float, Float> buttonDimensions = ui.getScaledDimensions(120f);

    TextButton okButton = ui.primaryButton("OK", 120f);
    okButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            hide();
            if (onClose != null) {
              onClose.accept(DialogComponent.this);
            }
          }
        });
    buttonTable.add(okButton).size(buttonDimensions.getKey(), buttonDimensions.getValue()).pad(5f);
  }

  /** Adds buttons for GAME_OVER dialog type. */
  private void addGameOverButtons(Table buttonTable) {
    Pair<Float, Float> quitDimensions = ui.getScaledDimensions(120f);
    Pair<Float, Float> playAgainDimensions = ui.getScaledDimensions(150f);

    TextButton playAgainButton = ui.primaryButton("Play Again", 150f);
    playAgainButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            hide();
            if (onConfirm != null) {
              onConfirm.accept(DialogComponent.this);
            }
          }
        });

    TextButton quitButton = ui.primaryButton("Quit", 120f);
    quitButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            hide();
            if (onCancel != null) {
              onCancel.accept(DialogComponent.this);
            }
          }
        });
    buttonTable
        .add(playAgainButton)
        .size(playAgainDimensions.getKey(), playAgainDimensions.getValue())
        .pad(5f);
    buttonTable.add(quitButton).size(quitDimensions.getKey(), quitDimensions.getValue()).pad(5f);
  }

  /** Adds buttons for WIN_GAME dialog type. */
  private void addWinGameButtons(Table buttonTable) {
    Pair<Float, Float> buttonDimensions = ui.getScaledDimensions(150f);

    TextButton continueButton = ui.primaryButton("Continue", 150f);
    continueButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            hide();
            if (onConfirm != null) {
              onConfirm.accept(DialogComponent.this);
            }
          }
        });

    buttonTable
        .add(continueButton)
        .size(buttonDimensions.getKey(), buttonDimensions.getValue())
        .pad(5f);
  }

  /** Adds buttons for WARNING dialog type. */
  private void addWarningButtons(Table buttonTable) {
    Pair<Float, Float> buttonDimensions = ui.getScaledDimensions(120f);
    Pair<Float, Float> cancelDimensions = ui.getScaledDimensions(150f);

    TextButton cancelButton = ui.primaryButton("Cancel", 150f);
    cancelButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            hide();
            if (onCancel != null) {
              onCancel.accept(DialogComponent.this);
            }
          }
        });

    TextButton continueButton = ui.primaryButton("Continue", 120f);
    continueButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            hide();
            if (onConfirm != null) {
              onConfirm.accept(DialogComponent.this);
            }
          }
        });

    buttonTable
        .add(cancelButton)
        .size(buttonDimensions.getKey(), buttonDimensions.getValue())
        .pad(5f);
    buttonTable
        .add(continueButton)
        .size(cancelDimensions.getKey(), buttonDimensions.getValue())
        .pad(5f);
  }

  /** Adds buttons for ERROR dialog type. */
  private void addErrorButtons(Table buttonTable) {
    Pair<Float, Float> buttonDimensions = ui.getScaledDimensions(120f);

    TextButton okButtonError = ui.primaryButton("OK", 120f);
    okButtonError.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            hide();
            if (onClose != null) {
              onClose.accept(DialogComponent.this);
            }
          }
        });
    buttonTable
        .add(okButtonError)
        .size(buttonDimensions.getKey(), buttonDimensions.getValue())
        .pad(5f);
  }

  /** Adds buttons for SKILL dialog type. */
  private void addSkillButtons(Table buttonTable) {
    Pair<Float, Float> buttonDimensions = ui.getScaledDimensions(120f);

    TextButton closeButton = ui.primaryButton("Close", 120f);
    closeButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            hide();
            if (onClose != null) {
              onClose.accept(DialogComponent.this);
            }
          }
        });

    TextButton unlockButton = ui.primaryButton("Unlock", 150f);
    unlockButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            if (onConfirm != null) {
              onConfirm.accept(DialogComponent.this);
            }
            // Note: Dialog hiding is handled by the callback
          }
        });

    buttonTable
        .add(closeButton)
        .size(buttonDimensions.getKey(), buttonDimensions.getValue())
        .pad(5f);
    buttonTable
        .add(unlockButton)
        .size(buttonDimensions.getKey(), buttonDimensions.getValue())
        .pad(5f);
  }

  /** Centers the dialog on the screen. */
  private void centerDialog() {
    float x = (stage.getWidth() - dialog.getWidth()) / 2f;
    float y = (stage.getHeight() - dialog.getHeight()) / 2f;
    dialog.setPosition(x, y);
  }

  /** Handles window resize by re-centering the dialog. */
  @Override
  public void resize() {
    if (dialog != null && isVisible) {
      centerDialog();
    }
  }

  /** Shows the dialog with a fade-in animation. */
  public void show() {
    if (isVisible || dialog == null) {
      return;
    }

    logger.debug("[DialogComponent] Showing {} dialog: {}", dialogType, title);
    isVisible = true;
    dialog.setVisible(true);

    // Fade in animation
    dialog.clearActions();
    dialog.addAction(Actions.fadeIn(ANIMATION_DURATION));
  }

  /** Hides the dialog with a fade-out animation. */
  public void hide() {
    if (!isVisible || dialog == null) {
      return;
    }

    logger.debug("[DialogComponent] Hiding {} dialog: {}", dialogType, title);
    isVisible = false;

    // Fade out animation
    dialog.clearActions();
    dialog.addAction(
        Actions.sequence(
            Actions.fadeOut(ANIMATION_DURATION), Actions.run(() -> dialog.setVisible(false))));
  }

  /** Immediately removes the dialog from the stage. */
  @Override
  public void dispose() {
    if (dialog != null) {
      dialog.remove();
    }
  }

  /**
   * Sets the position of the dialog.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public void setPosition(float x, float y) {
    if (dialog != null) {
      dialog.setPosition(x, y);
    }
  }

  /**
   * Sets the size of the dialog.
   *
   * @param width the width
   * @param height the height
   */
  public void setSize(float width, float height) {
    if (dialog != null) {
      dialog.setSize(width, height);
    }
  }

  /**
   * Sets the callback for when the confirm/continue button is clicked.
   *
   * @param onConfirm the callback function
   */
  public void setOnConfirm(Consumer<DialogComponent> onConfirm) {
    this.onConfirm = onConfirm;
  }

  /**
   * Sets the callback for when the cancel button is clicked.
   *
   * @param onCancel the callback function
   */
  public void setOnCancel(Consumer<DialogComponent> onCancel) {
    this.onCancel = onCancel;
  }

  /**
   * Sets the callback for when the dialog is closed.
   *
   * @param onClose the callback function
   */
  public void setOnClose(Consumer<DialogComponent> onClose) {
    this.onClose = onClose;
  }

  /**
   * Gets the current dialog type.
   *
   * @return the dialog type
   */
  public DialogType getDialogType() {
    return dialogType;
  }

  /**
   * Gets the dialog title.
   *
   * @return the title
   */
  public String getTitle() {
    return title;
  }

  /**
   * Gets the dialog message.
   *
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * Checks if the dialog is currently visible.
   *
   * @return true if visible, false otherwise
   */
  public boolean isVisible() {
    return isVisible;
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void draw(SpriteBatch batch) {
    // Dialog is handled by the stage, no custom drawing needed
  }
}
