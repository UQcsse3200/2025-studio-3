package com.csse3200.game.minigame;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;

public class MinigameHUD extends UIComponent {
  private Label scoreLabel;
  private Label timeLabel;
  private Table table;

  @Override
  public void create() {
    super.create();
    
    // Create labels using UIFactory for consistent styling
    scoreLabel = ui.subheading("Score: 0");
    timeLabel = ui.subheading("Time: 0.00s");

    // Create table layout
    table = new Table();
    table.setFillParent(true);
    table.top().left();
    table.pad(ui.getScaledWidth(20f));
    
    // Add labels to table
    table.add(scoreLabel).left().row();
    table.add(timeLabel).left().padTop(ui.getScaledHeight(10f)).row();
    
    // Add table to stage
    stage.addActor(table);
  }

  @Override
  public void update() {
    super.update();
    
    // Don't update HUD if game is over
    if (ServiceLocator.getMinigameService().isGameOver()) {
      return;
    }
    
    // Update labels with current game state
    int score = ServiceLocator.getMinigameService().getScore();
    float time = ServiceLocator.getTimeSource().getTime();
    
    if (scoreLabel != null) {
      scoreLabel.setText("Score: " + score);
    }
    
    if (timeLabel != null) {
      timeLabel.setText(String.format("Time: %.2fs", time / 1000f));
    }
  }

  @Override
  public void dispose() {
    super.dispose();
    if (table != null) {
      table.clear();
    }
  }
}


