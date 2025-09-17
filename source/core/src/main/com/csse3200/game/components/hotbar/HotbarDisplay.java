package com.csse3200.game.components.hotbar;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.components.DeckInputComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.ui.UIComponent;
import java.util.Map;
import java.util.function.Supplier;

public class HotbarDisplay extends UIComponent {

  private Table hotbarTable;
  private final float scaling;
  private final LevelGameArea game;
  private final Map<String, Supplier<Entity>> unitList;
  private final Map<String, Supplier<Entity>> itemList;
  private Array<Image> slotImages = new Array<>();
  private float cellWidth;
  private Group layered;

  public HotbarDisplay(
      LevelGameArea game,
      Float scaling,
      Map<String, Supplier<Entity>> unitList,
      Map<String, Supplier<Entity>> itemList) {
    this.scaling = scaling;
    this.game = game;
    this.unitList = unitList;
    this.itemList = itemList;
  }

  @Override
  public void create() {
    super.create();
    addActors();
  }

  /**
   * This method creates the ui for the hotbar and the units that are selectable within its slots
   */
  private void addActors() {
    layered = new Group();

    // create hotbar image
    Image hotbar = new Image(new Texture("images/hotbar.png"));
    layered.addActor(hotbar);

    layered.setSize(hotbar.getPrefWidth(), hotbar.getPrefHeight());

    // initialise the values needed for placing unit images in slots
    float hotbarWidth = layered.getWidth();
    cellWidth = hotbarWidth / 6;
    float startX = cellWidth / 4;
    float y = 30;

    // creates unit images and places in slots
    for (Map.Entry<String, Supplier<Entity>> unit : unitList.entrySet()) {
      Image tempUnit = new Image(new Texture(unit.getKey()));
      tempUnit.setSize(scaling, scaling);

      slotImages.add(tempUnit);

      // listener for selection/use
      tempUnit.addListener(
          new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
              if (event.getButton() == Input.Buttons.LEFT) {
                game.setIsCharacterSelected(true);
                game.beginDrag(new Texture(unit.getKey()));
                Entity tempPlaceableUnit =
                    new Entity()
                        .addComponent(new DeckInputComponent(game, unit.getValue()))
                        .addComponent(new TextureRenderComponent(unit.getKey()));
                game.setSelectedUnit(tempPlaceableUnit);
              } else if (event.getButton() == Input.Buttons.RIGHT) {
                game.setSelectedUnit(null);
              }
              return false;
            }
          });
      layered.addActor(tempUnit);
    }
    for (Map.Entry<String, Supplier<Entity>> item : itemList.entrySet()) {
      Image tempUnit = new Image(new Texture(item.getKey()));
      tempUnit.setSize(scaling, scaling);

      slotImages.add(tempUnit);

      // listener for selection/use
      tempUnit.addListener(
          new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
              if (event.getButton() == Input.Buttons.LEFT) {
                game.setIsCharacterSelected(true);
                game.beginDrag(new Texture(item.getKey()));
                Entity tempPlaceableUnit =
                    new Entity()
                        .addComponent(new DeckInputComponent(game, item.getValue()))
                        .addComponent(new TextureRenderComponent(item.getKey()));
                game.setSelectedUnit(tempPlaceableUnit);
                remove(tempUnit);
              } else if (event.getButton() == Input.Buttons.RIGHT) {
                game.setSelectedUnit(null);
              }
              return false;
            }
          });

      layered.addActor(tempUnit);
    }

    layoutUnits(startX, y, cellWidth);

    // sets the position to the top middle of screen
    hotbarTable = new Table();
    hotbarTable.setFillParent(true);
    hotbarTable.center().top();
    float targetWidth = stage.getViewport().getWorldWidth() * 0.5f;
    float scale = targetWidth / layered.getWidth();

    layered.setScale(scale);

    // makes only the images touchable
    hotbarTable.setTouchable(Touchable.childrenOnly);

    // changes size to fit screen
    hotbarTable.add(layered).size(layered.getWidth() * scale, layered.getHeight() * scale);

    stage.addActor(hotbarTable);
    hotbarTable.toBack();
  }

  public void remove(Image usedUnit) {
    slotImages.removeValue(usedUnit, true);
    usedUnit.remove();
    layoutUnits(cellWidth / 4, 30, layered.getWidth() / 6);
  }

  private void layoutUnits(float startX, float y, float cellWidth) {
    float x = startX;
    for (Image img : slotImages) {
      img.setPosition(x, y);
      x += cellWidth;
    }
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  @Override
  public void dispose() {
    if (hotbarTable != null) {
      hotbarTable.remove();
      hotbarTable = null;
    }
    super.dispose();
  }
}
