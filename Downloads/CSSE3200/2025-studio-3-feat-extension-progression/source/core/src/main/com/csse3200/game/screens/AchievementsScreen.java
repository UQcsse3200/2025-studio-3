package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.csse3200.game.GdxGame;
import com.csse3200.game.components.achievements.AchievementPopup;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.progression.Profile;
import com.csse3200.game.progression.achievements.Achievement;
import com.csse3200.game.progression.achievements.AchievementManager;

/**
 * The game screen containing the achievements menu.
 */
public class AchievementsScreen extends ScreenAdapter {
  private final GdxGame game;
  private Stage stage;
  private Table rootTable;

  // Styles
  private Skin skin;
  private Label.LabelStyle headerStyle;
  private Label.LabelStyle textStyle;
  private Window.WindowStyle windowStyle;

  public AchievementsScreen(GdxGame game) {
    this.game = game;


    //todo for testing achievement persistence (must import profile and persistence classes)
    Profile profile = Persistence.profile();
    AchievementManager manager = profile.achievements();
    AchievementPopup popup = game.getAchievementPopup();

// Unlock via profile’s manager
    manager.setPopup(popup); // inject popup reference
    manager.unlock("LEVEL_1_COMPLETE");
    manager.addProgress("5_DEFENSES", 1);


  }

  @Override
  public void show() {
    stage = new Stage(new ScreenViewport());
    Gdx.input.setInputProcessor(stage);

    // === Load skin ===
    skin = new Skin(Gdx.files.internal("flat-earth/skin/flat-earth-ui.json"));

    // === Styles from skin ===
    headerStyle = skin.has("title", Label.LabelStyle.class) ?
            skin.get("title", Label.LabelStyle.class) :
            new Label.LabelStyle(new BitmapFont(), Color.DARK_GRAY);

    textStyle = skin.get(Label.LabelStyle.class);
    windowStyle = skin.get(Window.WindowStyle.class);

    // === Root table ===
    rootTable = new Table();
    rootTable.setFillParent(true);
    stage.addActor(rootTable);

    // === Header ===
    Label header = new Label("Achievements", headerStyle);
    header.setFontScale(2f);

    rootTable.top().padTop(20);
    rootTable.add(header).center().row();

    // === Separate Achievements by Tier ===
    Table t1Table = new Table();
    Table t2Table = new Table();
    Table t3Table = new Table();

    int colCountT1 = 0, colCountT2 = 0, colCountT3 = 0;

    for (Achievement a : Persistence.profile().achievements().getAllAchievements()) {
      TextButton achButton = new TextButton(a.getName(), skin);

      if (a.isUnlocked()) {
        achButton.getLabel().setColor(Color.GREEN);
      } else {
        achButton.getLabel().setColor(Color.RED);
      }

      achButton.addListener(new ClickListener() {
        @Override
        public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
          showAchievementPopup(a);
        }
      });

      // Place in correct tier table
      switch (a.getTier()) {
        case T1:
          t1Table.add(achButton).pad(5).width(150).height(60);
          colCountT1++;
          if (colCountT1 % 7 == 0) t1Table.row();
          break;
        case T2:
          t2Table.add(achButton).pad(5).width(150).height(60);
          colCountT2++;
          if (colCountT2 % 7 == 0) t2Table.row();
          break;
        case T3:
          t3Table.add(achButton).pad(5).width(150).height(60);
          colCountT3++;
          if (colCountT3 % 7 == 0) t3Table.row();
          break;
      }
    }

    // === Stack tables vertically ===
    Table allTables = new Table();

    allTables.add(new Label("Tier 1", headerStyle)).center().row();
    allTables.add(t1Table).left().padBottom(20).row();

    allTables.add(new Label("Tier 2", headerStyle)).center().row();
    allTables.add(t2Table).left().padBottom(20).row();

    allTables.add(new Label("Tier 3", headerStyle)).center().row();
    allTables.add(t3Table).left().padBottom(20).row();

    ScrollPane scrollPane = new ScrollPane(allTables, skin);
    scrollPane.setFadeScrollBars(false);

    rootTable.row().padTop(20);
    rootTable.add(scrollPane).expand().fill().row();

    // === Back button ===
    TextButton backButton = new TextButton("Back", skin);
    backButton.getLabel().setFontScale(2f);
    backButton.pad(20, 60, 20, 60);

    backButton.addListener(new ClickListener() {
      @Override
      public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
        game.setScreen(GdxGame.ScreenType.PROFILE);
      }
    });

    rootTable.row().padBottom(40);
    rootTable.add(backButton).center().expandX();
  }

  /**
   * Opens a popup showing achievement details
   */
  private void showAchievementPopup(Achievement a) {
    Window popup = new Window("Achievement Details", windowStyle);

    Label name = new Label(a.getName(), headerStyle);
    if (a.isUnlocked()) {
      name.setColor(Color.GREEN);
    } else {
      name.setColor(Color.RED);
    }

    Label description = new Label(a.getDescription(), textStyle);
    Label points = new Label("Points: " + a.getSkillPoint(), textStyle);
    Label progress = new Label("Progress: " + a.getCurrentProgress() + "/" + a.getGoal(), textStyle);

    name.setFontScale(1.5f);

    TextButton closeBtn = new TextButton("Close", skin);
    closeBtn.addListener(new ClickListener() {
      @Override
      public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
        popup.remove();
      }
    });

    popup.add(name).pad(10).row();
    popup.add(description).pad(10).row();
    popup.add(points).pad(10).row();
    popup.add(progress).pad(10).row();
    popup.add(closeBtn).pad(10).row();

    popup.pack();
    popup.setModal(true);
    popup.setMovable(false);
    popup.setPosition(
            (stage.getWidth() - popup.getWidth()) / 2,
            (stage.getHeight() - popup.getHeight()) / 2
    );

    stage.addActor(popup);
  }

  @Override
  public void render(float delta) {
    Gdx.gl.glClearColor(248f / 255f, 249f / 255f, 178 / 255f, 1);
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

    stage.act(delta);
    stage.draw();

    // ✅ Draw popup stage last, so it overlays
    game.getAchievementPopup().getStage().act(delta);
    game.getAchievementPopup().getStage().draw();

    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      game.setScreen(GdxGame.ScreenType.MAIN_MENU);
    }
  }

  @Override
  public void dispose() {
    stage.dispose();
    skin.dispose();
  }
}
