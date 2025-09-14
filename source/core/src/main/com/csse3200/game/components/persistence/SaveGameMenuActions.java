package com.csse3200.game.components.persistence;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import com.csse3200.game.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Handles actions in the save game menu. */
public class SaveGameMenuActions extends Component {
  private static final Logger logger = LoggerFactory.getLogger(SaveGameMenuActions.class);
  private GdxGame game;
  private int selectedSlot = -1;

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

    // Save the current profile to the selected slot
    Persistence.save(selectedSlot);

    // Update the profile name if it's different
    if (Persistence.profile() != null && !Persistence.profile().getName().equals(saveName)) {
      Persistence.profile().setName(saveName);
      Persistence.save(selectedSlot); // Save again with the new name
    }

    // Return to the main game screen
    game.setScreen(GdxGame.ScreenType.MAIN_GAME);
  }
}
