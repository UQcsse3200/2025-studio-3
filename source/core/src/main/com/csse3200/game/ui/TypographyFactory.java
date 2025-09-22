package com.csse3200.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.csse3200.game.services.ServiceLocator;

/** Factory class for creating standardized typography using the Jersey10-Regular TTF font. */
public class TypographyFactory {
  private static final String FONT_KEY = "Default";

  // Font sizes for different typography levels
  private static final int TITLE_SIZE = 48;
  private static final int SUBTITLE_SIZE = 32;
  private static final int PARAGRAPH_SIZE = 24;

  // Default colors
  private static final Color DEFAULT_TITLE_COLOR = Color.WHITE;
  private static final Color DEFAULT_SUBTITLE_COLOR = Color.WHITE;
  private static final Color DEFAULT_PARAGRAPH_COLOR = Color.WHITE;

  // Private constructor to prevent instantiation
  private TypographyFactory() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Creates a title label with large text size.
   *
   * @param text The text to display
   * @return A Label with title styling
   */
  public static Label createTitle(String text) {
    return createTitle(text, DEFAULT_TITLE_COLOR);
  }

  /**
   * Creates a title label with large text size and custom color.
   *
   * @param text The text to display
   * @param color The text color
   * @return A Label with title styling
   */
  public static Label createTitle(String text, Color color) {
    BitmapFont font = generateFont(TITLE_SIZE);
    return createLabel(text, font, color);
  }

  /**
   * Creates a subtitle label with medium text size.
   *
   * @param text The text to display
   * @return A Label with subtitle styling
   */
  public static Label createSubtitle(String text) {
    return createSubtitle(text, DEFAULT_SUBTITLE_COLOR);
  }

  /**
   * Creates a subtitle label with medium text size and custom color.
   *
   * @param text The text to display
   * @param color The text color
   * @return A Label with subtitle styling
   */
  public static Label createSubtitle(String text, Color color) {
    BitmapFont font = generateFont(SUBTITLE_SIZE);
    return createLabel(text, font, color);
  }

  /**
   * Creates a paragraph label with standard text size.
   *
   * @param text The text to display
   * @return A Label with paragraph styling
   */
  public static Label createParagraph(String text) {
    return createParagraph(text, DEFAULT_PARAGRAPH_COLOR);
  }

  /**
   * Creates a paragraph label with standard text size and custom color.
   *
   * @param text The text to display
   * @param color The text color
   * @return A Label with paragraph styling
   */
  public static Label createParagraph(String text, Color color) {
    BitmapFont font = generateFont(PARAGRAPH_SIZE);
    return createLabel(text, font, color);
  }

  /**
   * Creates a label with custom font size.
   *
   * @param text The text to display
   * @param fontSize The font size in pixels
   * @return A Label with custom font size
   */
  public static Label createCustomSize(String text, int fontSize) {
    return createCustomSize(text, fontSize, Color.WHITE);
  }

  /**
   * Creates a label with custom font size and color.
   *
   * @param text The text to display
   * @param fontSize The font size in pixels
   * @param color The text color
   * @return A Label with custom font size and color
   */
  public static Label createCustomSize(String text, int fontSize, Color color) {
    BitmapFont font = generateFont(fontSize);
    return createLabel(text, font, color);
  }

  /**
   * Generates a BitmapFont using the Jersey10-Regular TTF font.
   *
   * @param size The font size in pixels
   * @return A BitmapFont with the specified size
   */
  private static BitmapFont generateFont(int size) {
    BitmapFont font =
        ServiceLocator.getGlobalResourceService().generateFreeTypeFont(FONT_KEY, size);
    if (font == null) {
      // Fallback to default font if global font is not available
      font = new BitmapFont();
      font.getData().setScale(size / 18f); // 18 is roughly the default font size
    }
    return font;
  }

  /**
   * Creates a Label with the specified font and color.
   *
   * @param text The text to display
   * @param font The BitmapFont to use
   * @param color The text color
   * @return A Label with the specified styling
   */
  private static Label createLabel(String text, BitmapFont font, Color color) {
    Label.LabelStyle style = new Label.LabelStyle();
    style.font = font;
    style.fontColor = color;
    return new Label(text, style);
  }
}
