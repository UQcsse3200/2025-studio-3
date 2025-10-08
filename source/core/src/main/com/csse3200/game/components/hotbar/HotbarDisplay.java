package com.csse3200.game.components.hotbar;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.csse3200.game.areas.LevelGameArea;
import com.csse3200.game.components.DeckInputComponent;
import com.csse3200.game.components.DefenderStatsComponent;
import com.csse3200.game.components.GeneratorStatsComponent;
import com.csse3200.game.components.items.ItemComponent;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotbarDisplay extends UIComponent {

  private Table hotbarTable;
  private final float scaling;
  private final LevelGameArea game;
  private final Map<String, Supplier<Entity>> unitList;
  private final Map<String, Supplier<Entity>> itemList;
  private static final Logger logger = LoggerFactory.getLogger(HotbarDisplay.class);
  // a list of all the images for the slots
  private final Array<Image> slotImages = new Array<>();
  private float cellWidth;
  private Label insufficientScrapMessage;
  private long insufficientScrapStartTime = -1; // -1 means not active
  private static final long SCRAP_MESSAGE_DURATION = 2000; // 2 seconds in ms

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
    Group layered = new Group();

    // create hotbar image
    Image hotbar = new Image(new Texture("images/ui/hotbar.png"));
    layered.addActor(hotbar);

    layered.setSize(hotbar.getPrefWidth(), hotbar.getPrefHeight());

    // initialise the values needed for placing unit images in slots
    float hotbarWidth = layered.getWidth();
    cellWidth = hotbarWidth / 6;
    float startX = cellWidth / 4;
    float y = 30;
    float currentX = startX;

    // creates unit images and places in slots
    for (Map.Entry<String, Supplier<Entity>> unit : unitList.entrySet()) {
      Table slot = new Table();

      Image tempUnit = new Image(new Texture(unit.getKey()));
      tempUnit.setSize(scaling, scaling);

      slotImages.add(tempUnit);

      // Get the cost of the entity
      Entity entity = unit.getValue().get();
      GeneratorStatsComponent generator = entity.getComponent(GeneratorStatsComponent.class);
      DefenderStatsComponent defender = entity.getComponent(DefenderStatsComponent.class);
      int entityCost;

      if (generator != null) {
        entityCost = generator.getCost();
      } else {
        entityCost = defender.getCost();
      }

      // Handles displaying the cost in the hotbar
      Label displayCost = new Label(String.valueOf(entityCost), skin);

      displayCost.setPosition(
          tempUnit.getWidth() / 2f - displayCost.getPrefWidth() / 2f,
          -displayCost.getPrefHeight() - 5f);

      slot.add(tempUnit).row();
      slot.add(displayCost);
      slot.setPosition(currentX, y);

      currentX += cellWidth;

      // listener for selection/use
      tempUnit.addListener(
          new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
              if (event.getButton() == Input.Buttons.LEFT) {
                // sets the drag image to the unit image and selects it
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
      layered.addActor(slot);
    }
    for (Map.Entry<String, Supplier<Entity>> item : itemList.entrySet()) {
      Image tempItem = new Image(new Texture(item.getKey()));
      tempItem.setSize(scaling, scaling);

      slotImages.add(tempItem);

      // listener for selection/use
      tempItem.addListener(
          new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
              if (event.getButton() == Input.Buttons.LEFT) {
                // sets the drag image to the item image and selects it
                game.setIsCharacterSelected(true);
                game.beginDrag(new Texture(item.getKey()));
                Entity tempPlaceableUnit =
                    new Entity()
                        .addComponent(new DeckInputComponent(game, item.getValue()))
                        .addComponent(new TextureRenderComponent(item.getKey()));
                game.setSelectedUnit(tempPlaceableUnit);
                // Get name of item
                String itemName =
                    item.getValue()
                        .get()
                        .getComponent(ItemComponent.class)
                        .getType()
                        .toString()
                        .toLowerCase(Locale.ROOT);
                // Only remove item from hotbar if it is the last of that item in the inventory
                if (!ServiceLocator.getProfileService()
                    .getProfile()
                    .getInventory()
                    .containsMoreThanOne(itemName)) {
                  // removes the item from use
                  remove(tempItem);
                  logger.info("Remove {} from hotbar", itemName);
                }
              } else if (event.getButton() == Input.Buttons.RIGHT) {
                game.setSelectedUnit(null);
              }
              return false;
            }
          });

      layered.addActor(tempItem);
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
    hotbarTable.add(layered).size(layered.getWidth() * scale, layered.getHeight() * scale).row();

    stage.addActor(hotbarTable);
    hotbarTable.toBack();

    stage.addListener(
        new ClickListener() {
          @Override
          public boolean touchDown(InputEvent event, float sx, float sy, int pointer, int button) {
            if (button == Input.Buttons.RIGHT && game.isCharacterSelected()) {
              cancelSelection();
              return true;
            }
            return false;
          }
        });

    // Sets a placeholder message and an event to be called from other classes
    insufficientScrapMessage = new Label("", skin);
    insufficientScrapMessage.setVisible(false);
    hotbarTable.add(insufficientScrapMessage).padTop(5f);
    entity.getEvents().addListener("insufficientScrap", this::insufficientScrap);
  }

  /**
   * This method removes an image from a slot this is used to remove items after they are used
   *
   * @param usedUnit is an image of the item to be removed
   */
  public void remove(Image usedUnit) {
    slotImages.removeValue(usedUnit, true);
    usedUnit.remove();
    // reformats the layout of the slots so there isn't gaps
    layoutUnits(cellWidth / 4, 30, cellWidth);
  }

  /**
   * Takes all the items and units to be placed in the hotbar and puts them into the slots of the
   * hotbar
   *
   * @param startX the starting x value of the hotbar
   * @param y the y value of the hotbar
   * @param cellWidth the width of the cells to increment the x value
   */
  private void layoutUnits(float startX, float y, float cellWidth) {
    float x = startX;
    for (Image img : slotImages) {
      img.setPosition(x, y);
      x += cellWidth;
      Label a = new Label("hey", skin);
      a.setFontScale(5f);
      a.setPosition(x, y - 10);
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

  /** Cancels the current selection and drag, if any. */
  private void cancelSelection() {
    game.setIsCharacterSelected(false);
    game.setSelectedUnit(null);
    game.cancelDrag();
  }

  /** Displays a message when called and starts a timer. */
  private void insufficientScrap() {
    insufficientScrapMessage.setText("Not enough scrap!");
    insufficientScrapMessage.setVisible(true);
    insufficientScrapStartTime = ServiceLocator.getTimeSource().getTime();
  }

  /** Handles how long the message gets displayed for. */
  @Override
  public void update() {
    // Hide message after 2 seconds
    if (insufficientScrapStartTime != -1) {
      long elapsed = ServiceLocator.getTimeSource().getTime() - insufficientScrapStartTime;
      if (elapsed >= SCRAP_MESSAGE_DURATION) {
        insufficientScrapMessage.setVisible(false);
        insufficientScrapStartTime = -1; // reset timer
      }
    }
  }
}
