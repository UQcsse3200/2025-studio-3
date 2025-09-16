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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A flexible dialog component that can display warning, error, or info messages. Supports
 * customizable positioning, actions, and animations.
 */
public class DialogComponent extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(DialogComponent.class);
  private static final float Z_INDEX = 100f;
  private static final float DEFAULT_WIDTH = 320f;
  private static final float DEFAULT_HEIGHT = 240f;
  private static final float ANIMATION_DURATION = 0.3f;
  private Window dialog;
  private DialogType dialogType;
  private String title;
  private String message;
  private boolean isVisible = false;
  private Consumer<DialogComponent> onConfirm;
  private Consumer<DialogComponent> onCancel;
  private Consumer<DialogComponent> onClose;

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
    // Create window without title (we'll add it manually)
    dialog = new Window("", skin);
    dialog.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    dialog.setMovable(false);
    dialog.setModal(true);

    // Apply type-specific styling
    applyDialogTypeStyling();

    // Create content table
    Table contentTable = new Table();
    contentTable.pad(20f);

    // Add title label (centered and bigger)
    Color titleColor = getTextColor();
    logger.debug("Setting title color to: {}", titleColor);

    // Create custom label style with the title color
    Label.LabelStyle titleStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
    titleStyle.fontColor = titleColor;
    Label titleLabel = new Label(title, titleStyle);
    titleLabel.setAlignment(Align.center);
    titleLabel.setFontScale(1.3f);
    contentTable.add(titleLabel).width(DEFAULT_WIDTH - 40f).center().padBottom(15f).row();

    // Add message label (always white)
    Label.LabelStyle messageStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
    messageStyle.fontColor = Color.WHITE;
    Label messageLabel = new Label(message, messageStyle);
    messageLabel.setWrap(true);
    messageLabel.setAlignment(Align.center);
    logger.debug("Setting message color to: WHITE");
    contentTable.add(messageLabel).width(DEFAULT_WIDTH - 40f).center().padBottom(20f).row();

    // Add buttons based on dialog type
    addButtons(contentTable);
    dialog.add(contentTable);
    dialog.pack();

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
          ServiceLocator.getGlobalResourceService().getAsset("images/dialog.png", Texture.class);
      if (dialogTexture != null) {
        TextureRegion dialogRegion = new TextureRegion(dialogTexture);
        Drawable dialogDrawable = new TextureRegionDrawable(dialogRegion);
        windowStyle.background = dialogDrawable;
      }
    } catch (Exception e) {
      logger.warn("Could not load dialog.png background image: {}", e.getMessage());
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
      default:
        return Color.WHITE;
    }
  }

  /** Adds appropriate buttons to the dialog based on its type. */
  private void addButtons(Table contentTable) {
    Table buttonTable = new Table();

    switch (dialogType) {
      // Info dialog buttons
      case INFO:
        TextButton okButton = new TextButton("OK", skin);
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
        buttonTable.add(okButton).pad(5f);
        break;

      // Warning dialog buttons
      case WARNING:
        TextButton cancelButton = new TextButton("Cancel", skin);
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

        TextButton continueButton = new TextButton("Continue", skin);
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

        buttonTable.add(cancelButton).pad(5f);
        buttonTable.add(continueButton).pad(5f);
        break;

      // Error dialog buttons
      case ERROR:
        TextButton okButtonError = new TextButton("OK", skin);
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
        buttonTable.add(okButtonError).pad(5f);
        break;
    }

    contentTable.add(buttonTable).center();
  }

  /** Centers the dialog on the screen. */
  private void centerDialog() {
    float x = (stage.getWidth() - dialog.getWidth()) / 2f;
    float y = (stage.getHeight() - dialog.getHeight()) / 2f;
    dialog.setPosition(x, y);
  }

  /** Handles window resize by re-centering the dialog. */
  public void resize() {
    if (dialog != null && isVisible) {
      centerDialog();
    }
  }

  /** Shows the dialog with a fade-in animation. */
  public void show() {
    if (isVisible) {
      return;
    }

    logger.debug("Showing {} dialog: {}", dialogType, title);
    isVisible = true;
    dialog.setVisible(true);

    // Fade in animation
    dialog.clearActions();
    dialog.addAction(Actions.fadeIn(ANIMATION_DURATION));
  }

  /** Hides the dialog with a fade-out animation. */
  public void hide() {
    if (!isVisible) {
      return;
    }

    logger.debug("Hiding {} dialog: {}", dialogType, title);
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
    dialog.setPosition(x, y);
  }

  /**
   * Sets the size of the dialog.
   *
   * @param width the width
   * @param height the height
   */
  public void setSize(float width, float height) {
    dialog.setSize(width, height);
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
