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
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaneRunnerGameOverDisplay extends UIComponent {
  private static final Logger logger =
      LoggerFactory.getLogger(com.csse3200.game.minigame.LaneRunnerGameOverDisplay.class);
  private static final float Z_INDEX = 2f;
  private Table table;
  private final int finalScore;
  private final float survivalTime;
  private final int obstaclesDodged;

  public LaneRunnerGameOverDisplay(int finalScore, float survivalTime, int obstaclesDodged) {
    this.finalScore = finalScore;
    this.survivalTime = survivalTime;
    this.obstaclesDodged = obstaclesDodged;
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
            ServiceLocator.getResourceService().getAsset("images/GameOver.png", Texture.class));
    Label ScoreLabel = new Label("Final Score: " + finalScore, skin);
    Label TimeLabel =
        new Label("Survival Time: " + String.format("%.2f", survivalTime) + "s", skin);
    Label ObstaclesLabel = new Label("Obstacles Dodged: " + obstaclesDodged, skin);
    Label performanceLabel = new Label("Performance:" + getPerformanceRating(), skin);

    TextButton playagainbtn = new TextButton("Play Again", skin);
    TextButton mainmenubtn = new TextButton("Main Menu", skin);

    playagainbtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("playagain button pressed");
            entity.getEvents().trigger("playAgain");
          }
        });
    mainmenubtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("main menu button pressed");
            entity.getEvents().trigger("mainMenu");
          }
        });
    table.add(gameOverImage).padBottom(30f);
    table.row();
    table.add(ScoreLabel).padBottom(10f);
    table.row();
    table.add(TimeLabel).padBottom(10f);
    table.row();
    table.add(ObstaclesLabel).padBottom(20f);
    table.row();
    table.add(performanceLabel).padBottom(20f);
    table.row();
    table.add(playagainbtn).padBottom(10f);
    table.row();
    table.add(mainmenubtn);
    table.row();
    Label InstructionLabel = new Label(" 'Space' to PLayAgain & 'Escape' to Main Menu", skin);
    table.add(InstructionLabel);
    stage.addActor(table);
  }

  private String getPerformanceRating() {
    if (finalScore >= 60) {
      return "LEGENDARY!";
    } else if (finalScore >= 45) {
      return "EXCELLENT!";
    } else if (finalScore >= 30) {
      return "GREAT!";
    } else if (finalScore >= 20) {
      return "GOOD";
    } else if (finalScore >= 10) {
      return "NOT BAD!";
    } else {
      return "KEEP TRYING";
    }
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  @Override
  public void dispose() {
    table.clear();
    super.dispose();
  }
}
