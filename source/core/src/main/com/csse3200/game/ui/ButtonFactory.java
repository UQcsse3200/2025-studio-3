package com.csse3200.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.services.ServiceLocator;

/** Factory class for creating standardized buttons using the btn-blue texture atlas. */
public class ButtonFactory {
  private static final String BTN_ATLAS_PATH = "images/ui/btn-blue.atlas";
  private static final String BTN_REGION = "default";

  // Private constructor to prevent instantiation
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
    return createButton(text, 1.5f);
  }

  /**
   * Creates a small TextButton using the standard btn-blue texture atlas.
   *
   * @param text The text to display on the button
   * @return A TextButton with smaller font styling
   */
  public static TextButton createSmallButton(String text) {
    return createButton(text, 1.0f);
  }

  /**
   * Creates a large TextButton using the standard btn-blue texture atlas.
   *
   * @param text The text to display on the button
   * @return A TextButton with larger font styling
   */
  public static TextButton createLargeButton(String text) {
    return createButton(text, 2.0f);
  }

  /**
   * Creates a TextButton using the standard btn-blue texture atlas with custom font scale.
   *
   * @param text The text to display on the button
   * @param fontScale The scale factor for the font
   * @return A TextButton with the btn-blue styling
   */
  public static TextButton createButton(String text, float fontScale) {
    return createButton(text, fontScale, null);
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

    TextButton button = new TextButton(text, style);
    button.getLabel().setColor(new Color(0.1f, 0.1f, 0.1f, 1f));
    return button;
  }
}
