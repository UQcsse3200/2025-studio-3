package com.csse3200.game.components.achievements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.GdxGame;
import com.csse3200.game.GdxGame.ScreenType;
import com.csse3200.game.entities.configs.BaseAchievementConfig;
import com.csse3200.game.progression.statistics.Statistics;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The AchievementsDisplay class is a UI component that renders a table of player achievements on
 * screen.
 */
public class AchievementsDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(AchievementsDisplay.class);
  private final GdxGame game;
  private Table rootTable;
  private ImageButton closeButton;

  /**
   * Creates an AchievementsDisplay for the game instance.
   *
   * @param game current game instance
   */
  public AchievementsDisplay(GdxGame game) {
    super();
    this.game = game;
  }

  @Override
  public void create() {
    super.create();
    addActors();
  }

  /** Builds and adds the main UI actors for the Achievements screen. */
  private void addActors() {
    Label title = new Label("Achievements", skin);
    title.setFontScale(2f);
    rootTable = new Table();
    rootTable.setFillParent(true);
    rootTable.top().padTop(20);
    rootTable.add(title).center().row();
    createAchievementDisplay();
    stage.addActor(rootTable);
    createCloseButton();
  }

  /** Creates the achievement display with achievements organized by tier. */
  private void createAchievementDisplay() {
    ConfigService configService = ServiceLocator.getConfigService();
    if (configService == null) {
      Label errorLabel = new Label("Config service not available", skin);
      errorLabel.setColor(Color.RED);
      rootTable.row().padTop(20);
      rootTable.add(errorLabel).center().row();
      return;
    }

    Statistics statistics = ServiceLocator.getProfileService().getProfile().getStatistics();
    Map<String, BaseAchievementConfig> achievementConfigs = configService.getAchievementConfigs();

    if (achievementConfigs == null || achievementConfigs.isEmpty()) {
      Label errorLabel = new Label("No achievements configured", skin);
      errorLabel.setColor(Color.RED);
      rootTable.row().padTop(20);
      rootTable.add(errorLabel).center().row();
      return;
    }

    Table[] tables = populateTable(achievementConfigs, statistics);
    Table t1Table = tables[0];
    Table t2Table = tables[1];
    Table t3Table = tables[2];

    // Stack tables vertically
    Table allTables = new Table();

    allTables.add(new Label("Tier 1", skin)).center().row();
    allTables.add(t1Table).left().padBottom(20).row();

    allTables.add(new Label("Tier 2", skin)).center().row();
    allTables.add(t2Table).left().padBottom(20).row();

    allTables.add(new Label("Tier 3", skin)).center().row();
    allTables.add(t3Table).left().padBottom(20).row();

    ScrollPane scrollPane = new ScrollPane(allTables, skin);
    scrollPane.setFadeScrollBars(false);

    rootTable.row().padTop(20);
    rootTable.add(scrollPane).expand().fill().row();
  }

  /**
   * Populates the table with achievements organized by tier.
   *
   * @param achievementConfigs the achievement configs
   * @param statistics the statistics
   * @return the tables
   */
  private Table[] populateTable(
      Map<String, BaseAchievementConfig> achievementConfigs, Statistics statistics) {
    // Separate achievements by tier
    Table t1Table = new Table();
    Table t2Table = new Table();
    Table t3Table = new Table();

    int colCountT1 = 0;
    int colCountT2 = 0;
    int colCountT3 = 0;

    for (Map.Entry<String, BaseAchievementConfig> entry : achievementConfigs.entrySet()) {
      String achievementKey = entry.getKey();
      BaseAchievementConfig config = entry.getValue();
      boolean isUnlocked = statistics.isAchievementUnlocked(achievementKey);

      ImageButton achButton =
          createAchievementButton(
              config, isUnlocked, statistics.getStatistic(config.getStatistic()));

      // Place in correct tier table based on tier string
      String tier = config.getTier();
      if ("T1".equals(tier)) {
        t1Table.add(achButton).pad(5).size(200f, 72f);
        colCountT1++;
        if (colCountT1 % 6 == 0) t1Table.row();
      } else if ("T2".equals(tier)) {
        t2Table.add(achButton).pad(5).size(200f, 72f);
        colCountT2++;
        if (colCountT2 % 6 == 0) t2Table.row();
      } else if ("T3".equals(tier)) {
        t3Table.add(achButton).pad(5).size(200f, 72f);
        colCountT3++;
        if (colCountT3 % 6 == 0) t3Table.row();
      }
    }
    return new Table[] {t1Table, t2Table, t3Table};
  }

  /**
   * Creates a button for displaying an achievement.
   *
   * @param config the achievement config to create a button for
   * @param isUnlocked whether the achievement is unlocked
   * @param currentProgress current progress towards the achievement
   * @return the created button
   */
  private ImageButton createAchievementButton(
      BaseAchievementConfig config, boolean isUnlocked, int currentProgress) {
    ImageButton achButton =
        new ImageButton(
            new TextureRegionDrawable(
                ServiceLocator.getGlobalResourceService()
                    .getAsset("images/achievement.png", Texture.class)));

    achButton.setSize(200f, 72f);
    Label nameLabel = new Label(config.getName(), skin);
    Label.LabelStyle st = new Label.LabelStyle(nameLabel.getStyle());
    if (isUnlocked) {
      st.fontColor = Color.GREEN;
    } else {
      st.fontColor = Color.RED;
    }
    nameLabel.setStyle(st);
    nameLabel.setFontScale(0.7f);
    nameLabel.setAlignment(com.badlogic.gdx.utils.Align.center);
    Table centerTable = new Table();
    centerTable.setSize(200f, 72f);
    centerTable.center();
    centerTable.add(nameLabel).center().expand().fill();
    achButton.addActor(centerTable);

    achButton.addListener(
        new ClickListener() {
          @Override
          public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
            showAchievementDialog(config, isUnlocked, currentProgress);
          }
        });

    return achButton;
  }

  /**
   * Shows an info dialog with achievement details using DialogService.
   *
   * @param config the achievement config to show details for
   * @param isUnlocked whether the achievement is unlocked
   * @param currentProgress current progress towards the achievement
   */
  private void showAchievementDialog(
      BaseAchievementConfig config, boolean isUnlocked, int currentProgress) {
    String title = config.getName();

    // Build the message content
    StringBuilder messageBuilder = new StringBuilder();
    messageBuilder
        .append(config.getDescription())
        .append("\n\n")
        .append("Progress: ")
        .append(currentProgress)
        .append("/")
        .append(config.getQuota())
        .append("\n");
    messageBuilder.append("Status: ").append(isUnlocked ? "Unlocked" : "Locked");

    String message = messageBuilder.toString();

    // Use DialogService to create an info dialog
    ServiceLocator.getDialogService().info(title, message);
  }

  /** Creates the close button in the top-left corner. */
  private void createCloseButton() {
    // Create close button using close-icon.png
    closeButton =
        new ImageButton(
            new TextureRegionDrawable(
                ServiceLocator.getGlobalResourceService()
                    .getAsset("images/ui/close-icon.png", Texture.class)));

    closeButton.setSize(60f, 60f);
    updateCloseButtonPosition();

    // Add listener for the close button
    closeButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("[AchievementsDisplay] Close button clicked");
            backMenu();
          }
        });

    stage.addActor(closeButton);
  }

  /** Updates the close button position based on current stage dimensions. */
  private void updateCloseButtonPosition() {
    if (closeButton != null && stage != null) {
      closeButton.setPosition(20f, stage.getHeight() - 60f - 20f);
    }
  }

  /** Public method to update close button position on resize. */
  public void updateOnResize() {
    updateCloseButtonPosition();
  }

  /** Handles navigation back to the World Map. */
  private void backMenu() {
    game.setScreen(ScreenType.WORLD_MAP);
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  /** Disposes of this UI component. */
  @Override
  public void dispose() {
    rootTable.clear();
    super.dispose();
  }
}
