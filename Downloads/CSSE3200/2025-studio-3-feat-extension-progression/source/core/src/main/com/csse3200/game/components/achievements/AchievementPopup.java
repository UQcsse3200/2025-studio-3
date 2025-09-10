package com.csse3200.game.components.achievements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

/**
 * Component for displaying achievement unlock notifications. Shows popup messages with sound
 * effects when achievements are unlocked.
 */
public class AchievementPopup {
  private final Stage stage;
  private final Sound unlockSound;

  public AchievementPopup(Stage stage) {
    this.stage = stage;
    unlockSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Impact4.ogg"));
  }

  public void show(String name, String description) {
    unlockSound.play(1.0f);

    // Create a simple colored background using Pixmap
    Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
    pixmap.setColor(new Color(0, 0, 0, 0.7f)); // semi-transparent black
    pixmap.fill();
    Drawable background = new TextureRegionDrawable(new Texture(pixmap));
    pixmap.dispose();

    // Table container for background + label
    Table popupTable = new Table();
    popupTable.setBackground(background);
    popupTable.pad(20); // padding around text

    // Label
    Label label = new Label("Achievement Unlocked!\n" + name + "\n" + description,
            new Label.LabelStyle(new com.badlogic.gdx.graphics.g2d.BitmapFont(), Color.WHITE));
    label.setFontScale(1.3f);

    popupTable.add(label);
    popupTable.pack();

    // Start above the screen
    float targetX = stage.getWidth() / 2f - popupTable.getWidth() / 2f;
    float targetY = stage.getHeight() - popupTable.getHeight() - 30;
    popupTable.setPosition(targetX, stage.getHeight()); // off-screen top

    // Slide in, wait, then slide out
    popupTable.addAction(Actions.sequence(
            Actions.moveTo(targetX, targetY, 0.5f),        // slide in
            Actions.delay(3f),                             // wait
            Actions.moveTo(targetX, stage.getHeight(), 0.5f), // slide out
            Actions.removeActor()                           // remove
    ));

    stage.addActor(popupTable);
  }

  public Stage getStage() {
    return stage;
  }

  public void dispose() {
    unlockSound.dispose();
  }
}
