package com.csse3200.game.components.shop;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
  private Table mainTable;
  private ImageButton closeButton;

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
    // Create main table with fixed dimensions
    mainTable = new Table();
    mainTable.setSize(778f, 564f);
    mainTable.setPosition((stage.getWidth() - 778f) / 2f, (stage.getHeight() - 564f) / 2f);

    // Set shop-popup.png as background with fixed size
    Image backgroundImage =
        new Image(
            ServiceLocator.getGlobalResourceService()
                .getAsset("images/ui/shop-popup.png", Texture.class));
    backgroundImage.setSize(778f, 564f);
    backgroundImage.setPosition((stage.getWidth() - 778f) / 2f, (stage.getHeight() - 564f) / 2f);
    stage.addActor(backgroundImage);

    createCloseButton();
    createShopUI();
    stage.addActor(mainTable);
  }

  /** Creates the close button in the top-left corner. */
  private void createCloseButton() {
    // Create close button using close-icon.png
    closeButton =
        new ImageButton(
            new TextureRegionDrawable(
                ServiceLocator.getGlobalResourceService()
                    .getAsset("images/ui/close-icon.png", Texture.class)));

    // Position in top left with 20f padding
    closeButton.setSize(60f, 60f);
    closeButton.setPosition(
        20f, // 20f padding from left
        stage.getHeight() - 60f - 20f // 20f padding from top
        );

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

  /**
   * Sets the label's font color to white
   *
   * @param label The label to set the font color of
   */
  private void whiten(Label label) {
    Label.LabelStyle st = new Label.LabelStyle(label.getStyle());
    st.fontColor = Color.WHITE;
    label.setStyle(st);
  }

  /** Creates the main shop UI layout. */
  private void createShopUI() {
    mainTable.clear();
    Label titleLabel = ui.title("SHOP");
    //Label titleLabel = new Label("SHOP", skin, "title");
    titleLabel.setColor(Color.BLACK);
    titleLabel.setFontScale(1.5f);
    mainTable.add(titleLabel).colspan(3).center().padTop(-40).padBottom(34f).row();
    createTimerDisplay();
    mainTable.add(timerLabel).colspan(3).center().padBottom(30f).row();
    createItemSlots();
  }

  /** Creates and displays the reset timer. */
  private void createTimerDisplay() {
    timerLabel = new Label("", skin);
    whiten(timerLabel);
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

    // Calculate center position for the fixed 778x564 dimensions
    float centerX = (stage.getWidth() - 778f) / 2f;
    float centerY = (stage.getHeight() - 564f) / 2f;

    // Recenter the main table
    mainTable.setPosition(centerX, centerY);

    // Update close button position
    if (closeButton != null) {
      closeButton.setPosition(
          20f, // 20f padding from left
          stage.getHeight() - 60f - 20f // 20f padding from top
          );
    }

    // Recenter the background image if it exists
    for (Actor actor : stage.getActors()) {
      if (actor instanceof Image image && image.getWidth() == 778f && image.getHeight() == 564f) {
        image.setPosition(centerX, centerY);
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

      // Create item column
      Table itemColumn = new Table();

      // Item image button
      createItemSlot(i, itemConfig, itemColumn);

      Table priceTable = new Table();

      Image coinIcon =
          new Image(
              ServiceLocator.getGlobalResourceService()
                  .getAsset("images/entities/currency/coins.png", Texture.class));
      coinIcon.setSize(COIN_ICON_SIZE, COIN_ICON_SIZE);
      priceTable.add(coinIcon).size(COIN_ICON_SIZE).padRight(8f);

      Label priceLabel = new Label(String.valueOf(itemConfig.getCost()), skin);
      whiten(priceLabel);
      priceLabel.setFontScale(1.2f);
      priceTable.add(priceLabel);

      itemColumn.add(priceTable).padTop(25f);

      // Add to main item table
      itemTable.add(itemColumn).padLeft(30f).padRight(30f);
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
    // Load the item texture
    Texture itemTex =
        ServiceLocator.getResourceService().getAsset(itemConfig.getAssetPath(), Texture.class);

    // Create ImageButton with the item texture
    ImageButton slot = new ImageButton(new TextureRegionDrawable(itemTex));
    slot.setSize(ITEM_SIZE, ITEM_SIZE);

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
    container.add(slot).size(ITEM_SIZE).row();
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
  public void draw(SpriteBatch batch) {
    // Do nothing, handled by the stage
  }

  @Override
  public float getZIndex() {
    return Z_INDEX;
  }
}
