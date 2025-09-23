package com.csse3200.game.minigame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.ButtonFactory;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WallPongGameOverDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(WallPongGameOverDisplay.class);
  private static final float Z_INDEX = 2f;
  private Table table;
  private final int finalScore;
  private final float survivalTime;
  private final int ballsHit;

  public WallPongGameOverDisplay(int finalScore, float survivalTime, int ballsHit) {
    this.finalScore = finalScore;
    this.survivalTime = survivalTime;
    this.ballsHit = ballsHit;
  }

  @Override
  public void create() {
    super.create();
    addActors();
  }

  private void addActors() {
    table = new Table();
    table.setFillParent(true);
    Image gameOverImage =
        new Image(
            ServiceLocator.getResourceService()
                .getAsset("images/backgrounds/GameOver.png", Texture.class));
    Label scoreLabelLocal = new Label("Final Score: " + finalScore, skin);
    Label timeLabelLocal =
        new Label(String.format("Survival Time: %.2f seconds", survivalTime), skin);
    Label ballsHitLabel = new Label("Balls Hit: " + ballsHit, skin);
    Label performanceLabel = new Label("Performance:" + getPerformanceRating(), skin);

    TextButton playAgainBtn = ButtonFactory.createButton("Play Again");
    TextButton returnToArcadeBtn = ButtonFactory.createButton("Return to Arcade");

    playAgainBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("playagain button pressed");
            entity.getEvents().trigger("playAgain");
          }
        });

    returnToArcadeBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent event, Actor actor) {
            entity.getEvents().trigger("returnToArcade");
          }
        });
    table.add(gameOverImage).padBottom(30f);
    table.row();
    table.add(scoreLabelLocal).padBottom(10f);
    table.row();
    table.add(timeLabelLocal).padBottom(10f);
    table.row();
    table.add(ballsHitLabel).padBottom(10f);
    table.row();
    table.add(performanceLabel).padBottom(20f);
    table.row();
    table.add(playAgainBtn).padBottom(10f).width(200f).height(50f);
    table.row();
    table.add(returnToArcadeBtn).width(200f).height(50f);
    table.row();
    Label instructionLabel =
        new Label("Press 'Space' to Play Again or 'Escape' to Return to Arcade", skin);
    table.add(instructionLabel);
    stage.addActor(table);
  }

  private String getPerformanceRating() {
    if (finalScore >= 100) {
      return "PONG MASTER!";
    } else if (finalScore >= 70) {
      return "EXCELLENT!";
    } else if (finalScore >= 40) {
      return "GOOD!";
    } else {
      return "KEEP TRYING!";
    }
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void draw(SpriteBatch batch) {
    // UI is drawn automatically
  }

  @Override
  public void dispose() {
    table.clear();
    super.dispose();
  }
}
