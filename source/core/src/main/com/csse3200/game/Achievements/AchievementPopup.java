package com.csse3200.game.Achievements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.graphics.Color;

public class AchievementPopup {
    private final Stage stage;
    private final Sound unlockSound;

    public AchievementPopup(Stage stage) {
        this.stage = stage;

        // Load the sound once
        unlockSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Impact4.ogg"));
    }

    public void show(String name, String description) {
        // Play the sound
        unlockSound.play(1.0f); // volume = 1.0f (max)

        Label label = new Label("Achievement Unlocked!\n" + name + "\n" + description,
                new Label.LabelStyle(new com.badlogic.gdx.graphics.g2d.BitmapFont(), Color.WHITE));
        label.setFontScale(1.5f);

        float x = stage.getWidth() / 2f - label.getWidth() / 2f;
        float y = stage.getHeight() - label.getHeight() - 20;

        label.setPosition(x, y);
        stage.addActor(label);

        label.addAction(Actions.sequence(
                Actions.delay(3f),
                Actions.fadeOut(1f),
                Actions.removeActor()
        ));
    }

    public void dispose() {
        unlockSound.dispose();
    }
}
