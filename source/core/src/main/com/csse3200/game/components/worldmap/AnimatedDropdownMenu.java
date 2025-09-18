package com.csse3200.game.components.worldmap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnimatedDropdownMenu extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(AnimatedDropdownMenu.class);
  private static final float Z_INDEX = 3f;
  private static final int BUTTON_WIDTH = 200;
  private static final int BUTTON_HEIGHT = 40;
  private static final int BUTTON_SPACING = 5;
  private TextButton[] menuButtons;
  private boolean isOpen = false;
  private float startX;
  private float startY;
  private BitmapFont customFont;

  @Override
  public void create() {
    super.create();
    addActors();
    entity.getEvents().addListener("open_dropdown_menu", this::toggle);
  }

  /** Creates a new AnimatedDropdownMenu. */
  public AnimatedDropdownMenu() {
    super();
  }

  /** Adds the actors to the stage */
  private void addActors() {
    // Check if stage is available, if not, defer initialization
    if (stage == null) {
      return;
    }

    // Position in top right corner
    startX = stage.getWidth() - 80f;
    startY = stage.getHeight() - 80f;
    createMenuButtons();
  }

  /** Creates the dropdown menu buttons */
  private void createMenuButtons() {
    String[] buttonTexts = {
      "Quicksave",
      "Save Game",
      "Load Game",
      "Statistics",
      "Achievements",
      "Dossier",
      "Inventory",
      "Main Menu",
      "Exit Game"
    };

    // Get the custom font from GlobalResourceService
    createCustomFont();

    menuButtons = new TextButton[buttonTexts.length];

    for (int i = 0; i < buttonTexts.length; i++) {
      TextButton button = createCustomButton(buttonTexts[i]);
      button.setSize(BUTTON_WIDTH, BUTTON_HEIGHT);

      // Position buttons below trigger button, initially hidden
      float buttonX = startX - BUTTON_WIDTH + 60f;
      float buttonY = startY - (i + 1) * (BUTTON_HEIGHT + BUTTON_SPACING);
      button.setPosition(buttonX, buttonY);
      button.setVisible(false);

      // Add click listener
      final String buttonText = buttonTexts[i];
      button.addListener(
          new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
              handleMenuClick(buttonText);
            }
          });

      menuButtons[i] = button;
      stage.addActor(button);
    }
  }

  /** Creates the custom font from the GlobalResourceService */
  private void createCustomFont() {
    try {
      // Generate the font with appropriate size for the dropdown menu
      customFont = ServiceLocator.getGlobalResourceService().generateFreeTypeFont("Default", 18);
      if (customFont == null) {
        logger.warn("Failed to load custom font, falling back to default");
        customFont = new BitmapFont(); // Fallback to default font
      }
    } catch (Exception e) {
      logger.error("Error creating custom font", e);
      customFont = new BitmapFont(); // Fallback to default font
    }
  }

  /** Creates a custom TextButton with the loaded FreeType font */
  private TextButton createCustomButton(String text) {
    // Start with the default skin style and modify only the font
    TextButton.TextButtonStyle buttonStyle;

    if (skin != null) {
      try {
        // Copy the existing TextButton style from the skin
        buttonStyle = new TextButton.TextButtonStyle(skin.get(TextButton.TextButtonStyle.class));
        logger.debug("Using skin's default TextButton style as base");
      } catch (Exception e) {
        logger.debug("Default TextButton style not found in skin, creating new style");
        buttonStyle = new TextButton.TextButtonStyle();
      }
    } else {
      buttonStyle = new TextButton.TextButtonStyle();
    }

    // Override only the font to use our custom FreeType font
    buttonStyle.font = customFont;
    // Keep the original colors but ensure text is visible
    if (buttonStyle.fontColor == null) {
      buttonStyle.fontColor = Color.WHITE;
    }
    if (buttonStyle.downFontColor == null) {
      buttonStyle.downFontColor = Color.LIGHT_GRAY;
    }
    if (buttonStyle.overFontColor == null) {
      buttonStyle.overFontColor = Color.YELLOW;
    }

    return new TextButton(text, buttonStyle);
  }

  /** Toggles the dropdown menu */
  private void toggleMenu() {
    if (isOpen) {
      closeMenu();
    } else {
      openMenu();
    }
  }

  /** Public method to toggle the menu from outside */
  public void toggle() {
    toggleMenu();
  }

  /** Opens the dropdown menu with staggered animation */
  private void openMenu() {
    isOpen = true;

    for (int i = 0; i < menuButtons.length; i++) {
      TextButton button = menuButtons[i];

      // Set initial position (moved up)
      float finalY = startY - (i + 1) * (BUTTON_HEIGHT + BUTTON_SPACING);
      button.setPosition(button.getX(), finalY + 50f);
      button.setVisible(true);
      button.getColor().a = 0f;

      // Animate to final position with delay (gap of 0.1s between buttons)
      float delay = 0.1f * (i + 1);
      button.addAction(
          Actions.sequence(
              Actions.delay(delay),
              Actions.parallel(
                  Actions.moveTo(button.getX(), finalY, 0.3f), Actions.alpha(1f, 0.3f))));
    }
  }

  /** Closes the dropdown menu */
  private void closeMenu() {
    isOpen = false;
    for (int i = 0; i < menuButtons.length; i++) {
      TextButton button = menuButtons[i];
      button.addAction(
          Actions.sequence(
              Actions.parallel(Actions.moveBy(0, 50f, 0.2f), Actions.alpha(0f, 0.2f)),
              Actions.visible(false)));
    }
  }

  /**
   * Handles menu button clicks
   *
   * @param buttonText The text of the button that was clicked.
   */
  private void handleMenuClick(String buttonText) {
    logger.info("Menu clicked: {}", buttonText);
    closeMenu();
    switch (buttonText) {
      case "Quicksave":
        logger.debug("Quicksave button clicked");
        entity.getEvents().trigger("quicksave");
        break;
      case "Save Game":
        logger.debug("Save game button clicked");
        entity.getEvents().trigger("savegame");
        break;
      case "Load Game":
        logger.debug("Load game button clicked");
        entity.getEvents().trigger("loadgame");
        break;
      case "Statistics":
        logger.debug("Statistics button clicked");
        entity.getEvents().trigger("open_statistics");
        break;
      case "Achievements":
        logger.debug("Achievements button clicked");
        entity.getEvents().trigger("open_achievements");
        break;
      case "Dossier":
        logger.debug("Dossier button clicked");
        entity.getEvents().trigger("open_dossier");
        break;
      case "Inventory":
        logger.debug("Inventory button clicked");
        entity.getEvents().trigger("open_inventory");
        break;
      case "Main Menu":
        logger.debug("Main menu button clicked");
        entity.getEvents().trigger("main_menu");
        break;
      case "Exit Game":
        logger.debug("Exit game button clicked");
        entity.getEvents().trigger("exit");
        break;
      default:
        break;
    }
  }

  @Override
  public void update() {
    super.update();
    // Initialize actors if stage is now available
    if (stage != null && menuButtons == null) {
      addActors();
    }

    updatePositions();
  }

  /** Updates positions when window is resized */
  private void updatePositions() {
    // Check if stage is available
    if (stage == null) {
      return;
    }

    // Update start positions based on current stage size
    startX = stage.getWidth() - 80f;
    startY = stage.getHeight() - 80f;
    // Update menu button positions
    if (menuButtons != null) {
      for (int i = 0; i < menuButtons.length; i++) {
        TextButton button = menuButtons[i];
        if (button != null) {
          float buttonX = startX - BUTTON_WIDTH + 60f;
          float buttonY = startY - (i + 1) * (BUTTON_HEIGHT + BUTTON_SPACING);
          button.setPosition(buttonX, buttonY);
        }
      }
    }
  }

  @Override
  public void draw(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
    // Draw is handled by the stage
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void dispose() {
    if (menuButtons != null) {
      for (TextButton button : menuButtons) {
        if (button != null) {
          button.remove();
        }
      }
      menuButtons = null;
    }

    // Note: Don't dispose customFont here as it's managed by GlobalResourceService
    customFont = null;

    super.dispose();
  }
}
