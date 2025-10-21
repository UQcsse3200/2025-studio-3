package com.csse3200.game.components.shop;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.entities.configs.BaseItemConfig;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import com.csse3200.game.utils.ShopRandomizer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The shop display component. */
public class ShopDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(ShopDisplay.class);
  private static final float Z_INDEX = 2f;
  private static final int ITEM_SIZE = 160;
  private static final int COIN_ICON_SIZE = 40;
  private ImageButton[] itemSlots = new ImageButton[3];
  private String[] itemKeys = new String[3];
  private Label timerLabel;
  private Table rootTable;
  private TextButton closeButton;
  private final Sound purchasedSound =
      ServiceLocator.getResourceService().getAsset("sounds/item_purchased_sound.mp3", Sound.class);
  private float uiScale;

  /** Creates a new ShopDisplay. */
  public ShopDisplay() {
    super();
    initializeShopItems();
  }

  @Override
  public void create() {
    super.create();
    uiScale = ui.getUIScale();
    // Check and reset shop period if needed
    ServiceLocator.getProfileService().getProfile().checkAndResetShopPeriod();
    addActors();
  }

  @Override
  public void update() {
    super.update();
    updateTimer();
    recenterTable();
  }

  /** Adds actors to the stage. */
  private void addActors() {
    // Create background image
    Texture backgroundTexture =
        ServiceLocator.getGlobalResourceService()
            .getAsset("images/ui/menu_card.png", Texture.class);
    Image backgroundImage = new Image(backgroundTexture);
    backgroundImage.setSize(870f * uiScale, 610f * uiScale);

    // Create content table for shop
    rootTable = new Table();
    rootTable.setSize(870f * uiScale, 610f * uiScale);
    rootTable.center();

    createShopDisplay();

    // Create stack with background and content
    Stack stack = new Stack();
    stack.add(backgroundImage);
    stack.add(rootTable);
    stack.setSize(1044f * uiScale, 732f * uiScale);
    stack.setPosition(
        (stage.getWidth() - stack.getWidth()) / 2, (stage.getHeight() - stack.getHeight()) / 2);

    // Add stack to stage
    stage.addActor(stack);

    // Add title at the top of the background image (outside the content table)
    Label title = ui.title("Shop");
    title.setPosition(
        stack.getX() + (stack.getWidth() - title.getWidth()) / 2,
        stack.getY() + stack.getHeight() - 85f * uiScale);
    stage.addActor(title);

    createCloseButton();
  }

  /** Creates the close button in the top-left corner. */
  private void createCloseButton() {
    // Create close button using createBackButton
    closeButton = ui.createBackButton(entity.getEvents(), stage.getHeight());
    stage.addActor(closeButton);
  }

  /** Creates the main shop display layout. */
  private void createShopDisplay() {
    createTimerDisplay();
    rootTable.add(timerLabel).center().padBottom(20f * uiScale).row();
    createItemSlots();
  }

  /** Creates and displays the reset timer. */
  private void createTimerDisplay() {
    timerLabel = ui.subheading("");
    updateTimer();
  }

  /** Updates the countdown timer display. */
  private void updateTimer() {
    if (timerLabel == null) return;

    LocalDateTime now = LocalDateTime.now();
    // Calculate next 15-minute interval
    int currentMinute = now.getMinute();
    int nextInterval = ((currentMinute / 15) + 1) * 15;
    if (nextInterval >= 60) {
      nextInterval = 0;
    }
    LocalDateTime nextReset = now.withMinute(nextInterval).withSecond(0).withNano(0);
    if (nextInterval == 0) {
      nextReset = nextReset.plusHours(1);
    }

    long minutesLeft = ChronoUnit.MINUTES.between(now, nextReset);
    long secondsLeft = ChronoUnit.SECONDS.between(now, nextReset) % 60;

    timerLabel.setText(String.format("Shop resets in: %02d:%02d", minutesLeft, secondsLeft));
  }

  /** Recenters the table and background to ensure they stay centered on screen. */
  private void recenterTable() {
    if (rootTable == null) return;

    // Update close button position
    if (closeButton != null) {
      closeButton.setPosition(
          20f, // 20f padding from left
          stage.getHeight() - 60f - 20f // 20f padding from top
          );
    }

    // Recenter the stack if it exists
    for (Actor actor : stage.getActors()) {
      if (actor instanceof Stack stack && stack.getWidth() == 1044f * uiScale) {
        stack.setPosition(
            (stage.getWidth() - stack.getWidth()) / 2, (stage.getHeight() - stack.getHeight()) / 2);
        break;
      }
    }
  }

  /** Initializes the shop items. */
  private void initializeShopItems() {
    ConfigService configService = ServiceLocator.getConfigService();
    if (configService == null) {
      logger.warn("ConfigService is null");
      return;
    }
    String[] allItemKeys = configService.getItemKeys();
    int[] itemIndexes =
        ShopRandomizer.getShopItemIndexes(
            ServiceLocator.getProfileService().getProfile().getName(),
            0,
            allItemKeys.length - 1,
            LocalDateTime.now());
    this.itemKeys =
        new String[] {
          allItemKeys[itemIndexes[0]], allItemKeys[itemIndexes[1]], allItemKeys[itemIndexes[2]]
        };
  }

  /** Creates the item slots with prices and coin icons. */
  private void createItemSlots() {
    Table itemTable = new Table();

    for (int i = 0; i < 3; i++) {
      BaseItemConfig itemConfig = ServiceLocator.getConfigService().getItemConfig(itemKeys[i]);
      boolean[] soldItems = ServiceLocator.getProfileService().getProfile().getSoldItems();
      boolean isSold = soldItems != null && i < soldItems.length && soldItems[i];

      // Create item column
      Table itemColumn = new Table();

      // Item image button
      createItemSlot(i, itemConfig, itemColumn);

      // Always create price table but set visibility for sold items
      Table priceTable = new Table();

      Image coinIcon =
          new Image(
              ServiceLocator.getGlobalResourceService()
                  .getAsset("images/entities/currency/coins.png", Texture.class));
      coinIcon.setSize(COIN_ICON_SIZE * uiScale, COIN_ICON_SIZE * uiScale);
      priceTable.add(coinIcon).size(COIN_ICON_SIZE * uiScale).padRight(8f * uiScale);

      Label priceLabel = ui.subheading(String.valueOf(itemConfig.getCost()));
      priceTable.add(priceLabel);

      // Set visibility/opacity for sold items
      if (isSold) {
        priceTable.setVisible(false);
      }

      itemColumn.add(priceTable).padTop(25f * uiScale);

      // Add to main item table
      itemTable.add(itemColumn).padLeft(30f * uiScale).padRight(30f * uiScale);
    }

    rootTable.add(itemTable).center();
  }

  /**
   * Creates an item slot.
   *
   * @param slotIndex The index of the slot.
   * @param itemConfig The item configuration.
   * @param container The container to add the slot to.
   */
  private void createItemSlot(int slotIndex, BaseItemConfig itemConfig, Table container) {
    // Load the item texture
    Texture itemTex =
        ServiceLocator.getResourceService().getAsset(itemConfig.getAssetPath(), Texture.class);

    // Create background window using ui.createWindow
    Window backgroundWindow = ui.createWindow("");
    backgroundWindow.setSize(ITEM_SIZE * uiScale, ITEM_SIZE * uiScale);

    // Create ImageButton with the item texture
    ImageButton slot = new ImageButton(new TextureRegionDrawable(itemTex));
    slot.setSize(ITEM_SIZE * uiScale, ITEM_SIZE * uiScale);

    // Check if item is sold
    boolean[] soldItems = ServiceLocator.getProfileService().getProfile().getSoldItems();
    boolean isSold = soldItems != null && slotIndex < soldItems.length && soldItems[slotIndex];

    if (isSold) {
      // Create sold out overlay - hide the item image
      Label soldOutLabel = ui.text("Sold Out");
      soldOutLabel.setColor(Color.GRAY);
      soldOutLabel.setAlignment(com.badlogic.gdx.utils.Align.center);

      // Create stack with just background and sold out overlay (no item image)
      Stack itemStack = new Stack();
      itemStack.add(backgroundWindow);
      itemStack.add(soldOutLabel);

      container.add(itemStack).size(ITEM_SIZE * uiScale).row();
    } else {
      // Add click listener only if not sold
      slot.addListener(
          new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
              logger.debug("Item slot {} clicked ({})", slotIndex, itemConfig.getName());
              onItemSlotClicked(slotIndex, itemConfig.getAssetPath());
            }
          });

      // Create stack with background and item
      Stack itemStack = new Stack();
      itemStack.add(backgroundWindow);
      itemStack.add(slot);

      container.add(itemStack).size(ITEM_SIZE * uiScale).row();
    }

    // Store reference
    itemSlots[slotIndex] = slot;
  }

  /**
   * Handles the click event for an item slot.
   *
   * @param slotIndex The index of the slot.
   * @param itemTexture The texture of the item.
   */
  private void onItemSlotClicked(int slotIndex, String itemTexture) {
    logger.info("Selected item: {} from slot {}", itemTexture, slotIndex);
    showShopItemPopUp(itemKeys[slotIndex]);
    entity.getEvents().trigger("item_selected", slotIndex, itemTexture);
  }

  /**
   * Shows the shop item dialog using DialogService.
   *
   * @param itemKey The key of the item.
   */
  private void showShopItemPopUp(String itemKey) {
    BaseItemConfig itemConfig = ServiceLocator.getConfigService().getItemConfig(itemKey);
    String title = itemConfig.getName();
    String message =
        String.format("%s Do you want to purchase this item?", itemConfig.getDescription());
    ServiceLocator.getDialogService()
        .warning(
            title,
            message,
            dialog -> onPurchaseClicked(itemKey), // onConfirm callback
            null // onCancel callback (no action needed)
            );
  }

  /**
   * Handles the purchase event for an item. This method is called when the user confirms the
   * purchase in the dialog.
   *
   * @param itemKey The key of the item.
   */
  private void onPurchaseClicked(String itemKey) {

    BaseItemConfig itemConfig = ServiceLocator.getConfigService().getItemConfig(itemKey);
    if (ServiceLocator.getProfileService().getProfile().getWallet().getCoins()
        < itemConfig.getCost()) {
      String title = "Insufficient Funds";
      String message =
          String.format(
              "You need $%d to purchase %s, but you only have $%d.",
              itemConfig.getCost(),
              itemConfig.getName(),
              ServiceLocator.getProfileService().getProfile().getWallet().getCoins());
      ServiceLocator.getDialogService().error(title, message);
      return;
    }

    // Purchase the item
    ServiceLocator.getProfileService()
        .getProfile()
        .getWallet()
        .purchaseShopItem(itemConfig.getCost());
    ServiceLocator.getProfileService().getProfile().getInventory().addItem(itemKey);

    // Mark item as sold
    int slotIndex = -1;
    for (int i = 0; i < itemKeys.length; i++) {
      if (itemKeys[i].equals(itemKey)) {
        slotIndex = i;
        break;
      }
    }
    if (slotIndex >= 0) {
      ServiceLocator.getProfileService().getProfile().markItemAsSold(slotIndex);
    }

    // Play Item purchased sound
    float volume = ServiceLocator.getSettingsService().getSoundVolume();
    purchasedSound.play(volume);

    // Update statistics
    ServiceLocator.getProfileService()
        .getProfile()
        .getStatistics()
        .incrementStatistic("purchasesMade");
    ServiceLocator.getProfileService()
        .getProfile()
        .getStatistics()
        .incrementStatistic("itemsCollected");
    ServiceLocator.getProfileService()
        .getProfile()
        .getStatistics()
        .incrementStatistic("coinsSpent", itemConfig.getCost());

    logger.info("Successfully purchased: {}", itemConfig.getName());

    // Show success message
    ServiceLocator.getDialogService()
        .info(
            "Purchase Successful",
            String.format("You have successfully purchased %s!", itemConfig.getName()));

    entity.getEvents().trigger("resetShop");
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }

  @Override
  public void resize() {
    super.resize();
    recenterTable();
  }

  @Override
  public void dispose() {
    if (rootTable != null) {
      rootTable.clear();
    }
    super.dispose();
  }
}
