package com.csse3200.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.csse3200.game.GdxGame;
import com.csse3200.game.Achievements.Achievement;
import com.csse3200.game.Achievements.AchievementManager;
import com.csse3200.game.services.ServiceLocator;

public class AchievementsScreen extends ScreenAdapter {
    private final GdxGame game;
    private Stage stage;
    private Table rootTable;

    private AchievementManager achievementManager;

    // Styles
    private BitmapFont font;
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

        font = new BitmapFont();

        // === Basic colored drawables ===
        TextureRegionDrawable buttonUp = makeColorDrawable(Color.LIGHT_GRAY);
        TextureRegionDrawable buttonDown = makeColorDrawable(Color.DARK_GRAY);
        TextureRegionDrawable windowBg = makeColorDrawable(new Color(0.9f, 0.9f, 0.9f, 1f));

        // === Styles ===
        headerStyle = new Label.LabelStyle(font, Color.DARK_GRAY);
        textStyle = new Label.LabelStyle(font, Color.BLACK);

        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.RED;
        buttonStyle.up = buttonUp;
        buttonStyle.down = buttonDown;

        windowStyle = new Window.WindowStyle(font, Color.BLACK, windowBg);

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
            achievementManager.addAchievement(new Achievement("Debug A", "Fallback achievement A", 5));
            achievementManager.addAchievement(new Achievement("Debug B", "Fallback achievement B", 10));
        }



        Table achievementTable = new Table();
        for (Achievement a : achievementManager.getAllAchievements()) {
            TextButton achButton = new TextButton(a.getName(), buttonStyle);

            if (a.isUnlocked()) {
                achButton.getLabel().setColor(Color.GREEN);  // Label text
                achButton.getStyle().fontColor = Color.GREEN; // Style text color
            } else {
                achButton.getLabel().setColor(Color.RED);
                achButton.getStyle().fontColor = Color.RED;
            }

            achButton.addListener(new ClickListener() {
                @Override
                public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                    showAchievementPopup(a);
                }
            });

            achievementTable.add(achButton).left().pad(5).row();
        }




        ScrollPane scrollPane = new ScrollPane(achievementTable);
        scrollPane.setFadeScrollBars(false);

        rootTable.row().padTop(20);
        rootTable.add(scrollPane).expand().fill().row();

// === Back button ===
// Start from scratch or clone buttonStyle
        TextButton.TextButtonStyle backButtonStyle = new TextButton.TextButtonStyle();
        backButtonStyle.font = font;
        backButtonStyle.fontColor = Color.WHITE;
        backButtonStyle.up = makeColorDrawable(Color.NAVY);   // Default background
        backButtonStyle.down = makeColorDrawable(Color.ROYAL); // Pressed background

        TextButton backButton = new TextButton("Back", backButtonStyle);

// Make it "big"
        backButton.getLabel().setFontScale(2f);  // Bigger text
        backButton.pad(20, 60, 20, 60); // Top, left, bottom, right padding (more space inside button)
        backButton.setWidth(300);       // Optional: fixed width
        backButton.setHeight(100);      // Optional: fixed height

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(com.badlogic.gdx.scenes.scene2d.InputEvent event, float x, float y) {
                game.setScreen(GdxGame.ScreenType.MAIN_MENU);
            }
        });

// Place it centered at the bottom
        rootTable.row().padBottom(40);
        rootTable.add(backButton).center().expandX();

    }

    /**
     * Opens a popup showing achievement details
     */
    private void showAchievementPopup(Achievement a) {
        Window popup = new Window("Achievement Details", windowStyle);

        Label name = new Label(a.getName(), headerStyle);
        Label description = new Label(a.getDescription(), textStyle);
        Label points = new Label("Points: " + a.getSkillPoint(), textStyle);

        name.setFontScale(1.5f);

        // --- Custom Close Button Style (Black text, White background) ---
        TextButton.TextButtonStyle closeBtnStyle = new TextButton.TextButtonStyle();
        closeBtnStyle.font = font;
        closeBtnStyle.fontColor = Color.BLACK;
        closeBtnStyle.up = makeColorDrawable(Color.WHITE);        // White background
        closeBtnStyle.down = makeColorDrawable(Color.LIGHT_GRAY); // Slightly darker when pressed

        TextButton closeBtn = new TextButton("Close", closeBtnStyle);
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


    /** Utility: make a solid-colored drawable */
    private TextureRegionDrawable makeColorDrawable(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(texture);
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
        font.dispose();
    }
}
