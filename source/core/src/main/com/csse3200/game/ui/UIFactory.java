package com.csse3200.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.services.ServiceLocator;

import net.dermetfan.utils.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory class for creating UI components with consistent styling.
 *
 * <p>NOTE: This is not a static util class, it is instantiated as a static instance in UIComponent.
 * This allows for the UI scale to be customized and the skin to be passed in to the constructor.
 */
public class UIFactory {
  private final Map<String, BitmapFont> fontCache = new HashMap<>();
  private final Skin skin;
  private float uiScale;
  private Color white;
  private Color offWhite;
  private Color gray;
  private Color red;
  private Color cyan;
  private Color gold;
  private Color orange;
  private Color yellow;
  private Color black;
  private Color clear;

  /**
   * Creates a UIFactory with the provided skin.
   *
   * @param skin the skin to use for styling
   * @param currentUIScale the UI scale to use for styling
   */
  public UIFactory(Skin skin, Settings.UIScale currentUIScale) {
    this.skin = skin;
    setUIScale(currentUIScale);
    white = skin.getColor("white");
    offWhite = skin.getColor("off-white");
    gray = skin.getColor("gray");
    red = skin.getColor("red");
    cyan = skin.getColor("cyan");
    gold = skin.getColor("gold");
    orange = skin.getColor("orange");
    yellow = skin.getColor("yellow");
    black = skin.getColor("black");
    clear = skin.getColor("clear");
  }

  /**
   * Sets the UI scale for the factory.
   *
   * @param currentUIScale the UI scale to use for styling
   */
  public void setUIScale(Settings.UIScale currentUIScale) {
    switch (currentUIScale) {
      case SMALL -> this.uiScale = 0.8f;
      case MEDIUM -> this.uiScale = 1.0f;
      case LARGE -> this.uiScale = 1.2f;
      default -> this.uiScale = 1.0f;
    }
  }

  /**
   * Gets the UI scale.
   *
   * @return the UI scale
   */
  public float getUIScale() {
    return uiScale;
  }

  /**
   * Creates a scaled font. Caches the result to avoid creating the same font multiple times.
   *
   * @param baseSize the base font size
   * @return a scaled BitmapFont
   */
  public BitmapFont createFont(int baseSize) {
    int scaledSize = (int) (baseSize * uiScale);
    String cacheKey = "font_" + scaledSize;

    return fontCache.computeIfAbsent(
        cacheKey,
        k -> ServiceLocator.getGlobalResourceService().generateFreeTypeFont("Default", scaledSize));
  }

  /**
   * Creates a custom label.
   *
   * @param text the text to display
   * @param fontSize the base font size
   * @param color the text color
   * @return a styled Label
   */
  public Label createLabel(String text, int fontSize, Color color) {
    BitmapFont font = createFont(fontSize);
    Label.LabelStyle style = new Label.LabelStyle(font, color);
    return new Label(text, style);
  }

  /**
   * Creates a title label with large text size.
   *
   * @param text the text to display
   * @return a styled Label with title styling
   */
  public Label title(String text) {
    return createLabel(text.toUpperCase(), 56, white);
  }

  /**
   * Creates a heading label with medium text size.
   *
   * @param text the text to display
   * @return a styled Label with heading styling
   */
  public Label heading(String text) {
    return createLabel(text, 32, white);
  }

  /**
   * Creates a subheading label with small text size.
   *
   * @param text the text to display
   * @return a styled Label with subheading styling
   */
  public Label subheading(String text) {
    return createLabel(text, 24, white);
  }

  /**
   * Creates a text label with small text size.
   *
   * @param text the text to display
   * @return a styled Label with text styling
   */
  public Label text(String text) {
    return createLabel(text, 18, white);
  }

  /**
   * Creates a subtext label with small text size.
   *
   * @param text the text to display
   * @return a styled Label with subtext styling
   */
  public Label subtext(String text) {
    return createLabel(text, 14, gray);
  }

  /**
   * Gets the scaled dimensions for a given width. USed for 
   * 
   * @param width the base width
   * @return the scaled dimensions, width and height
   */
  public Pair<Float, Float> getScaledDimensions(float width) {
    return new Pair<>(width * uiScale, 42f * uiScale);
  }

