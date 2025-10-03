package com.csse3200.game.ui;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.services.ServiceLocator;

/** Factory class for creating standardized buttons using the btn-blue texture atlas. */
public class ButtonFactory {
  private static final String BTN_ATLAS_PATH = "images/ui/btn-blue.atlas";
  private static final String BTN_REGION = "default";

  /** Private constructor to prevent instantiation */
  private ButtonFactory() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Creates a TextButton using the standard btn-blue texture atlas.
   *
   * @param text The text to display on the button
   * @return A TextButton with the btn-blue styling
   */
  public static TextButton createButton(String text) {
    TextButton button = createButton(text, 1.5f);
    buttonPressedListener(button);
    return button;
  }

  /**
   * Creates a small TextButton using the standard btn-blue texture atlas.
   *
   * @param text The text to display on the button
   * @return A TextButton with smaller font styling and 3f left/right padding
   */
  public static TextButton createSmallButton(String text) {
    TextButton button = createButton(text, 1.0f);
    button.pad(0f, 3f, 0f, 3f); // top, left, bottom, right
    buttonPressedListener(button);
    return button;
  }

  /**
   * Creates a large TextButton using the standard btn-blue texture atlas.
   *
   * @param text The text to display on the button
   * @return A TextButton with larger font styling and 5f left/right padding
   */
  public static TextButton createLargeButton(String text) {
    TextButton button = createButton(text, 2.0f);
    button.pad(0f, 5f, 0f, 5f); // top, left, bottom, right
    buttonPressedListener(button);
    return button;
  }

  /**
   * Creates a dialog TextButton with specific styling for dialog boxes.
   *
   * @param text The text to display on the button
   * @return A TextButton styled for dialogs with wider width and 1.5f font scale
   */
  public static TextButton createDialogButton(String text) {
    TextButton button = createButton(text, 1.5f);
    button.pad(5f, 10f, 5f, 10f); // top, left, bottom, right - wider padding
    buttonPressedListener(button);
    return button;
  }

  /**
   * Creates a TextButton using the standard btn-blue texture atlas with custom font scale.
   *
   * @param text The text to display on the button
   * @param fontScale The scale factor for the font
   * @return A TextButton with the btn-blue styling
   */
  public static TextButton createButton(String text, float fontScale) {
    TextButton button = createButton(text, fontScale, null);
    buttonPressedListener(button);
    return button;
  }

  /**
   * Creates a TextButton using the standard btn-blue texture atlas with custom font.
   *
   * @param text The text to display on the button
   * @param fontScale The scale factor for the font (ignored if customFont is provided)
   * @param customFont The custom font to use, or null to use global font
   * @return A TextButton with the btn-blue styling
   */
  public static TextButton createButton(String text, float fontScale, BitmapFont customFont) {
    TextureAtlas buttonAtlas =
        ServiceLocator.getGlobalResourceService().getAsset(BTN_ATLAS_PATH, TextureAtlas.class);

    Drawable background = new TextureRegionDrawable(buttonAtlas.findRegion(BTN_REGION));

    BitmapFont font;
    if (customFont != null) {
      font = customFont;
    } else {
      // Use the global Jersey10-Regular font with specified size
      font =
          ServiceLocator.getGlobalResourceService()
              .generateFreeTypeFont("Default", (int) (18 * fontScale));
      if (font == null) {
        // Fallback to default font if global font is not available
        font = new BitmapFont();
        font.getData().setScale(fontScale);
      }
      font.setColor(Color.BLACK);
    }

    TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
    style.up = background;
    style.down = background;
    style.over = background;
    style.font = font;
    style.fontColor = Color.WHITE;
    style.overFontColor = Color.CYAN;
    style.downFontColor = Color.CYAN;

    TextButton button = new TextButton(text, style);
    buttonPressedListener(button);
    return button;
  }

  public static void buttonPressedListener(TextButton button) {
    button.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            buttonPressedSound();
          }
        });
  }

  public static void buttonPressedSound() {
    // Play sound effect for button click
    Sound buttonSound =
        ServiceLocator.getGlobalResourceService()
            .getAsset("sounds/button_clicked.mp3", Sound.class);
    if (buttonSound != null) {
      float volume = ServiceLocator.getSettingsService().getSoundVolume();
      buttonSound.play(volume);
    }
  }
}
