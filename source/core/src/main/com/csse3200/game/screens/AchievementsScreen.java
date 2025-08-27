package com.csse3200.game.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.csse3200.game.GdxGame;
import com.csse3200.game.Achievements.Achievement;
import com.csse3200.game.Achievements.AchievementManager;
import com.csse3200.game.services.ServiceLocator;

import java.util.List;

public class AchievementsScreen extends ScreenAdapter {
    private final GdxGame game;
    private SpriteBatch batch;
    private BitmapFont font;
    private AchievementManager achievementManager; // <-- store a reference safely

    public AchievementsScreen(GdxGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // TODO: Replace with proper UI skin later




        achievementManager = ServiceLocator.getAchievementManager();
        // Access the AchievementManager here, after it has been registered
        System.out.println("DEBUG: Achievements count = " + achievementManager.getAllAchievements().size());

        if (achievementManager == null) {
            Gdx.app.error("AchievementsScreen", "AchievementManager not initialized! Creating fallback...");
            achievementManager = new AchievementManager();
            ServiceLocator.registerAchievementManager(achievementManager);

            // Add fallback test achievements so the screen isn't empty
            achievementManager.addAchievement(
                    new Achievement("Debug A", "Fallback achievement A", 5)
            );
            achievementManager.addAchievement(
                    new Achievement("Debug B", "Fallback achievement B", 10)
            );
        }

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(248f/255f, 249f/255f, 178/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch, "Achievements", 50, 450);

        if (achievementManager != null) {
            int y = 400;
            for (Achievement a : achievementManager.getAllAchievements()) {
                String status = a.isUnlocked() ? "Unlocked" : "Locked";
                font.draw(batch, "- " + a.getName() + " (" + status + ")", 50, y);
                y -= 30;
            }
        }

        font.draw(batch, "[ESC] Back to Menu", 50, 50);
        batch.end();

        // Simple input handling
        if (Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            game.setScreen(GdxGame.ScreenType.MAIN_MENU);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
