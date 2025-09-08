package com.csse3200.game.components.shop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.components.items.Item;
import com.csse3200.game.persistence.Persistence;
import com.csse3200.game.progression.inventory.ItemRegistry;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The shop display component. */
public class ShopDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(ShopDisplay.class);
  private static final float Z_INDEX = 2f;
  private static final int SLOT_SIZE = 50;
  private Window.WindowStyle windowStyle;
  private static final Random random = new Random();
  private Item[] shopItems = new Item[3];
  private String[] shopItemImages = new String[3];
  private ImageButton[] itemSlots = new ImageButton[3];

  public ShopDisplay() {
    super();
    initializeShopItems();
    this.windowStyle = skin.get(Window.WindowStyle.class);
  }

  /** Creates the shop display. */
  @Override
  public void create() {
    super.create();
    addActors();
  }

  /** Adds actors to the stage. */
  private void addActors() {
    Image backgroundImage =
        new Image(
            ServiceLocator.getResourceService()
                .getAsset("images/shopbackground.jpg", Texture.class));

    backgroundImage.setFillParent(true);
    stage.addActor(backgroundImage);
    createItemSlots();
  }

  private void initializeShopItems() {
    ItemRegistry.ItemEntry itemOne = ItemRegistry.ITEMS[random.nextInt(ItemRegistry.ITEMS.length)];
    shopItemImages[0] = itemOne.assetPath();
    shopItems[0] = itemOne.item();
    ItemRegistry.ItemEntry itemTwo = ItemRegistry.ITEMS[random.nextInt(ItemRegistry.ITEMS.length)];
    shopItemImages[1] = itemTwo.assetPath();
    shopItems[1] = itemTwo.item();
    ItemRegistry.ItemEntry itemThree =
        ItemRegistry.ITEMS[random.nextInt(ItemRegistry.ITEMS.length)];
    shopItemImages[2] = itemThree.assetPath();
    shopItems[2] = itemThree.item();
  }

  private void createItemSlots() {
    float[] pedestalX = {
      stage.getWidth() * 0.25f, stage.getWidth() * 0.5f, stage.getWidth() * 0.75f
    };

    float pedestalY = stage.getHeight() * 0.4f;

    for (int i = 0; i < 3; i++) {
      createItemSlot(i, pedestalX[i] - SLOT_SIZE / 2f, pedestalY, shopItemImages[i]);
    }
  }

  private void createItemSlot(int slotIndex, float x, float y, String itemTexture) {
    // Load the item texture
    Texture itemTex = ServiceLocator.getResourceService().getAsset(itemTexture, Texture.class);

    // Create ImageButton with the item texture
    ImageButton slot = new ImageButton(new TextureRegionDrawable(itemTex));

    // Position and size the slot
    slot.setPosition(x, y);
    slot.setSize(SLOT_SIZE, SLOT_SIZE);

    // Add click listener
    slot.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Item slot {} clicked ({})", slotIndex, itemTexture);
            onItemSlotClicked(slotIndex, itemTexture);
          }
        });

    // Store reference and add to stage
    itemSlots[slotIndex] = slot;
    stage.addActor(slot);
  }

  private void onItemSlotClicked(int slotIndex, String itemTexture) {
    logger.info("Selected item: {} from slot {}", itemTexture, slotIndex);
    showShopItemPopUp(shopItems[slotIndex]);
    entity.getEvents().trigger("item_selected", slotIndex, itemTexture);
  }

  private void showShopItemPopUp(Item a) {
    Window popup = new Window("Item Details", windowStyle);
    popup.setModal(true);
    popup.setMovable(false);

    // Create main content table
    Table contentTable = new Table();
    contentTable.pad(20f);

    // Item name label
    Label nameLabel = new Label(a.getName(), skin, "title");
    nameLabel.setColor(Color.WHITE);
    contentTable.add(nameLabel).colspan(2).center().padBottom(10f).row();

    // Item description
    Label descriptionLabel = new Label(a.getDescription(), skin);
    descriptionLabel.setColor(Color.LIGHT_GRAY);
    descriptionLabel.setWrap(true);
    contentTable.add(descriptionLabel).colspan(2).width(300f).center().padBottom(15f).row();

    // Price label
    Label priceLabel = new Label("Price: $" + a.getCost(), skin);
    priceLabel.setColor(Color.GOLD);
    contentTable.add(priceLabel).colspan(2).center().padBottom(20f).row();

    // Buttons table
    Table buttonTable = new Table();

    // Purchase button
    TextButton purchaseButton = new TextButton("Purchase", skin);
    purchaseButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Purchase button clicked for item: {}", a.getName());
            onPurchaseClicked(a);
            popup.remove();
          }
        });

    // Close button
    TextButton closeButton = new TextButton("Close", skin);
    closeButton.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Close button clicked");
            popup.remove();
          }
        });

    buttonTable.add(purchaseButton).padRight(10f);
    buttonTable.add(closeButton);
    contentTable.add(buttonTable).colspan(2).center();
    popup.add(contentTable);
    popup.pack();
    popup.setPosition(
        (stage.getWidth() - popup.getWidth()) / 2f, (stage.getHeight() - popup.getHeight()) / 2f);
    stage.addActor(popup);
  }

  private void onPurchaseClicked(Item item) {
    if (Persistence.profile().wallet().purchaseShopItem(item.getCost())) {
      Persistence.profile().inventory().addItem(item);
      logger.info("Successfully purchased: {}", item.getName());
      entity.getEvents().trigger("purchased");
    } else {
      logger.warn("Insufficient funds to purchase: {}", item.getName());
    }
  }

  @Override
  public void draw(SpriteBatch batch) {}

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void dispose() {
    super.dispose();
  }
}
