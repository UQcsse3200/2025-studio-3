package com.csse3200.game.components.profile;

import com.csse3200.game.GdxGame;
import com.csse3200.game.components.Component;
import com.csse3200.game.services.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Handles actions for the profile menu display. */
public class ProfileDisplayActions extends Component {
  private static final Logger logger = LoggerFactory.getLogger(ProfileDisplayActions.class);
  private GdxGame game;

  public ProfileDisplayActions(GdxGame game) {
    this.game = game;
  }

  @Override
  public void create() {
    entity.getEvents().addListener("profile_inventory", this::onInventory);
    entity.getEvents().addListener("profile_achievements", this::onAchievements);
    entity.getEvents().addListener("profile_skills", this::onSkills);
    entity.getEvents().addListener("profile_stats", this::onStats);
    entity.getEvents().addListener("profile_back", this::onBack);
    entity.getEvents().addListener("profile_exit", this::onExit);
    entity.getEvents().addListener("profile_save", this::onSave);
    entity.getEvents().addListener("profile_settings", this::onSettings);
    entity.getEvents().addListener("profile_shop", this::onShop);
    entity.getEvents().addListener("profile_dossier", this::onDossier);
  }

  private void onBack() {
    logger.info("Returning to world map");
    game.setScreen(GdxGame.ScreenType.WORLD_MAP);
  }

  private void onInventory() {
    logger.info("Opening inventory");
    game.setScreen(GdxGame.ScreenType.INVENTORY);
  }

  private void onAchievements() {
    logger.info("Opening achievements");
    game.setScreen(GdxGame.ScreenType.ACHIEVEMENTS);
  }

  private void onSkills() {
    logger.info("Opening skills");
    game.setScreen(GdxGame.ScreenType.SKILLTREE);
  }

  private void onStats() {
    logger.info("Opening stats");
    game.setScreen(GdxGame.ScreenType.STATISTICS);
  }

  private void onShop() {
    logger.info("Opening shop");
    game.setScreen(GdxGame.ScreenType.SHOP);
  }

  private void onExit() {
    logger.info("Exiting game");
    game.setScreen(GdxGame.ScreenType.MAIN_MENU);
  }

  private void onSave() {
    logger.info("Saving game");
    ServiceLocator.getProfileService().saveCurrentProfile();
  }

  private void onSettings() {
    logger.info("Opening settings");
    game.setScreen(GdxGame.ScreenType.SETTINGS);
  }

  private void onDossier() {
    logger.info("Opening dossier");
    game.setScreen(GdxGame.ScreenType.DOSSIER);
  }
}
