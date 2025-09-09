package com.csse3200.game.components.achievements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

/**
 * Component for displaying achievement unlock notifications. Shows popup messages with sound
 * effects when achievements are unlocked.
 */
public class AchievementPopup {
  private final Stage stage;
  private final Sound unlockSound;

  /**
   * Creates a new achievement popup component.
   *
   * @param stage the stage to display popups on
   */
  public AchievementPopup(Stage stage) {
    this.stage = stage;

    // Load the sound once
    unlockSound = Gdx.audio.newSound(Gdx.files.internal("sounds/Impact4.ogg"));
  }

  /**
   * Shows an achievement unlock popup with the given name and description.
   *
   * @param name the name of the achievement
   * @param description the description of the achievement
   */
  public void show(String name, String description) {
    // Play the sound
    unlockSound.play(1.0f); // volume = 1.0f (max)

    Label label =
        new Label(
            "Achievement Unlocked!\n" + name + "\n" + description,
            new Label.LabelStyle(new com.badlogic.gdx.graphics.g2d.BitmapFont(), Color.WHITE));
    label.setFontScale(1.5f);

    float x = stage.getWidth() / 2f - label.getWidth() / 2f;
    float y = stage.getHeight() - label.getHeight() - 20;

    label.setPosition(x, y);
    stage.addActor(label);

    label.addAction(
        Actions.sequence(Actions.delay(3f), Actions.fadeOut(1f), Actions.removeActor()));
  }

  /** Disposes of resources used by this popup component. */
  public void dispose() {
    unlockSound.dispose();
  }
}
