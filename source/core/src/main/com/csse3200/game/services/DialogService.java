package com.csse3200.game.services;

import com.csse3200.game.components.dialog.AchievementDialogComponent;
import com.csse3200.game.components.dialog.DialogComponent;
import com.csse3200.game.entities.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for managing dialog components throughout the game. Provides convenient methods for
 * creating and displaying different types of dialogs.
 */
public class DialogService {
  private static final Logger logger = LoggerFactory.getLogger(DialogService.class);
  private final List<DialogComponent> activeDialogs = new ArrayList<>();
  private final List<AchievementDialogComponent> activeAchievementDialogs = new ArrayList<>();

  /** Enum for the different types of dialogs. */
  public enum DialogType {
    /** Info dialog type, typically used for informational messages with a single "OK" button. */
    INFO,
    /**
     * Warning dialog type, typically used for warning messages with "Confirm" and "Cancel" buttons.
     */
    WARNING,
    /** Error dialog type, typically used for error messages with a single "OK" button. */
    ERROR
  }

  /** Creates a new dialog service. */
  public DialogService() {
    logger.debug("Dialog service created");
  }

  /**
   * Creates and shows an info dialog.
   *
   * @param title the dialog title
   * @param message the dialog message
   * @return the created dialog component
   */
  public DialogComponent info(String title, String message) {
    return info(title, message, null);
  }

  /**
   * Creates and shows an info dialog with a close callback.
   *
   * @param title the dialog title
   * @param message the dialog message
   * @param onClose callback when dialog is closed
   * @return the created dialog component
   */
  public DialogComponent info(String title, String message, Consumer<DialogComponent> onClose) {
    return createAndShowDialog(DialogType.INFO, title, message, null, null, onClose);
  }

  /**
   * Creates and shows a warning dialog.
   *
   * @param title the dialog title
   * @param message the dialog message
   * @return the created dialog component
   */
  public DialogComponent warning(String title, String message) {
    return warning(title, message, null, null);
  }

  /**
   * Creates and shows a warning dialog with callbacks.
   *
   * @param title the dialog title
   * @param message the dialog message
   * @param onConfirm callback when user confirms/continues
   * @param onCancel callback when user cancels
   * @return the created dialog component
   */
  public DialogComponent warning(
      String title,
      String message,
      Consumer<DialogComponent> onConfirm,
      Consumer<DialogComponent> onCancel) {
    return createAndShowDialog(DialogType.WARNING, title, message, onConfirm, onCancel, null);
  }

  /**
   * Creates and shows an error dialog.
   *
   * @param title the dialog title
   * @param message the dialog message
   * @return the created dialog component
   */
  public DialogComponent error(String title, String message) {
    return error(title, message, null);
  }

  /**
   * Creates and shows an error dialog with a close callback.
   *
   * @param title the dialog title
   * @param message the dialog message
   * @param onClose callback when dialog is closed
   * @return the created dialog component
   */
  public DialogComponent error(String title, String message, Consumer<DialogComponent> onClose) {
    return createAndShowDialog(DialogType.ERROR, title, message, null, null, onClose);
  }

  /** Hides all active dialogs. */
  public void hideAllDialogs() {
    logger.debug("Hiding all active dialogs");
    for (DialogComponent dialog : activeDialogs) {
      dialog.hide();
    }
    activeDialogs.clear();
    for (AchievementDialogComponent dialog : activeAchievementDialogs) {
      dialog.hide();
    }
    activeAchievementDialogs.clear();
  }

  /**
   * Gets the number of currently active dialogs.
   *
   * @return the number of active dialogs
   */
  public int getActiveDialogCount() {
    return activeDialogs.size();
  }

  /**
   * Checks if there are any active dialogs.
   *
   * @return true if there are active dialogs, false otherwise
   */
  public boolean hasActiveDialogs() {
    return !activeDialogs.isEmpty();
  }

  /**
   * Gets a copy of the list of active dialogs.
   *
   * @return list of active dialog components
   */
  public List<DialogComponent> getActiveDialogs() {
    return new ArrayList<>(activeDialogs);
  }

  /** Handles window resize by re-centering all active dialogs. */
  public void resize() {
    logger.debug("Resizing {} active dialogs", activeDialogs.size());
    for (DialogComponent dialog : activeDialogs) {
      dialog.resize();
    }
    for (AchievementDialogComponent dialog : activeAchievementDialogs) {
      dialog.resize();
    }
  }

  /**
   * Internal method to create and show a dialog with all options.
   *
   * @param dialogType the type of dialog
   * @param title the title of the dialog
   * @param message the message of the dialog
   * @param onConfirm the callback for the confirm button
   * @param onCancel the callback for the cancel button
   * @param onClose the callback for the close button
   * @return the created dialog component
   */
  private DialogComponent createAndShowDialog(
      DialogType dialogType,
      String title,
      String message,
      Consumer<DialogComponent> onConfirm,
      Consumer<DialogComponent> onCancel,
      Consumer<DialogComponent> onClose) {
    logger.debug("Creating {} dialog: {}", dialogType, title);

    // Create entity and add dialog component
    Entity dialogEntity = new Entity();
    DialogComponent dialog = new DialogComponent(dialogType, title, message);

    // Set up callbacks
    if (onConfirm != null) {
      dialog.setOnConfirm(onConfirm);
    }
    if (onCancel != null) {
      dialog.setOnCancel(onCancel);
    }
    if (onClose != null) {
      dialog.setOnClose(onClose);
    }

    // Add cleanup callback to remove from active list
    dialog.setOnClose(
        d -> {
          activeDialogs.remove(dialog);
          dialogEntity.dispose();
        });

    dialogEntity.addComponent(dialog);

    // Register entity with service locator
    ServiceLocator.getEntityService().register(dialogEntity);

    // Show the dialog
    dialog.show();

    // Add to active dialogs list
    activeDialogs.add(dialog);

    return dialog;
  }

  /**
   * Creates and shows an achievement dialog.
   *
   * @param name the name of the achievement
   * @param description the description of the achievement
   * @param skillPoints the skill points of the achievement
   * @param tier the tier of the achievement
   * @return the created achievement dialog component
   */
  public AchievementDialogComponent achievement(
      String name, String description, int skillPoints, String tier) {
    AchievementDialogComponent dialogComponent =
        new AchievementDialogComponent(name, description, skillPoints, tier != null ? tier : "T1");
    Entity dialogEntity = new Entity();
    dialogEntity.addComponent(dialogComponent);
    ServiceLocator.getEntityService().register(dialogEntity);
    dialogComponent.show();
    dialogComponent.setOnCompletion(
        d -> {
          activeAchievementDialogs.remove(dialogComponent);
          dialogEntity.dispose();
        });

    activeAchievementDialogs.add(dialogComponent);
    return dialogComponent;
  }
}
