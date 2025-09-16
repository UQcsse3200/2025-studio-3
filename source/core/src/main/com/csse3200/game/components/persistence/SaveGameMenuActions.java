package com.csse3200.game.components.persistence;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import com.csse3200.game.services.ProfileService;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Handles actions in the save game menu. */
public class SaveGameMenuActions extends Component {
  private static final Logger logger = LoggerFactory.getLogger(SaveGameMenuActions.class);
  private GdxGame game;
  private int selectedSlot = -1;

  /**
   * Constructor for the SaveGameMenuActions class.
   *
   * @param game the game instance
   */
  public SaveGameMenuActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("back", this::handleBack);
    entity.getEvents().addListener("selectSlot", this::handleSelectSlot);
    entity.getEvents().addListener("saveGame", this::handleSaveGame);
  }

  /** Handle going back to the previous screen. */
  private void handleBack() {
    // Go back to the main game screen since this is typically accessed from in-game
    game.setScreen(GdxGame.ScreenType.MAIN_GAME);
  }

  /**
   * Handle selecting a save slot.
   *
   * @param slotIndex the index of the selected slot
   */
  private void handleSelectSlot(int slotIndex) {
    selectedSlot = slotIndex + 1;
    logger.debug("Selected save slot: {}", slotIndex);
  }

  /**
   * Handle saving the current game with the given save name.
   *
   * @param saveName the name for the save file
   */
  private void handleSaveGame(String saveName) {
    logger.info("Saving game with save name: {} in slot: {}", saveName, selectedSlot);
    ProfileService profileService = ServiceLocator.getProfileService();
    if (profileService.getProfile() != null
        && !profileService.getProfile().getName().equals(saveName)) {
      profileService.getProfile().setName(saveName);
    }
    profileService.saveProfileToSlot(selectedSlot);
    game.setScreen(GdxGame.ScreenType.MAIN_GAME);
  }
}
