package com.csse3200.game.components.minigame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import net.dermetfan.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** UI component for displaying the mini game selection menu. */
public class MiniGameDisplay extends UIComponent {
  private static final Logger logger =
      LoggerFactory.getLogger(com.csse3200.game.components.minigame.MiniGameDisplay.class);
  private static final float Z_INDEX = 2f;
  private Table table;

  @Override
  public void create() {
    super.create();
    addActors();
  }

  /** Adds the actors to the table. */
  private void addActors() {
    Stack stack = new Stack();
    stack.setFillParent(false);

    Label title = ui.title("Arcade");

    float frameWidth = ui.getScaledWidth(320);
    float frameHeight = ui.getScaledHeight(390);
    stack.setSize(frameWidth, frameHeight);
    stack.setPosition((stage.getWidth() - frameWidth) / 2f, (stage.getHeight() - frameHeight) / 2f);
    Texture frameTexture =
        ServiceLocator.getGlobalResourceService().getAsset("images/ui/menu.png", Texture.class);
    Image frameImage = new Image(frameTexture);
    frameImage.setSize(frameWidth, frameHeight);

    table = new Table();
    table.center();
    table.setFillParent(false);

    // Add the frame first so it's behind the table
    stage.addActor(frameImage);

    TextButton laneRunnerBtn = ui.primaryButton("Lane Runner", 200f);
    TextButton wallPongBtn = ui.primaryButton("Wall Pong", 200f);
    Pair<Float, Float> buttonDimensions = ui.getScaledDimensions(200f);

    laneRunnerBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            logger.debug("Lane Runner button clicked");
            entity.getEvents().trigger("lanerunner");
          }
        });

    wallPongBtn.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            logger.debug("Wall Pong button clicked");
            entity.getEvents().trigger("wallpong");
          }
        });

    table.add(title);
    table.row();
    table
        .add(laneRunnerBtn)
        .width(buttonDimensions.getKey())
        .height(buttonDimensions.getValue())
        .padTop(30f);
    table.row();
    table
        .add(wallPongBtn)
        .width(buttonDimensions.getKey())
        .height(buttonDimensions.getValue())
        .padTop(30f);

    stack.add(frameImage);
    stack.add(table);

    stage.addActor(stack);

    // Close button stays on top
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
