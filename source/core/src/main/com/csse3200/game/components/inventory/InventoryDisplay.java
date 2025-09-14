package com.csse3200.game.components.inventory;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.GdxGame;
import com.csse3200.game.GdxGame.ScreenType;
import com.csse3200.game.entities.configs.BaseItemConfig;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The InventoryDisplay class is a UI component that renders a grid of player inventory items on
 * screen.
 */
public class InventoryDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(InventoryDisplay.class);
  private static final int ITEM_SIZE = 80;
  private static final int GRID_COLUMNS = 5;
  private Map<String, BaseItemConfig> inventoryItems;
  private final GdxGame game;

  // Root table that holds all UI elements for this screen
  private Table rootTable;

  /**
   * Creates an InventoryDisplay for the game instance.
   *
   * @param game current game instance
   */
  public InventoryDisplay(GdxGame game) {
    super();
    this.game = game;
    this.inventoryItems = Persistence.profile().getInventoryItems();
  }

  @Override
  public void create() {
    super.create();
    addActors();
  }

  /** Builds and adds the main UI actors for the Inventory screen. */
  private void addActors() {
    Label title = new Label("Inventory", skin, "title");
    ScrollPane inventoryScrollPane = makeInventoryGrid();
    Table menuBtns = makeBackBtn();
    rootTable = new Table();
    rootTable.setFillParent(true);
    rootTable.add(title).expandX().top().padTop(20f);
    rootTable.row().padTop(30f);
    rootTable.add(inventoryScrollPane).expandX().expandY().pad(20f);
    rootTable.row();
    rootTable.add(menuBtns).fillX();
    stage.addActor(rootTable);
  }

  /**
   * Creates a scrollable grid displaying the player's inventory items
   *
   * @return a ScrollPane containing the inventory grid
   */
  private ScrollPane makeInventoryGrid() {
    // Get player's inventory items

    // Create grid table
    Table gridTable = new Table();

    if (inventoryItems.isEmpty()) {
      Label emptyLabel = new Label("No items in inventory", skin);
      gridTable.add(emptyLabel).center();
    } else {
      int itemCount = 0;

      for (String itemKey : inventoryItems.keySet()) {
        // Start new row every GRID_COLUMNS items
        if (itemCount % GRID_COLUMNS == 0 && itemCount > 0) {
          gridTable.row().padTop(10f);
        }

        // Create item slot
        Table itemSlot = createItemSlot(itemKey);
        gridTable.add(itemSlot).pad(5f);

        itemCount++;
      }
    }

    // Create scroll pane
    ScrollPane scrollPane = new ScrollPane(gridTable, skin);
    scrollPane.setScrollingDisabled(true, false); // Only vertical scrolling
    scrollPane.setFadeScrollBars(false);

    return scrollPane;
  }

  /**
   * Creates a visual slot for an inventory item
   *
   * @param itemKey the key of the item to display
   * @return a Table containing the item's visual representation
   */
  private Table createItemSlot(String itemKey) {
    Table slot = new Table();

    // Find the corresponding asset path for this item
    String assetPath = inventoryItems.get(itemKey).getAssetPath();

    if (assetPath != null) {
      // Load and display item texture
      Texture itemTexture = ServiceLocator.getResourceService().getAsset(assetPath, Texture.class);

      Image itemImage = new Image(new TextureRegionDrawable(itemTexture));
      itemImage.setSize(ITEM_SIZE, ITEM_SIZE);

      slot.add(itemImage).size(ITEM_SIZE, ITEM_SIZE);
    } else {
      // Fallback if no texture found
      Label itemLabel = new Label("?", skin);
      slot.add(itemLabel).size(ITEM_SIZE, ITEM_SIZE);
    }

    // Add item name below the image
    slot.row();
    Label nameLabel = new Label(inventoryItems.get(itemKey).getName(), skin);
    nameLabel.setFontScale(0.8f);
    slot.add(nameLabel).center().padTop(5f);

    return slot;
  }

  /**
   * Builds a table containing exit button.
   *
   * @return table with exit button
   */
  private Table makeBackBtn() {
    TextButton backBtn = new TextButton("Back", skin);

    // Add listener for the back button
    backBtn.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Back button clicked");
            backMenu();
          }
        });

    // Place button in a table
    Table table = new Table();
    table.add(backBtn).expandX().left().pad(0f, 15f, 15f, 0f);
    return table;
  }

  /** Handles navigation back to the Profile Screen. */
  private void backMenu() {
    game.setScreen(ScreenType.MAIN_GAME);
  }

  @Override
  protected void draw(SpriteBatch batch) {
    // draw is handled by the stage
  }

  @Override
  public void dispose() {
    rootTable.clear();
    super.dispose();
  }
}
