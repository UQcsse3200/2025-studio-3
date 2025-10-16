package com.csse3200.game.components.statistics;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.csse3200.game.GdxGame;
import com.csse3200.game.progression.statistics.Statistics;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The StatisticsDisplay class is a UI component that renders a table of player Statistics on
 * screen.
 */
public class StatisticsDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(StatisticsDisplay.class);
  private final GdxGame game;

  /**
   * Creates a StatisticsDisplay for the game instance.
   *
   * @param game current game instance
   */
  public StatisticsDisplay(GdxGame game) {
    super();
    this.game = game;
  }

  @Override
  public void create() {
    super.create();
    addActors();
  }

  /** Builds and adds the main UI actors for the Statistics screen. */
  private void addActors() {
    Label titleLabel = ui.title("Statistics");
    titleLabel.setSize(titleLabel.getPrefWidth(), titleLabel.getPrefHeight());

    float pad = ui.getScaledHeight(24f);
    titleLabel.setPosition(
        stage.getViewport().getWorldWidth() / 2f - titleLabel.getWidth() / 2f,
        stage.getViewport().getWorldHeight() - titleLabel.getHeight() - pad);
    titleLabel.setZIndex(3);
    stage.addActor(titleLabel);
    titleLabel.toFront();

    Table statisticsTable = makeStatisticsTable();
    statisticsTable.center();
    statisticsTable.top();
    statisticsTable.setPosition(
        stage.getViewport().getWorldWidth() / 2f - statisticsTable.getPrefWidth() / 2f,
        stage.getViewport().getWorldHeight() - titleLabel.getHeight() - pad * 3);

    stage.addActor(statisticsTable);
    statisticsTable.toFront();
    createCloseButton();
  }

  /**
   * Builds a table displaying the player's Statistics
   *
   * @return a table containing formatted Statistics
   */
  private Table makeStatisticsTable() {
    // Get current values
    Statistics statistics = ServiceLocator.getProfileService().getProfile().getStatistics();

    Object[][] stats = {
      {"Total Kills:", statistics.getStatistic("enemiesKilled")},
      {"Shots Fired:", statistics.getStatistic("shotsFired")},
      {"Levels Passed:", statistics.getStatistic("levelsCompleted")},
      {"Levels Lost:", statistics.getStatistic("levelsLost")},
      {"Defences Planted:", statistics.getStatistic("defencesPlanted")},
      {"Defences Unlocked:", statistics.getStatistic("defencesUnlocked")},
      {"Defences Lost:", statistics.getStatistic("defencesLost")},
      {"Total Coins Earned:", statistics.getStatistic("coinsCollected")},
      {"Total Coins Spent:", statistics.getStatistic("coinsSpent")},
      {"Skill Points Collected:", statistics.getStatistic("skillPointsCollected")},
      {"Skill Points Spent:", statistics.getStatistic("skillPointsSpent")},
      {"Purchases Made:", statistics.getStatistic("purchasesMade")},
      {"Waves Completed:", statistics.getStatistic("wavesCompleted")},
      {"Items Collected:", statistics.getStatistic("itemsCollected")},
    };

    // Position Components on table
    Table table = new Table();

    for (Object[] entry : stats) {
      Label label = ui.text(entry[0].toString());
      Label stat = ui.text(entry[1].toString());
      table.row().padTop(10f);
      table.add(label).right().padRight(30f);
      table.add(stat).left();
    }
    return table;
  }

  /** Creates the close button in the top-left corner. */
  private void createCloseButton() {
    entity
        .getEvents()
        .addListener(
            "back",
            () -> {
              logger.debug("Back button clicked");
              game.setScreen(GdxGame.ScreenType.WORLD_MAP);
            });

    // Create UIFactory back button
    TextButton backButton = ui.createBackButton(entity.getEvents(), stage.getHeight());
    stage.addActor(backButton);
  }
}
