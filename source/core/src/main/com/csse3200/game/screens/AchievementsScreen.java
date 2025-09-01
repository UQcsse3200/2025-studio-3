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
import com.csse3200.game.Achievements.Achievement;
import com.csse3200.game.Achievements.AchievementManager;
import com.csse3200.game.services.ServiceLocator;

/**
 * The game screen containing the achievements menu.
 */
public class AchievementsScreen extends ScreenAdapter {
    private final GdxGame game;
    private Stage stage;
    private Table rootTable;

    private AchievementManager achievementManager;

    // Styles
    private Skin skin;
    private Label.LabelStyle headerStyle;
    private Label.LabelStyle textStyle;
    private TextButton.TextButtonStyle buttonStyle;
    private Window.WindowStyle windowStyle;

    public AchievementsScreen(GdxGame game) {
        this.game = game;
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

        textStyle = skin.get(Label.LabelStyle.class); // default
        buttonStyle = skin.get(TextButton.TextButtonStyle.class); // default
        windowStyle = skin.get(Window.WindowStyle.class); // default

        // === Root table ===
        rootTable = new Table();
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // === Header ===
        Label header = new Label("Achievements", headerStyle);
        header.setFontScale(2f);

        rootTable.top().padTop(20);
        rootTable.add(header).center().row();

        // === Achievements list ===
        achievementManager = ServiceLocator.getAchievementManager();
        if (achievementManager == null) {
            achievementManager = new AchievementManager();
            ServiceLocator.registerAchievementManager(achievementManager);
        }

        Table achievementTable = new Table();
        int colCount = 0;
        for (Achievement a : achievementManager.getAllAchievements()) {
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

            achievementTable.add(achButton).pad(5).width(150).height(60);


            colCount++;
            if (colCount % 7 == 0) {
                // new row after every 7 buttons
                achievementTable.row();
            }

        }


        ScrollPane scrollPane = new ScrollPane(achievementTable, skin);
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
                game.setScreen(GdxGame.ScreenType.MAIN_MENU);
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
        // Change color depending on locked/unlocked
        if (a.isUnlocked()) {
            name.setColor(Color.GREEN);
        } else {
            name.setColor(Color.RED);
        }
        Label description = new Label(a.getDescription(), textStyle);
        Label points = new Label("Points: " + a.getSkillPoint(), textStyle);

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
        Gdx.gl.glClearColor(248f/255f, 249f/255f, 178/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();

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