  /**
   * Creates a primary button with consistent styling and UI scale support. Unfortunately, to
   * make the buttons look crisp, we need to create the button style manually.
   * 
   * NOTE: This does not work by itself for tables. You will need to call the 
   * getScaledDimensions() method above and set the table cell width and height.
   *
   * @param text the button text
   * @return a styled TextButton
   */
  public TextButton primaryButton(String text, float width) {
    TextButtonStyle style = new TextButtonStyle();
    style.font = createFont(32);
    style.fontColor = white;  
    style.overFontColor = cyan;
    style.up = skin.getDrawable("b");
    style.down = skin.getDrawable("a");
    TextButton button = new TextButton(text.toUpperCase(), style);
    button.setWidth(width * uiScale);
    button.setHeight(42f * uiScale);
    button.getLabelCell().center();
    return button;
  }

  /**
   * Creates a secondary button with consistent styling and UI scale support.Unfortunately, to
   * make the buttons look crisp, we need to create the button style manually.
   * 
   * NOTE: This does not work by itself for tables. You will need to call the 
   * getScaledDimensions() method above and set the table cell width and height.
   *
   * @param text the button text
   * @return a styled TextButton
   */
  public TextButton secondaryButton(String text, float width) {
    TextButtonStyle style = new TextButtonStyle();
    style.font = createFont(32);
    style.fontColor = white;  
    style.overFontColor = cyan;
    style.up = skin.getDrawable("c");
    style.down = skin.getDrawable("d");
    TextButton button = new TextButton(text.toUpperCase(), style);
    button.setWidth(width * uiScale);
    button.setHeight(42f * uiScale);
    button.getLabelCell().center();
    return button;
  }

  /**
   * Creates a window with consistent styling and UI scale support.
   *
   * @param title the window title
   * @return a styled Window
   */
  public Window createWindow(String title) {
    Window window = new Window(title, skin);
    window.scaleBy(uiScale, uiScale);
    return window;
  }

  /**
   * Creates a text field with consistent styling and UI scale support.
   *
   * @param placeholder the placeholder text
   * @return a styled TextField
   */
  public TextField createTextField(String placeholder) {
    TextField textField = new TextField(placeholder, skin);
    textField.scaleBy(uiScale, uiScale);
    return textField;
  }

  /**
   * Creates a checkbox with consistent styling and UI scale support.
   *
   * @param text the checkbox text
   * @return a styled CheckBox
   */
  public CheckBox createCheckBox(String text) {
    CheckBox checkBox = new CheckBox(text, skin);
    checkBox.scaleBy(uiScale, uiScale);
    return checkBox;
  }

  /**
   * Creates a slider with consistent styling and UI scale support.
   *
   * @param min the minimum value
   * @param max the maximum value
   * @param step the step value
   * @param vertical the direction of the slider
   * @return a styled Slider
   */
  public Slider createSlider(float min, float max, float step, boolean vertical) {
    Slider slider = new Slider(min, max, step, vertical, skin);
    slider.scaleBy(uiScale, uiScale);
    return slider;
  }

  /**
   * Creates a text tooltip with consistent styling and UI scale support.
   *
   * @param text the tooltip text
   * @return a styled TextTooltip
   */
  public TextTooltip createTextTooltip(String text) {
    return new TextTooltip(text, skin);
  }

  /**
   * Creates a select box with consistent styling and UI scale support.
   *
   * @param items the items to display in the select box
   * @return a styled SelectBox
   */
  public <T> SelectBox<T> createSelectBox(T[] items) {
    SelectBox<T> selectBox = new SelectBox<>(skin);
    selectBox.scaleBy(uiScale, uiScale);
    return selectBox;
  }

  /** Clears the font cache. Call this when UI scale changes. */
  public void clearFontCache() {
    fontCache.clear();
  }

  /**
   * Gets the scaled width for a given base width.
   * @param baseWidth the base width to scale
   * @return the scaled width
   */
  public float getScaledWidth(float baseWidth) {
    return baseWidth * uiScale;
  }

  /**
   * Gets the scaled height for a given base height.
   * @param baseHeight the base height to scale
   * @return the scaled height
   */
  public float getScaledHeight(float baseHeight) {
    return baseHeight * uiScale;
  }
}
