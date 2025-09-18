package com.csse3200.game.minigame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.ButtonFactory;
import com.csse3200.game.ui.UIComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** ui component for displaying the Main menu. */
public class MiniGameDisplay extends UIComponent {
  private static final Logger logger =
      LoggerFactory.getLogger(com.csse3200.game.components.mainmenu.MainMenuDisplay.class);
  private static final float Z_INDEX = 2f;
  private Table table;

  @Override
  public void create() {
    super.create();
    addActors();
  }

  private void addActors() {
    table = new Table();
    table.setFillParent(true);

    TextButton LaneRunnerBtn = ButtonFactory.createButton("Lane Runner");
    TextButton WallPongBtn = ButtonFactory.createButton("Wall Pong");

    // Triggers an event when the button is pressed
    LaneRunnerBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Lane Runner button clicked");
            entity.getEvents().trigger("lanerunner");
          }
        });
    WallPongBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Wall Pong button clicked");
            entity.getEvents().trigger("wallpong");
          }
        });

    table.add(LaneRunnerBtn).size(200f, 50f).padTop(30f);
    table.row();
    table.add(WallPongBtn).size(200f, 50f).padTop(30f);
    table.row();
    stage.addActor(table);

    // Add close button in top left corner
    createCloseButton();
  }

  /** Creates the close button in the top-left corner. */
  private void createCloseButton() {
    ImageButton closeButton =
        new ImageButton(
            new TextureRegionDrawable(
                ServiceLocator.getGlobalResourceService()
                    .getAsset("images/ui/close-icon.png", Texture.class)));

    // Position in top left with 20f padding
    closeButton.setSize(60f, 60f);
    closeButton.setPosition(20f, stage.getHeight() - 60f - 20f);

    // Add listener for the close button
    closeButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Close button clicked");
            entity.getEvents().trigger("back");
          }
        });

    stage.addActor(closeButton);
  }

  @Override
  public void draw(SpriteBatch batch) {
    // draw is handled by the stage
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
