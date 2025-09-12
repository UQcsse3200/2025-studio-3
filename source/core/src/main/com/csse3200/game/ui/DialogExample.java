package com.csse3200.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.csse3200.game.services.DialogService.DialogType;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.csse3200.game.services.DialogService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Example class demonstrating how to use the DialogComponent and DialogManager.
 * This class can be used as a reference for implementing dialogs in the game.
 */
public class DialogExample extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(DialogExample.class);
  
  @Override
  public void create() {
    super.create();
    
    // Register DialogService if not already registered
    if (ServiceLocator.getDialogService() == null) {
      ServiceLocator.registerDialogService(new DialogService());
    }
    
    logger.info("DialogExample created. Press keys 1-5 to test different dialog types.");
  }
  
  @Override
  public void update() {
    super.update();
    
    // Example key bindings for testing different dialog types
    if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
      showInfoDialogExample();
    } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
      showWarningDialogExample();
    } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
      showErrorDialogExample();
    } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
      showConfirmationDialogExample();
    } else if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
      showCustomPositionDialogExample();
    }
  }
  
  /**
   * Example: Simple info dialog
   */
  private void showInfoDialogExample() {
    ServiceLocator.getDialogService().info(
        "Information", 
        "This is an informational message. It provides neutral information to the user."
    );
  }
  
  /**
   * Example: Warning dialog with callbacks
   */
  private void showWarningDialogExample() {
    ServiceLocator.getDialogService().warning(
        "Warning", 
        "This action may have unintended consequences. Are you sure you want to continue?",
        dialog -> logger.info("User confirmed the warning"),
        dialog -> logger.info("User cancelled the warning")
    );
  }
  
  /**
   * Example: Error dialog
   */
  private void showErrorDialogExample() {
    ServiceLocator.getDialogService().error(
        "Error", 
        "An error has occurred. Please check your input and try again.",
        dialog -> logger.info("User acknowledged the error")
    );
  }
  
  /**
   * Example: Confirmation dialog
   */
  private void showConfirmationDialogExample() {
    ServiceLocator.getDialogService().showConfirmationDialog(
        "Confirm Action", 
        "Are you sure you want to delete this item? This action cannot be undone.",
        dialog -> logger.info("User confirmed deletion"),
        dialog -> logger.info("User cancelled deletion")
    );
  }
  
  /**
   * Example: Dialog at custom position
   */
  private void showCustomPositionDialogExample() {
    ServiceLocator.getDialogService().showDialogAt(
        DialogType.INFO, 
        "Custom Position", 
        "This dialog appears at a custom position on the screen.",
        100f, 200f
    );
  }
  
  @Override
  public void draw(SpriteBatch batch) {
    // No custom drawing needed for this example
  }
}
