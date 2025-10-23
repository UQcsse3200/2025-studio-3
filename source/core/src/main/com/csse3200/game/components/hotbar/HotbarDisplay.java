package com.csse3200.game.components.hotbar;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
import com.csse3200.game.persistence.Settings;
import com.csse3200.game.rendering.TextureRenderComponent;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import com.csse3200.game.ui.UIFactory;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HotbarDisplay extends UIComponent {

  private Table unitHotbarTable;
  private Table itemHotbarTable;
  private final float scaling;
  private final LevelGameArea game;
  private final Map<String, Supplier<Entity>> unitList;
  private final Map<String, Supplier<Entity>> itemList;
  private static final Logger logger = LoggerFactory.getLogger(HotbarDisplay.class);
  // a list of all the images for the slots
  private final Array<Image> slotImages = new Array<>();
  private final Array<Image> itemImages = new Array<>();
  private float cellWidth;
  private Label insufficientScrapMessage;
  private long insufficientScrapStartTime = -1; // -1 means not active
  private static final long SCRAP_MESSAGE_DURATION = 2000; // 2 seconds in ms
  private final Map<Entity, Label> generatorCostLabels = new HashMap<>();
  private int lastFurnaceCount = -1;

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
    // Creates images for the hotbars (needs to different variables)
    Image unitHotbar = new Image(new Texture("images/ui/hotbar.png"));
    Image itemHotbar = new Image(new Texture("images/ui/hotbar.png"));

    // Create hotbars and position them
    unitHotbarTable = createUnitHotbar(unitHotbar);
    itemHotbarTable = createItemHotbar(itemHotbar);
    stage.addActor(itemHotbarTable);
    itemHotbarTable.toBack();
    stage.addActor(unitHotbarTable);

    // Listener to cancel placement
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
    Table messageTable = new Table();
    messageTable.setFillParent(true);
    messageTable.center().bottom().padBottom(50f);
    stage.addActor(messageTable);
    UIFactory insufficientScrapUI = new UIFactory(skin, Settings.UIScale.LARGE);
    insufficientScrapMessage = insufficientScrapUI.text("Insufficient Scrap!");
    insufficientScrapMessage.setVisible(false);
    messageTable.add(insufficientScrapMessage);
    entity.getEvents().addListener("insufficientScrap", this::insufficientScrap);
  }

  /**
   * Creates the unit hotbar for the game. Places all the available units and their costs in the
   * slots of the hotbar.
   *
   * @param hotbar The image of the unit hotbar
   * @return The created unit hotbar
   */
  private Table createUnitHotbar(Image hotbar) {
    // create group to store all assets for the hotbar
    Group unitLayers = new Group();
    unitLayers.addActor(hotbar);
    unitLayers.setSize(hotbar.getPrefWidth(), hotbar.getPrefHeight());

    // initialise the values needed for placing unit images in slots
    float hotbarWidth = unitLayers.getWidth();
    cellWidth = hotbarWidth / 9;
    float startX = cellWidth / 6;
    float y = 30;
    float currentX = startX;

    // creates unit images and places in slots
    for (Map.Entry<String, Supplier<Entity>> unit : unitList.entrySet()) {
      Table slot = new Table();
      Image tempUnit = new Image(new Texture(unit.getKey()));
      tempUnit.setSize((float) (scaling * 1.3), (float) (scaling * 1.3));
      slotImages.add(tempUnit);

      // Get the cost of the entity
      Entity entity = unit.getValue().get();
      GeneratorStatsComponent generator = entity.getComponent(GeneratorStatsComponent.class);
      DefenderStatsComponent defender = entity.getComponent(DefenderStatsComponent.class);

      // Handles displaying the cost in the hotbar
      Label displayCost = ui.createLabel("50", 30, Color.WHITE);
      displayCost.setTouchable(Touchable.disabled);

      if (generator != null) {
        generatorCostLabels.put(entity, displayCost);
      } else {
        int entityCost = defender.getCost();
        displayCost.setText(String.valueOf(entityCost));
      }

      displayCost.setFontScale(2f);
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
      unitLayers.addActor(tempUnit);
      unitLayers.addActor(slot);
    }
    layoutUnits(startX, y, cellWidth, slotImages);

    // create table for hotbar and set position
    Table tempHotbarTable = new Table();
    tempHotbarTable.setFillParent(true);
    tempHotbarTable.center().top();

    float targetWidth = stage.getViewport().getWorldWidth() * 0.35f;
    float scale = targetWidth / unitLayers.getWidth();
    unitLayers.setScale(scale);

    // makes only the images touchable
    tempHotbarTable.setTouchable(Touchable.childrenOnly);

    // changes size to fit screen
    tempHotbarTable
        .add(unitLayers)
        .size(unitLayers.getWidth() * scale, unitLayers.getHeight() * scale);
    return tempHotbarTable;
  }

  /**
   * Creates the collapsible item hotbar for the game and button to collapse it. Places all the
   * available items in the slots of the hotbar and creates the button used to collapse the hotbar.
   *
   * @param hotbar The image of the item hotbar
   * @return The created item hotbar
   */
  private Table createItemHotbar(Image hotbar) {
    // create group to store all assets for the hotbar
    Group itemLayers = new Group();
    itemLayers.addActor(hotbar);
    itemLayers.setSize(hotbar.getPrefWidth(), hotbar.getPrefHeight());

    // initialise the values needed for placing unit images in slots
    float hotbarWidth = itemLayers.getWidth();
    cellWidth = hotbarWidth / 9;
    float startX = cellWidth / 6;
    float y = 30;

    // creates down arrow image
    Image upDownArrow = new Image(new Texture("images/ui/up_down_arrow.png"));
    upDownArrow.setSize((float) (1.5 * scaling), (float) (0.6 * scaling));
    upDownArrow.setPosition((float) (0.45 * hotbarWidth), -40);

    // creates all the items
    for (Map.Entry<String, Supplier<Entity>> item : itemList.entrySet()) {
      Image tempItem = new Image(new Texture(item.getKey()));
      tempItem.setSize((float) (scaling * 1.3), (float) (scaling * 1.3));

      itemImages.add(tempItem);

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

      itemLayers.addActor(tempItem);
    }
    // lays out the items
    layoutUnits(startX, y, cellWidth, itemImages);

    // create table for hotbar and set position
    Table tempHotbarTable = new Table();
    tempHotbarTable.setFillParent(true);
    float targetWidth = stage.getViewport().getWorldWidth() * 0.35f;
    float scale = targetWidth / itemLayers.getWidth();
    tempHotbarTable.center().top().padTop(165 * scale);

    // handles the collapsing of the item hotbar
    final boolean[] isUp = {false};
    upDownArrow.addListener(
        new ClickListener() {
          @Override
          public void clicked(InputEvent event, float x, float y) {
            float distance = 170 * scale;

            if (!isUp[0]) {
              // Move up
              tempHotbarTable.addAction(Actions.moveBy(0, distance, 0.35f));
              itemLayers.addAction(
                  Actions.sequence(
                      Actions.delay(0.35f), Actions.run(() -> itemLayers.setVisible(false))));
              isUp[0] = true;
            } else {
              // Move down
              tempHotbarTable.addAction(Actions.moveBy(0, -distance, 0.35f));
              itemLayers.addAction(
                  Actions.sequence(
                      Actions.delay(0.05f), Actions.run(() -> itemLayers.setVisible(true))));
              isUp[0] = false;
            }
          }
        });

    itemLayers.setScale(scale);
    itemLayers.toBack();
    tempHotbarTable
        .add(itemLayers)
        .size(itemLayers.getWidth() * scale, itemLayers.getHeight() * scale);
    // makes only the images touchable
    tempHotbarTable.setTouchable(Touchable.childrenOnly);
    tempHotbarTable.row();
    tempHotbarTable
        .add(upDownArrow)
        .size(upDownArrow.getWidth() * scale, upDownArrow.getHeight() * scale);
    return tempHotbarTable;
  }

  @Override
  public void resize() {
    super.resize();
    // Future: handle dynamic resizing if required
  }

  /**
   * This method removes an image from a slot this is used to remove items after they are used
   *
   * @param usedUnit is an image of the item to be removed
   */
  public void remove(Image usedUnit) {
    itemImages.removeValue(usedUnit, true);
    usedUnit.remove();
    // reformats the layout of the slots so there isn't gaps
    layoutUnits(cellWidth / 6, 30, cellWidth, itemImages);
  }

  /**
   * Takes all the items and units to be placed in the hotbar and puts them into the slots of the
   * hotbar
   *
   * @param startX the starting x value of the hotbar
   * @param y the y value of the hotbar
   * @param cellWidth the width of the cells to increment the x value
   * @param array an array of images to be laid out
   */
  private void layoutUnits(float startX, float y, float cellWidth, Array<Image> array) {
    float x = startX;
    for (Image img : array) {
      img.setPosition(x, y);
      x += cellWidth;
      Label a = new Label("hey", skin);
      a.setFontScale(5f);
      a.setPosition(x, y - 10);
    }
  }

  @Override
  public void dispose() {
    if (unitHotbarTable != null) {
      unitHotbarTable.remove();
      unitHotbarTable = null;
    }
    if (itemHotbarTable != null) {
      itemHotbarTable.remove();
      itemHotbarTable = null;
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

    int currentFurnaceCount = 0;
    if (ServiceLocator.getGameArea() != null) {
      for (Entity entity : ServiceLocator.getGameArea().getEntities()) {
        if (entity.getComponent(GeneratorStatsComponent.class) != null) {
          currentFurnaceCount++;
        }
      }
    }
    if (currentFurnaceCount != lastFurnaceCount) {
      for (Map.Entry<Entity, Label> entry : generatorCostLabels.entrySet()) {

        Label costLabel = entry.getValue();

        int newCost = 50 + (currentFurnaceCount * 50);
        costLabel.setText(String.valueOf(newCost));
      }
      lastFurnaceCount = currentFurnaceCount;
    }
    // Handles the message pop-up when the player doesn't have enough scrap
    if (insufficientScrapStartTime != -1) {
      long elapsed = ServiceLocator.getTimeSource().getTime() - insufficientScrapStartTime;
      if (elapsed >= SCRAP_MESSAGE_DURATION) {
        insufficientScrapMessage.setVisible(false);
        insufficientScrapStartTime = -1; // reset timer
      }
    }
  }
}
