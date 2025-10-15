package com.csse3200.game.minigame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

public class LaneRunnerHUD extends UIComponent {
  private Label scoreLabel;
  private Label timeLabel;

  @Override
  public void create() {
    super.create();

    BitmapFont font = ServiceLocator.getGlobalResourceService().generateFreeTypeFont("Default", 20);
    Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

    scoreLabel = new Label("Score: 0", labelStyle);
    scoreLabel.setFontScale(2f);
    scoreLabel.setPosition(20, Gdx.graphics.getHeight() - 40f);
    stage.addActor(scoreLabel);

    timeLabel = new Label("Time: 0.0s", labelStyle);
    timeLabel.setFontScale(2f);
    timeLabel.setPosition(Gdx.graphics.getWidth() - 200f, Gdx.graphics.getHeight() - 40f);
    stage.addActor(timeLabel);
  }

  public void setScore(float score) {
    if (scoreLabel != null) {
      scoreLabel.setText("Score: " + score);
    }
  }

  public void setTime(float survivalTimeSeconds) {
    if (timeLabel != null) {
      timeLabel.setText(String.format("Time: %.1fs", survivalTimeSeconds));
    }
  }
}


