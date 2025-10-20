package com.csse3200.game.components.statistics;

import com.badlogic.gdx.graphics.Texture;
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
  private Table rootTable;
  float uiScale = ui.getUIScale();

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
    // Create background image
    Texture backgroundTexture = ServiceLocator.getGlobalResourceService()
        .getAsset("images/ui/menu_card.png", Texture.class);
    Image backgroundImage = new Image(backgroundTexture);
    backgroundImage.setSize(870f * uiScale, 610f * uiScale);
    
    // Create content table for statistics
    rootTable = new Table();
    rootTable.setSize(870f * uiScale, 610f * uiScale);
    rootTable.center();
    
    // Add title with 10f padding from top
    Label title = ui.title("Statistics");
    rootTable.add(title)
        .expandX()
        .center()
        .padTop(25f * uiScale)
        .row();
    
    createStatisticsDisplay();
    
    // Create stack with background and content
    Stack stack = new Stack();
    stack.add(backgroundImage);
    stack.add(rootTable);
    stack.setSize(1044f * uiScale, 732f * uiScale);
    stack.setPosition((stage.getWidth() - stack.getWidth()) / 2, (stage.getHeight() - stack.getHeight()) / 2);
    
    stage.addActor(stack);
    createCloseButton();
  }

  /** Creates the statistics display with a two-column layout. */
  private void createStatisticsDisplay() {
    Table statisticsTable = makeStatisticsTable();
    
    ScrollPane scrollPane = new ScrollPane(statisticsTable, skin);
    scrollPane.setFadeScrollBars(false);

    rootTable.row();
    rootTable.add(scrollPane).expand().fill().row();
  }

  /**
   * Builds a table displaying the player's Statistics in a two-column layout
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

    // Create main table for two-column layout
    Table mainTable = new Table();
    
    // Create left column table
    Table leftColumn = new Table();
    // Create right column table  
    Table rightColumn = new Table();

    // Split statistics into two columns (7 per column)
    for (int i = 0; i < stats.length; i++) {
      Object[] entry = stats[i];
      Label label = ui.subheading(entry[0].toString());
      Label stat = ui.subheading(entry[1].toString());
      
      Table statRow = new Table();
      statRow.add(label).right().padRight(20f * uiScale);
      statRow.add(stat).left();
      
      if (i < 7) {
        // First 7 statistics go in left column
        leftColumn.add(statRow).left().padBottom(15f * uiScale).row();
      } else {
        // Remaining statistics go in right column
        rightColumn.add(statRow).left().padBottom(15f * uiScale).row();
      }
    }
    
    // Add columns to main table
    mainTable.add(leftColumn).expand().fill().padRight(20f * uiScale);
    mainTable.add(rightColumn).expand().fill();
    
    return mainTable;
  }

  /** Creates the close button in the top-left corner. */
  private void createCloseButton() {
    // Create close button using createBackExitButton
    TextButton closeButton = ui.createBackExitButton(entity.getEvents(), stage.getHeight(), "Back");
    entity.getEvents().addListener("back", () -> {
      logger.debug("Back button clicked");
      game.setScreen(GdxGame.ScreenType.WORLD_MAP);
    });
    stage.addActor(closeButton);
  }

  /** Disposes of this UI component. */
  @Override
  public void dispose() {
    if (rootTable != null) {
      rootTable.clear();
    }
    super.dispose();
  }
}
