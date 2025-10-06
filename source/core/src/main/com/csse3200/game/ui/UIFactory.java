package com.csse3200.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.events.EventHandler;
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.services.ServiceLocator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.dermetfan.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory class for creating UI components with consistent styling.
 *
 * <p>NOTE: This is not a static util class, it is instantiated as a static instance in UIComponent.
 * This allows for the UI scale to be customized and the skin to be passed in to the constructor.
 */
public class UIFactory {
  private static final Logger logger = LoggerFactory.getLogger(UIFactory.class);
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
    return createLabel(text, 44, white);
  }

  /**
   * Creates a subheading label with small text size.
   *
   * @param text the text to display
   * @return a styled Label with subheading styling
   */
  public Label subheading(String text) {
    return createLabel(text, 36, white);
  }

  /**
   * Creates a text label with small text size.
   *
   * @param text the text to display
   * @return a styled Label with text styling
   */
  public Label text(String text) {
    return createLabel(text, 28, white);
  }

  /**
   * Creates a subtext label with small text size.
   *
   * @param text the text to display
   * @return a styled Label with subtext styling
   */
  public Label subtext(String text) {
    return createLabel(text, 24, gray);
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
   * Creates a primary button with consistent styling and UI scale support. Unfortunately, to make
   * the buttons look crisp, we need to create the button style manually.
   *
   * <p>NOTE: This does not work by itself for tables. You will need to call the
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
    // ButtonFactory.textButtonPressedListener(button);
    return button;
  }

  /**
   * Creates a secondary button with consistent styling and UI scale support.Unfortunately, to make
   * the buttons look crisp, we need to create the button style manually.
   *
   * <p>NOTE: This does not work by itself for tables. You will need to call the
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
    //  ButtonFactory.textButtonPressedListener(button);
    return button;
  }

  /**
   * Creates an image button consistent styling and UI scaling.
   *
   * @param imagePath the asset path for the button image
   * @param baseWidth the base width
   * @param baseHeight the base height
   * @return the configured ImageButton (not yet added to stage)
   */
  public ImageButton createImageButton(String imagePath, float baseWidth, float baseHeight) {
    // Load the texture
    Texture texture = ServiceLocator.getGlobalResourceService().getAsset(imagePath, Texture.class);
    ImageButton button = new ImageButton(new TextureRegionDrawable(texture));

    // Apply UI scaling
    float width = getScaledWidth(baseWidth);
    float height = getScaledHeight(baseHeight);

    button.setSize(width, height);
    // ButtonFactory.imageButtonPressedListener(button);
    return button;
  }

  /**
   * Create a standard "Back" or "Exit" button positioned in the top-left corner of the stage, using
   * consistent styling and UI scaling.
   *
   * @param eventHandler the event handler to trigger when the button is clicked
   * @param stageHeight the height of the stage
   * @param backOrExit the relevant text for the button
   * @return the configured and positioned ImageButton (not yet added to stage)
   */
  public TextButton createBackExitButton(
      EventHandler eventHandler, float stageHeight, String backOrExit) {
    TextButton button = secondaryButton(backOrExit, 200f);
    // Scale
    Pair<Float, Float> dimensions = getScaledDimensions(200f);
    button.setSize(dimensions.getKey(), dimensions.getValue());
    // Position
    button.setPosition(
        20f * uiScale, // 20f padding from left
        stageHeight - button.getHeight() - 20f * uiScale);

    // Add listener for the back button
    button.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("{} button clicked", backOrExit);
            eventHandler.trigger(
                backOrExit.toLowerCase(
                    Locale.ROOT)); // Note: must have set up a listener for this event
          }
        });
    return button;
  }

  /**
   * Create a standard "Back" button positioned in the top-left corner of the stage, using
   * consistent styling and UI scaling.
   *
   * @param eventHandler the event handler to trigger when the button is clicked
   * @param stageHeight the height of the stage
   * @return the configured and positioned ImageButton (not yet added to stage)
   */
  public TextButton createBackButton(EventHandler eventHandler, float stageHeight) {
    return createBackExitButton(eventHandler, stageHeight, "Back");
  }

  /**
   * Create a standard "Exit" button positioned in the top-left corner of the stage, using
   * consistent styling and UI scaling.
   *
   * @param eventHandler the event handler to trigger when the button is clicked
   * @param stageHeight the height of the stage
   * @return the configured and positioned ImageButton (not yet added to stage)
   */
  public TextButton createExitButton(EventHandler eventHandler, float stageHeight) {
    return createBackExitButton(eventHandler, stageHeight, "Exit");
  }

  /**
   * Creates a window with consistent styling and UI scale support.
   *
   * @param title the window title
   * @return a styled Window
   */
  public Window createWindow(String title) {
    return new Window(title, skin);
  }

  /**
   * Creates a text field with consistent styling and UI scale support.
   *
   * @param placeholder the placeholder text
   * @return a styled TextField
   */
  public TextField createTextField(String placeholder) {
    TextField.TextFieldStyle style = new TextField.TextFieldStyle();
    style.font = createFont(32);
    style.fontColor = white;
    style.focusedFontColor = white;
    style.cursor = skin.getDrawable("q");
    style.selection = skin.getDrawable("w");
    style.background = skin.getDrawable("d");
    style.focusedBackground = skin.getDrawable("d");

    return new TextField(placeholder, style);
  }

  /**
   * Creates a checkbox with consistent styling and UI scale support.
   *
   * @param text the checkbox text
   * @return a styled CheckBox
   */
  public CheckBox createCheckBox(String text) {
    CheckBox.CheckBoxStyle style = new CheckBox.CheckBoxStyle();
    style.font = createFont(32);
    style.fontColor = white;
    style.checkboxOff = skin.getDrawable("e");
    style.checkboxOn = skin.getDrawable("f");

    return new CheckBox(text, style);
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
    return new Slider(min, max, step, vertical, skin);
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
    SelectBox.SelectBoxStyle style = new SelectBox.SelectBoxStyle();
    style.font = createFont(32);
    style.fontColor = white;
    style.background = skin.getDrawable("n");
    style.scrollStyle = skin.get("list", ScrollPane.ScrollPaneStyle.class);
    style.listStyle = skin.get("default", List.ListStyle.class);

    SelectBox<T> selectBox = new SelectBox<>(style);
    selectBox.setItems(items);
    return selectBox;
  }

  /** Clears the font cache. Call this when UI scale changes. */
  public void clearFontCache() {
    fontCache.clear();
  }

  /**
   * Gets the scaled width for a given base width.
   *
   * @param baseWidth the base width to scale
   * @return the scaled width
   */
  public float getScaledWidth(float baseWidth) {
    return baseWidth * uiScale;
  }

  /**
   * Gets the scaled height for a given base height.
   *
   * @param baseHeight the base height to scale
   * @return the scaled height
   */
  public float getScaledHeight(float baseHeight) {
    return baseHeight * uiScale;
  }
}
