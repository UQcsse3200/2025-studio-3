package com.csse3200.game.components.hud;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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

    menuButtons = new TextButton[buttonTexts.length];

    for (int i = 0; i < buttonTexts.length; i++) {
      TextButton button = new TextButton(buttonTexts[i], skin);
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
    updatePositions();
  }

  /** Updates positions when window is resized */
  private void updatePositions() {
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
    super.dispose();
  }
}
