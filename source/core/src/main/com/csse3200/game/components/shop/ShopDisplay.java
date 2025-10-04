package com.csse3200.game.components.shop;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.csse3200.game.entities.configs.BaseItemConfig;
import com.csse3200.game.services.ConfigService;
import com.csse3200.game.services.ServiceLocator;
import com.csse3200.game.ui.UIComponent;
import com.csse3200.game.utils.ShopRandomizer;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import net.dermetfan.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** The shop display component. */
public class ShopDisplay extends UIComponent {
  private static final Logger logger = LoggerFactory.getLogger(ShopDisplay.class);
  private static final float Z_INDEX = 2f;

  // Base dimensions
  private static final float BACKGROUND_WIDTH = 778f;
  private static final float BACKGROUND_HEIGHT = 564f;
  private static final float ITEM_SIZE = 160f;
  private static final float COIN_ICON_SIZE = 40f;
  private static final float CLOSE_PAD = 20f;
  private static final float CLOSE_WIDTH = 200f;
  private static final float CLOSE_HEIGHT = 42f;
  private ImageButton[] itemSlots = new ImageButton[3];
  private String[] itemKeys = new String[3];
  private Label timerLabel;
  private Table mainTable;
  private TextButton closeButton;
  private Image backgroundImage;

  /** Creates a new ShopDisplay. */
  public ShopDisplay() {
    super();
    initializeShopItems();
  }

  @Override
  public void create() {
    super.create();
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
    // Get scaled background size
    float uiScale = ui.getUIScale();
    float backgroundWidth = BACKGROUND_WIDTH * uiScale;
    float backgroundHeight = BACKGROUND_HEIGHT * uiScale;

    // Create main table with fixed dimensions
    mainTable = new Table();
    mainTable.setSize(backgroundWidth, backgroundHeight);

    // Set shop-popup.png as background with fixed size
    backgroundImage =
        new Image(
            ServiceLocator.getGlobalResourceService()
                .getAsset("images/ui/shop-popup.png", Texture.class));
    backgroundImage.setSize(backgroundWidth, backgroundHeight);
    // backgroundImage.setPosition((stage.getWidth() - BACKGROUND_WIDTH) / 2f, (stage.getHeight() - BACKGROUND_HEIGHT) / 2f);
    stage.addActor(backgroundImage);

    createCloseButton();
    createShopUI();
    stage.addActor(mainTable);

    recenterTable();
  }

  /** Creates the close button in the top-left corner. */
  private void createCloseButton() {
    closeButton = ui.secondaryButton("Back", CLOSE_WIDTH);
    Pair<Float, Float> dimensions = ui.getScaledDimensions(CLOSE_WIDTH);
    closeButton.setSize(dimensions.getKey(), dimensions.getValue());

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

  /** Creates the main shop UI layout. */
  private void createShopUI() {
    mainTable.clear();
    mainTable.top().center();

    // Title
    mainTable.add(ui.title("Shop")).colspan(3).padTop(-40f * ui.getUIScale()).padBottom(34f).row();

    createTimerDisplay();
    mainTable.add(timerLabel).colspan(3).padBottom(30f).row();

    createItemSlots();
  }

  /** Creates and displays the reset timer. */
  private void createTimerDisplay() {
    timerLabel = ui.subtext("Reset in: --:--");
    updateTimer();
  }

  /** Updates the countdown timer display. */
  private void updateTimer() {
    if (timerLabel == null) return;

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime nextHour = now.truncatedTo(ChronoUnit.HOURS).plusHours(1);
    long minutesLeft = ChronoUnit.MINUTES.between(now, nextHour);
    long secondsLeft = ChronoUnit.SECONDS.between(now, nextHour) % 60;

    timerLabel.setText(String.format("Shop resets in: %02d:%02d", minutesLeft, secondsLeft));
  }

  /** Recenters the table and background to ensure they stay centered on screen. */
  private void recenterTable() {
    if (mainTable == null) return;

    float uiScale = ui.getUIScale();
    float backgroundWidth = BACKGROUND_WIDTH * uiScale;
    float backgroundHeight = BACKGROUND_HEIGHT * uiScale;

    // Calculate center position for the fixed 778x564 dimensions
    float centerX = (stage.getWidth() - backgroundWidth) / 2f;
    float centerY = (stage.getHeight() - backgroundHeight) / 2f;

    // Recenter the main table
    mainTable.setSize(backgroundWidth, backgroundHeight);
    mainTable.setPosition(centerX, centerY);

    // Update close button position
    if (closeButton != null) {
      closeButton.setPosition(
          CLOSE_PAD * uiScale, // 20f padding from left
          stage.getHeight() - closeButton.getHeight() - CLOSE_PAD * uiScale);
    }

    // Recenter the background image if it exists
    if (backgroundImage != null) {
        backgroundImage.setSize(backgroundWidth, backgroundHeight);
        backgroundImage.setPosition(centerX, centerY);
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
    float uiScale = ui.getUIScale();
    float coinSize = COIN_ICON_SIZE * uiScale;

    Table itemTable = new Table();

    for (int i = 0; i < 3; i++) {
      BaseItemConfig itemConfig = ServiceLocator.getConfigService().getItemConfig(itemKeys[i]);

      // Create item column
      Table itemColumn = new Table();

      // Item image button
      createItemSlot(i, itemConfig, itemColumn);

      Table priceTable = new Table();

      Image coinIcon =
          new Image(
              ServiceLocator.getGlobalResourceService()
                  .getAsset("images/entities/currency/coins.png", Texture.class));
      priceTable.add(coinIcon).size(coinSize).padRight(8f * uiScale);

      Label priceLabel = ui.text(String.valueOf(itemConfig.getCost()));
      priceTable.add(priceLabel);

      itemColumn.add(priceTable).padTop(25f * uiScale);

      // Add to main item table
      itemTable.add(itemColumn).padLeft(30f * uiScale).padRight(30f * uiScale);
    }

    mainTable.add(itemTable).center();
  }

  /**
   * Creates an item slot.
   *
   * @param slotIndex The index of the slot.
   * @param itemConfig The item configuration.
   * @param container The container to add the slot to.
   */
  private void createItemSlot(int slotIndex, BaseItemConfig itemConfig, Table container) {
    float uiScale = ui.getUIScale();
    float itemSize = ITEM_SIZE * uiScale;

    // Load the item texture
    Texture itemTex =
        ServiceLocator.getResourceService().getAsset(itemConfig.getAssetPath(), Texture.class);

    // Create ImageButton with the item texture
    ImageButton slot = new ImageButton(new TextureRegionDrawable(itemTex));
    slot.setSize(itemSize, itemSize);

    // Add click listener
    slot.addListener(
        new ChangeListener() {
          @Override
          public void changed(ChangeEvent changeEvent, Actor actor) {
            logger.debug("Item slot {} clicked ({})", slotIndex, itemConfig.getName());
            onItemSlotClicked(slotIndex, itemConfig.getAssetPath());
          }
        });

    // Store reference and add to container
    itemSlots[slotIndex] = slot;
    container.add(slot).size(itemSize).row();
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
    int itemCost = itemConfig.getCost();
    String title = itemConfig.getName();
    String message =
        String.format(
            "%s%n%nPrice: $%d%n%nDo you want to purchase this item?",
            itemConfig.getDescription(), itemCost);
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
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }
}
