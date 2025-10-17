package com.csse3200.game.minigame;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ui component for displaying the Main menu. */
public class MiniGameDisplay extends UIComponent {
  private static final Logger logger =
      LoggerFactory.getLogger(com.csse3200.game.minigame.MiniGameDisplay.class);
  private static final float Z_INDEX = 2f;
  private Table table;

  @Override
  public void create() {
    super.create();
    addActors();
  }

  /** Adds the actors to the table. */
  private void addActors() {
    table = new Table();
    table.setFillParent(true);

    TextButton laneRunnerBtn = ui.primaryButton("Lane Runner", 60f);
    TextButton wallPongBtn = ui.primaryButton("Wall Pong", 60f);

    // Triggers an event when the button is pressed
    laneRunnerBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Lane Runner button clicked");
            entity.getEvents().trigger("lanerunner");
          }
        });
    wallPongBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Wall Pong button clicked");
            entity.getEvents().trigger("wallpong");
          }
        });

    table.add(laneRunnerBtn).size(200f, 50f).padTop(30f);
    table.row();
    table.add(wallPongBtn).size(200f, 50f).padTop(30f);
    table.row();
    stage.addActor(table);

    // Add close button in top left corner
    createCloseButton();
  }

  /** Creates the close button in the top-left corner. */
  private void createCloseButton() {
    TextButton closeButton = ui.createBackButton(entity.getEvents(), stage.getHeight());
    stage.addActor(closeButton);
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void dispose() {
    table.clear();
    super.dispose();
  }
}
