package com.csse3200.game.components.persistence;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import com.csse3200.game.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Handles actions in the new game menu. */
public class NewGameMenuActions extends Component {
  private static final Logger logger = LoggerFactory.getLogger(NewGameMenuActions.class);
  private GdxGame game;
  private int selectedSlot = -1;

  public NewGameMenuActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("back", this::handleBack);
    entity.getEvents().addListener("selectSlot", this::handleSelectSlot);
    entity.getEvents().addListener("startGame", this::handleStartGame);
  }

  /** Handle going back to the main menu. */
  private void handleBack() {
    game.setScreen(GdxGame.ScreenType.MAIN_MENU);
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
   * Handle starting a new game with the given save name.
   *
   * @param saveName the name for the new save file
   */
  private void handleStartGame(String saveName) {
    logger.info("Starting new game with save name: {} in slot: {}", saveName, selectedSlot);
    Persistence.create(saveName, selectedSlot);
    game.setScreen(GdxGame.ScreenType.MAIN_GAME);
  }
}
